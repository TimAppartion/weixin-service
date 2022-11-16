package com.example.jiuzhou.user.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.jiuzhou.common.Enum.ResultEnum;
import com.example.jiuzhou.common.utils.DateTimeUtils;
import com.example.jiuzhou.common.utils.Result;
import com.example.jiuzhou.user.entity.PayAttach;
import com.example.jiuzhou.user.mapper.*;
import com.example.jiuzhou.user.model.*;
import com.example.jiuzhou.user.query.*;
import com.example.jiuzhou.user.service.CouponsService;
import com.example.jiuzhou.user.service.PublicBasisService;
import com.example.jiuzhou.user.service.PushMessageService;
import com.example.jiuzhou.user.view.CouponsView;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.weixin.sdk.api.PaymentApi;

import com.jfinal.weixin.sdk.kit.IpKit;
import com.jfinal.weixin.sdk.kit.PaymentKit;

import com.jfinal.weixin.sdk.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Appartion
 * @data 2022/10/17
 * 一入代码深似海，从此生活是路人
 */
@Slf4j
@Service
public class PublicBasisServiceImpl implements PublicBasisService {
    private static final Prop prop = PropKit.use("weixin.properties");
    private static Integer TENANTID=Integer.parseInt(prop.get("tenantId"));
    private static String APP_ID=prop.get("appId");
    private static String APP_SECRET=prop.get("appSecret");;
    private static String TOKEN=prop.get("token");;
    private static String NOTIFYURL=prop.get("notifyUrl");
    private static Integer MONEUSERID=Integer.parseInt(prop.get("moneUserid"));
    private static Integer MONECOMPANYID=Integer.parseInt(prop.get("monecompanyid"));

    @Autowired
    private RedisTemplate redisTemplate;

    @Resource
    private PushMessageService pushMessageService;

    @Resource
    private TUserMapper tUserMapper;

    @Resource
    private AbpMonthlyCarsMapper abpMonthlyCarsMapper;

    @Resource
    private MonthRecordMapper monthRecordMapper;

    @Resource
    private CouponsDetailsMapper couponsDetailsMapper;

    @Resource
    private CouponsService couponsService;

    @Resource
    private WeixinordersMapper weixinordersMapper;

    @Resource
    private ExtOtherAccountMapper extOtherAccountMapper;

    @Resource
    private AbpDeductionRecordsMapper abpDeductionRecordsMapper;

    @Resource
    private AbpBusinessDetailMapper abpBusinessDetailMapper;

    @Resource
    private AbpRemoteGuidsMapper abpRemoteGuidsMapper;

    @Resource
    private AbpBerthsMapper abpBerthsMapper;

    @Resource
    private AbpMonthlyCarHistoryMapper monthlyCarHistoryMapper;

    @Resource
    private OpinionMapper opinionMapper;

    @Resource
    private WeiXinPayAttachMapper weiXinPayAttachMapper;
    @Override
    public Result<?> WeiXinPay(WeiXinPayQuery query) {
        String device_info= UUID.randomUUID().toString().replace("-","");

        if(query.getType()==2){
            if(query.getIsMonthlyRenewal()==null){
                return Result.error(ResultEnum.MISS_DATA,"是否包月不可为空");
            }
            Result result=checkMonthlyCar(query.getIsMonthlyRenewal(),query.getPlateNumber(),query.getParkId());
            if(result.getCode()!=200){
                return result;
            }
            MonthRecord monthRecord = new MonthRecord();
            monthRecord.setId(device_info);
            monthRecord.setOpenId(query.getOpenId());
            monthRecord.setPlateNumber(query.getPlateNumber());
            monthRecord.setMonthly_total_fee(query.getFee());
            monthRecord.setParkId(query.getParkId());
            monthRecord.setMonth(query.getMonth());
            monthRecord.setMonthlyType(query.getMonthlyType());
            monthRecord.setUid(query.getUid());
            monthRecordMapper.insertOne(monthRecord);
        }

        WeiXinPayAttach weiXinPayAttach=new WeiXinPayAttach();
        //存储优惠前的金额
        weiXinPayAttach.setFee(query.getFee());
        //计算优惠券金额
        if(query.getCouponId()!=null){
            CouponsView couponsView = couponsDetailsMapper.getDetails(query.getCouponId());
            if(couponsView!=null){
                Result result=couponsService.canUseCoupon(query.getFee(), query.getUid(), query.getCouponId());
                if(result.getCode()!=200){
                    return result;
                }
                query.setFee(new BigDecimal(result.getData().toString()));
            }
        }

        //组装微信需要的参数
        AbpWeixinConfig config= JSONObject.parseObject(redisTemplate.opsForValue().get("config").toString(),AbpWeixinConfig.class);

        Map<String ,String> params = new HashMap<>();
        String out_trade_no=System.currentTimeMillis()+"";
        String ip = "127.0.0.1";


        params.put("appid",APP_ID);
        params.put("mch_id",config.getMch_id());
        params.put("body",config.getAppName());
        params.put("out_trade_no",out_trade_no);
        params.put("total_fee",String.valueOf((int) (Float.valueOf(query.getFee().toString()) * 100)));


        String payAttachId=UUID.randomUUID().toString().replace("-","");

        weiXinPayAttach.setId(payAttachId);
        weiXinPayAttach.setOrderId(out_trade_no);
        weiXinPayAttach.setType(query.getType());
        weiXinPayAttach.setCount(3);
        weiXinPayAttach.setGuid(query.getGuid());
        weiXinPayAttach.setCouponId(query.getCouponId()!=null?query.getCouponId():null);

        weiXinPayAttachMapper.insert(weiXinPayAttach);
//        new PayAttach(out_trade_no,query.getType(),3, query.getGuid(),query.getCouponId()!=null?query.getCouponId().toString():"",query.getFee().toString()))+"|"+query.getType()


        params.put("attach", payAttachId);
        params.put("spbill_create_ip",ip);
        params.put("trade_type", PaymentApi.TradeType.JSAPI.name());
        params.put("nonce_str",System.currentTimeMillis()/1000+"");
        params.put("notify_url",NOTIFYURL);
        params.put("openid",query.getOpenId());
        params.put("device_info",device_info);
        params.put("sign", PaymentKit.createSign(params,config.getPaternerKey()));

        log.info("微信下单支付:{}",params);
        String xmlResult = PaymentApi.pushOrder(params);
        log.info("微信订单生成返回参数:{}",xmlResult);

        Map<String,String> result = PaymentKit.xmlToMap(xmlResult);
        String return_code = result.get("return_code");
        String return_msg = result.get("return_msg");
        if (StrKit.isBlank(return_code) || !"SUCCESS".equals(return_code)) {
            return Result.error(return_msg);
        }

        //组装返回前端参数
        String prepay_id = result.get("prepay_id");

        Map<String, String> packageParams = new HashMap<String, String>();
        packageParams.put("appId", APP_ID);
        packageParams.put("timeStamp", System.currentTimeMillis() / 1000 + "");
        packageParams.put("nonceStr", System.currentTimeMillis() + "");
        packageParams.put("package", "prepay_id=" + prepay_id);
        packageParams.put("signType", "MD5");
        String packageSign = PaymentKit.createSign(packageParams, config.getPaternerKey());
        packageParams.put("paySign", packageSign.replaceAll("-", "").toLowerCase());

        return Result.success(packageParams);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> weiXinCallBack(Map<String, String> params) throws ParseException {

        log.info("微信支付返回参数params：{}",params);
        //读取系统配置
        AbpWeixinConfig config= JSONObject.parseObject(redisTemplate.opsForValue().get("config").toString(),AbpWeixinConfig.class);

        Weixinorders order = new Weixinorders();
        order.setAppid(params.get("appid"));
        // 商户订单号
        order.setOut_trade_no(params.get("out_trade_no"));
        order.setOpenId(params.get("openid"));
        order.setMch_id(params.get("mch_id"));
        order.setCash_fee(Integer.valueOf(params.get("cash_fee")));
        order.setTotal_fee( Integer.valueOf(params.get("total_fee")));
        order.setFee_type(params.get("fee_type"));
        order.setResult_code(params.get("result_code"));
        order.setErr_code(params.get("err_code"));
        order.setIs_subscribe(params.get("is_subscribe"));
        // 交易类型
        order.setTrade_type(params.get("trade_type"));
        // 付款银行
        order.setBank_type(params.get("bank_type"));
        // 微信支付订单号
        order.setTransaction_id(params.get("transaction_id"));
        String attachId = params.get("attach");
        WeiXinPayAttach payAttach=weiXinPayAttachMapper.getById(attachId);

        order.setAttach(JSONObject.toJSONString(payAttach));
        order.setTime_end( params.get("time_end"));
        order.setCouresCount(0);
        order.setUrl("");

        ///////////////////////////// 以下是附加参数///////////////////////////////////
        //月租车唯一标识id
        String device_info=params.get("device_info");

        String fee_type = params.get("fee_type");
        String is_subscribe = params.get("is_subscribe");
        String err_code = params.get("err_code");
        String err_code_des = params.get("err_code_des");
        Weixinorders weixinorders = weixinordersMapper.getByTransactionId(order.getTransaction_id());
        Boolean payKit=PaymentKit.verifyNotify(params, config.getPaternerKey());
        log.info("微信回调返回参数:{},payKit:{}",weixinorders,payKit);
        // 注意重复通知的情况，同一订单号可能收到多次通知，请注意一定先判断订单状态
        if(weixinorders==null && payKit){
            if("SUCCESS".equals(order.getResult_code())){
                log.info("更新订单信息:{}",payAttach);
                int courseId = payAttach.getType();
                order.setCouresId(courseId);
                order.setTenantId(TENANTID);
                TUser tUser=tUserMapper.getByOpenId(order.getOpenId());
                order.setUid(tUser.getUid());
                log.info("新增订单参数:{}",order);
                weixinordersMapper.insertOne(order);

                String guid = payAttach.getGuid();
                Integer couponId = payAttach.getCouponId();
                //优惠前的总金额
                BigDecimal fee=	payAttach.getFee();

                //先使用优惠券
                if(couponId!=null){
                    couponsService.useCoupon(couponId,2);
                }


                if(courseId == 4 && StringUtils.isNotEmpty(guid)){//处理在线补缴
                    this.payment(order.getTotal_fee(),guid,config.getDepositCard(),order.getUid(),fee);
                }
                if(courseId == 5){//账号充值
                    log.info("账号充值");
                    this.saveRecharge(fee,order.getUid(),1);
                }
                if(courseId == 3) {//自主结单
                    log.info("自主结单");
                    this.statement(guid,fee,order.getUid(),order.getTotal_fee(),config.getDepositCard(),1);
                }
                if(courseId == 2){
                    log.info("包月缴费");
                    this.monthPay(device_info, order.getUid(), order.getTotal_fee(),config.getDepositCard(),1);
                }
            }
        }
        return Result.success();
    }

    @Override
    public void monthPay(String device_info,String uid,Integer total_fee,Integer depositCard,Integer payFrom) throws ParseException {
        MonthRecord record = monthRecordMapper.getById(device_info);
        if(record!=null){
            monthlyCar(uid,record.getPlateNumber(),record.getMonthly_total_fee(),record.getParkId(),record.getMonth(),record.getMonthlyType());
        }

        ExtOtherAccount account = extOtherAccountMapper.getByUid(uid);
        String tf = String.valueOf(Double.valueOf(total_fee)*0.01);
        AbpDeductionRecords deductionRecords=new AbpDeductionRecords();
        deductionRecords.setOtherAccountId(account.getId());
        deductionRecords.setOperType(1);
        deductionRecords.setMoney(new BigDecimal(tf));
        deductionRecords.setPayStatus(1);
        deductionRecords.setRemark("包月缴费");
        deductionRecords.setEmployeeId(depositCard);
        deductionRecords.setTenantId(TENANTID);
        deductionRecords.setCompanyId(MONECOMPANYID);
        deductionRecords.setUserId(MONEUSERID);
        deductionRecords.setCardNo(account.getCardNo());
        deductionRecords.setBeginMoney(account.getWallet());
        deductionRecords.setEndMoney(new BigDecimal(tf));
        deductionRecords.setPayFrom(payFrom);
        deductionRecords.setInTime(new Date());
        abpDeductionRecordsMapper.insetOne(deductionRecords);
    }

    @Override
    public void statement(String guid,BigDecimal fee,String uid,Integer total_fee,Integer depositCard,Integer payFrom){
        this.carOut(guid,fee);
        ExtOtherAccount account = extOtherAccountMapper.getByUid(uid);
        String tf = String.valueOf(Double.valueOf(total_fee)*0.01);
        AbpDeductionRecords deductionRecords=new AbpDeductionRecords();
        deductionRecords.setOtherAccountId(account.getId());
        deductionRecords.setOperType(1);
        deductionRecords.setMoney(new BigDecimal(tf));
        deductionRecords.setPayStatus(1);
        deductionRecords.setRemark("停车缴费");
        deductionRecords.setEmployeeId(depositCard);
        deductionRecords.setTenantId(TENANTID);
        deductionRecords.setCompanyId(MONECOMPANYID);
        deductionRecords.setUserId(MONEUSERID);
        deductionRecords.setCardNo(account.getCardNo());
        deductionRecords.setBeginMoney(account.getWallet());
        deductionRecords.setEndMoney(account.getWallet());
        deductionRecords.setPayFrom(payFrom);
        deductionRecords.setInTime(new Date());
        abpDeductionRecordsMapper.insetOne(deductionRecords);

    }

    @Override
    public void payment(Integer totalFee,String guid,Integer depositCard,String uid,BigDecimal fee){
        String[] guids=guid.split(",");
        for(int i=0;i<guids.length;i++){
            updateOrder(guids[i],new BigDecimal(totalFee));
        }

        ExtOtherAccount account = extOtherAccountMapper.getByUid(uid);
        String tf = String.valueOf(Double.valueOf(totalFee)*0.01);
        AbpDeductionRecords deductionRecords=new AbpDeductionRecords();
        deductionRecords.setOtherAccountId(account.getId());
        deductionRecords.setOperType(1);
        deductionRecords.setMoney(new BigDecimal(tf));
        deductionRecords.setPayStatus(1);
        deductionRecords.setRemark("欠费补缴");
        deductionRecords.setEmployeeId(depositCard);
        deductionRecords.setTenantId(TENANTID);
        deductionRecords.setCompanyId(MONECOMPANYID);
        deductionRecords.setUserId(MONEUSERID);
        deductionRecords.setCardNo(account.getCardNo());
        deductionRecords.setBeginMoney(account.getWallet());
        deductionRecords.setEndMoney(fee);
        deductionRecords.setPayFrom(1);
        deductionRecords.setInTime(new Date());
        abpDeductionRecordsMapper.insetOne(deductionRecords);
    }

    @Override
    public void carOut(String guid,BigDecimal money){
        AbpWeixinConfig config=JSONObject.parseObject(redisTemplate.opsForValue().get("config").toString(),AbpWeixinConfig.class);


        AbpBusinessDetail businessDetail=abpBusinessDetailMapper.getByGuid(guid);
        Date time= new Date();
        //出场记录
        businessDetail.setStatus(2);
        businessDetail.setMoney(money);
        businessDetail.setFactReceive(money);
        businessDetail.setCarOutTime(time);
        businessDetail.setCarPayTime(time);
        businessDetail.setOutOperaId(config.getOnlineOrdering());
        businessDetail.setOutDeviceCode("WeixinClient");
        businessDetail.setPayStatus(3);
        businessDetail.setIsPay(1);
        businessDetail.setFeeType(1);
        businessDetail.setStopTime(10);
        businessDetail.setOutBatchNo("0");
        abpBusinessDetailMapper.updateByPrimaryKey(businessDetail);

        AbpRemoteGuids remoteGuids=new AbpRemoteGuids();
        remoteGuids.setBusinessDetailGuid(businessDetail.getGuid());
        remoteGuids.setCreationTime(time);
        remoteGuids.setIsActive(0);
        remoteGuids.setBerthsecId(businessDetail.getBerthsecId());
        remoteGuids.setCreatorUserId(0);
        abpRemoteGuidsMapper.insertOne(remoteGuids);
        //修改泊位表状态
        AbpBerths berths = abpBerthsMapper.getByGuid(businessDetail.getGuid());
        if(berths!=null){
            berths.setOutCarTime(time);
            berths.setBerthStatus("2");
            abpBerthsMapper.updateByPrimaryKey(berths);
        }

    }

    @Override
    public void monthlyCar(String uid,String plateNumber, BigDecimal fee, Integer parkId, Integer month, String monthlyType) throws ParseException {
        log.info("车辆包月续费plateNumber:"+plateNumber+" fee:"+fee+" parkId:"+parkId+" month:"+month+" monthlyType:"+monthlyType);
        AbpMonthlyCars monthlyCars = abpMonthlyCarsMapper.getByPlateNumber(TENANTID,plateNumber);
        TUser tUser = tUserMapper.getByUid(uid);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if(monthlyCars==null){
            //新增包月车辆

            Date nowDate = new Date();
            String beginTime = df.format(nowDate) + " 00:00:00";
            String endTime = df.format(DateTimeUtils.stepMonth(nowDate, month)) + " 00:00:00";


            AbpMonthlyCars cars=new AbpMonthlyCars();
            cars.setVehicleOwner(StringUtils.isNotEmpty(tUser.getNickName())? tUser.getNickName():plateNumber);
            cars.setTelphone(tUser.getTel());
            cars.setPlateNumber(plateNumber);
            cars.setMoney(fee);
            cars.setParkIds(parkId.toString());
            cars.setBeginTime(sdf.parse(beginTime));
            cars.setEndTime(sdf.parse(endTime));
            cars.setCarType(0);
            cars.setVersion(1);
            cars.setIsDeleted(0);
            cars.setTenantId(TENANTID);
            cars.setCompanyId(MONECOMPANYID);
            cars.setCreatorUserId(MONEUSERID);
            cars.setCreationTime(new Date());
            cars.setMonthyType( monthlyType);
            cars.setIsSms(0);
            abpMonthlyCarsMapper.insertOne(cars);
            addMonthlyCarHistory(plateNumber,fee,parkId,sdf.parse(beginTime),sdf.parse(endTime),monthlyType,1,3);
            //推送包月消息
            pushMessageService.sendMonthlyCarRenewalMsg(tUser.getOpenId(),fee,plateNumber,false);
        }else{
            //包月续费
            monthlyCars.setVehicleOwner(StringUtils.isNotEmpty(tUser.getNickName()) ? tUser.getNickName() : plateNumber);
            fee = fee.setScale(2, BigDecimal.ROUND_HALF_UP);
            fee = fee.add(monthlyCars.getMoney());
            monthlyCars.setMoney(fee);
            monthlyCars.setParkIds(parkId.toString());


            Date endTime = monthlyCars.getEndTime();
            Date historyBeginTime;
            Date newEndTime;
            if (endTime.getTime() >= new Date().getTime()) {
                // 包月未到期
                newEndTime = DateTimeUtils.stepMonth(endTime, month);
                historyBeginTime = endTime;
            } else {
                // 包月到期
                historyBeginTime = new Date();
                newEndTime = DateTimeUtils.stepMonth(new Date(),month);
            }
            monthlyCars.setEndTime(newEndTime);
            monthlyCars.setVersion(monthlyCars.getVersion() + 1);
            monthlyCars.setLastModificationTime(new Date());
            monthlyCars.setLastModifierUserId(MONEUSERID);
            monthlyCars.setIsSms(0);
            abpMonthlyCarsMapper.updateByPrimaryKey(monthlyCars);
            addMonthlyCarHistory(plateNumber, fee, parkId, historyBeginTime, newEndTime, monthlyType, 0, 3);

            pushMessageService.sendMonthlyCarRenewalMsg(tUser.getOpenId(),fee,plateNumber,true);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> balancePay(BalancePayQuery query) throws ParseException {
        //取系统配置信息
        AbpWeixinConfig config=JSONObject.parseObject(redisTemplate.opsForValue().get("config").toString(),AbpWeixinConfig.class);
        if(query.getCouponId()!=null){
            Result result=couponsService.canUseCoupon(query.getFee(),query.getUid(),query.getCouponId());
            if(result.getCode()!=200){
                return result;
            }
            query.setFee(new BigDecimal(result.getData().toString()));
        }
        //获取用户余额信息
        ExtOtherAccount account = extOtherAccountMapper.getByUid(query.getUid());
        BigDecimal wallet = account.getWallet();
        BigDecimal money = wallet.subtract(query.getFee());


        //增加消费记录扣除余额
        AbpDeductionRecords deductionRecords =  new AbpDeductionRecords();
        if(wallet.compareTo(new BigDecimal(0))>=0&&
            money.compareTo(new BigDecimal(0))>=0){
            deductionRecords.setOtherAccountId(account.getId());
            deductionRecords.setMoney(query.getFee());
            deductionRecords.setPayStatus(1);
            deductionRecords.setEmployeeId(config.getDepositCard());
            deductionRecords.setTenantId(TENANTID);
            deductionRecords.setCompanyId(MONECOMPANYID);
            deductionRecords.setUserId(MONEUSERID);
            deductionRecords.setCardNo(account.getCardNo());
            deductionRecords.setBeginMoney(account.getWallet());
            deductionRecords.setEndMoney(money);
            deductionRecords.setPayFrom(3);
            deductionRecords.setInTime(new Date());

            account.setWallet(money);
        }

        if(query.getType()==2){
            //包月
            if(StringUtils.isEmpty(query.getPlateNumber()) || query.getParkId()==null || query.getIsMonthlyRenewal()==null){
                return Result.error(ResultEnum.MISS_DATA);
            }
            Result result=checkMonthlyCar(query.getIsMonthlyRenewal(),query.getPlateNumber(),query.getParkId());
            if(result.getCode()!=200){
                return result;
            }
            deductionRecords.setOperType(1);
            deductionRecords.setRemark("包月缴费");
            monthlyCar(query.getUid(),query.getPlateNumber(),query.getFee(),query.getParkId(), query.getMonth(), query.getMonthlyType());
        }
        String tf = String.valueOf(Double.valueOf(query.getFee().toString()) * 0.01);
        if(query.getType()==3){
            this.carOut(query.getGuid(), query.getFee());

            deductionRecords.setMoney(new BigDecimal(tf));
            deductionRecords.setOperType(2);
            deductionRecords.setRemark("停车缴费");
        }
        if (query.getType()==4){
            if(StringUtils.isEmpty(query.getGuid())){
                return Result.error(ResultEnum.MISS_DATA);
            }
            String[] guids=query.getGuid().split(",");
            for(int i=0;i<guids.length;i++){
                updateOrder(guids[i],new BigDecimal(tf) );
            }

            deductionRecords.setMoney(new BigDecimal(tf));
            deductionRecords.setOperType(2);
            deductionRecords.setRemark("欠费补缴");
        }
        if(query.getType()==5){
            deductionRecords.setOperType(1);
            deductionRecords.setRemark("账号充值");
            this.saveRecharge(query.getFee(),query.getUid(),3);
        }
        abpDeductionRecordsMapper.insetOne(deductionRecords);
        extOtherAccountMapper.updateByPrimaryKey(account);
        if(query.getCouponId()!=null){
            couponsService.useCoupon(query.getCouponId(), 2);
        }
        return Result.success();
    }

    @Override
    public Result<?> insertOpinion(OpinionQuery query) {
        Opinion opinion= new Opinion();
        opinion.setUid(query.getUid());
        opinion.setType(query.getType());
        opinion.setCreateTime(new Date());
        opinion.setContext(query.getContext());
        opinion.setStatus(1);
        opinionMapper.insertOne(opinion);
        return Result.success();
    }

    @Override
    public Result<?> saveOnlineCar(SaveOnlineCarQuery query) {
        AbpBusinessDetail businessDetail=abpBusinessDetailMapper.getByGuid(query.getGuid());
        return Result.success(pushMessageService.sendMsgOrder(query.getOpenId(),businessDetail.getMoney(),businessDetail.getPlateNumber(),businessDetail.getBerthNumber(),StopTimes(businessDetail.getStopTime())));

    }

    @Override
    public Result<?> onlineCarSendMsg(OnlineCarSendMsgQuery query) {
        TUser tUser=tUserMapper.getByOpenId(query.getOpenId());
        String stopTime=query.getDay()+"天"+query.getHours()+"小时"+query.getMinute()+"分";
        AbpBusinessDetail businessDetail=abpBusinessDetailMapper.getByGuid(query.getGuid());
        return Result.success(pushMessageService.SendMsgOrderOut(query.getOpenId(),businessDetail.getPlateNumber(),businessDetail.getBerthNumber(),"微信支付",businessDetail.getMoney(),stopTime,tUser.getTel(),businessDetail.getCarOutTime()));
    }


    public void updateOrder(String guid,BigDecimal money){
        //取系统配置信息
        AbpWeixinConfig config=JSONObject.parseObject(redisTemplate.opsForValue().get("config").toString(),AbpWeixinConfig.class);

        log.info("更新订单信息：{}",guid);
        AbpBusinessDetail businessDetail=abpBusinessDetailMapper.getByGuid(guid);
        businessDetail.setStatus(4);
        businessDetail.setElectronicOrderid(businessDetail.getId());
        businessDetail.setRepayment(money);
        businessDetail.setCarRepaymentTime(new Date());
        businessDetail.setIsEscapePay(1);
        businessDetail.setEscapePayStatus(4);
        businessDetail.setEscapeCardNo("0");
        businessDetail.setEscapeDeviceCode("weixinclient");
        businessDetail.setPaymentType(3);
        businessDetail.setEscapeOperaId(config.getRecoverMoneny());
        businessDetail.setCarPayTime(new Date());
        abpBusinessDetailMapper.updateByPrimaryKey(businessDetail);
    }
    /**
     * 添加历史记录
     * @param plateNumber
     * @param fee
     * @param parkId
     * @param beginTime
     * @param endTime
     * @param monthlyType
     * @param status
     * @param payStatus
     */
    public void addMonthlyCarHistory(String plateNumber,BigDecimal fee,Integer parkId,Date beginTime,Date endTime,String monthlyType,Integer status,Integer payStatus){
        AbpMonthlyCars cars = abpMonthlyCarsMapper.getByPlateNumber(TENANTID,plateNumber);
        AbpMonthlyCarHistory monthlyCarHistory=new AbpMonthlyCarHistory();
        monthlyCarHistory.setMonthlyCarId(cars.getId());
        monthlyCarHistory.setMoney(fee);
        monthlyCarHistory.setParkIds(String.valueOf(parkId));
        monthlyCarHistory.setBeginTime(beginTime);
        monthlyCarHistory.setEndTime( endTime);
        // 1：开通 0：续费
        monthlyCarHistory.setStatus(status);
        monthlyCarHistory.setCreationTime(new Date());
        monthlyCarHistory.setCreatorUserId(MONEUSERID);
        monthlyCarHistory.setMonthyType(monthlyType);
        // 0：未知 2：刷卡支付 3：微信支付
        monthlyCarHistory.setPayStatus(payStatus);
        monthlyCarHistoryMapper.insertOne(monthlyCarHistory);
    }


    @Override
    public void saveRecharge(BigDecimal money , String uid,Integer payFrom){
        AbpWeixinConfig config=JSONObject.parseObject(redisTemplate.opsForValue().get("config").toString(),AbpWeixinConfig.class);

        ExtOtherAccount account = extOtherAccountMapper.getByUid(uid);
        account.setWallet(account.getWallet().add(money));
        extOtherAccountMapper.updateByPrimaryKey(account);

        AbpDeductionRecords deductionRecords = new AbpDeductionRecords();
        deductionRecords.setOtherAccountId(account.getId());
        deductionRecords.setOperType(1);
        deductionRecords.setMoney(money);
        deductionRecords.setPayStatus(1);
        deductionRecords.setRemark(payFrom==1?"微信充值":"支付宝充值");
        deductionRecords.setEmployeeId(config.getDepositCard());
        deductionRecords.setTenantId(TENANTID);
        deductionRecords.setCompanyId(MONECOMPANYID);
        deductionRecords.setUserId(MONEUSERID);
        deductionRecords.setCardNo(account.getCardNo());
        deductionRecords.setBeginMoney(account.getWallet().subtract(money));
        deductionRecords.setEndMoney(account.getWallet());
        deductionRecords.setInTime(new Date());
        deductionRecords.setPayFrom(payFrom);
        abpDeductionRecordsMapper.insetOne(deductionRecords);
    }


    @Override
    public Result<?> checkMonthlyCar(boolean isMonthlyRenewal,String plateNumber,Integer parkId){
        AbpMonthlyCars abpMonthlyCars= abpMonthlyCarsMapper.getByPlateNumber(TENANTID,plateNumber);
        log.info("月卡信息:{},plateNumber:{}",abpMonthlyCars,plateNumber);
        if(isMonthlyRenewal){
            //续费
            Date now=new Date();
            if(abpMonthlyCars.getEndTime().getTime()>=now.getTime() && !abpMonthlyCars.getParkIds().equals(parkId.toString())){
                return Result.error(ResultEnum.ERROR,"该车辆包月未到期，不能更改路段");
            }
        }else{
            //购买月卡
            if(abpMonthlyCars!=null){
                return Result.error(ResultEnum.ERROR,"该车辆已购买月卡，请勿重复购买");
            }
        }
        return Result.success();
    }

    @Override
    public Result<?> scanCode(WeiXinScanCodeQuery query) {
        AbpWeixinConfig config= JSONObject.parseObject(redisTemplate.opsForValue().get("config").toString(),AbpWeixinConfig.class);
        String out_trade_no=UUID.randomUUID().toString().replace("-","");

        Map<String, String> params = new HashMap<String, String>();
        params.put("appid", APP_ID);
        params.put("mch_id", config.getMch_id());
        params.put("device_info", "dingdingtc");// 终端设备号
        params.put("nonce_str", System.currentTimeMillis() / 1000 + "");
        params.put("body", "在线支付");
        // params.put("detail", "json字符串");//非必须
        params.put("out_trade_no", out_trade_no);
        int price = ((int) (Float.valueOf(query.getTotal_fee().toString()) * 100));
        params.put("total_fee", price + "");
        String ip = "127.0.0.1";

        params.put("spbill_create_ip", ip);
        params.put("auth_code", query.getAuth_code());

        String sign = PaymentKit.createSign(params, config.getPaternerKey());
        params.put("sign", sign);

        String xmlResult = HttpUtils.post("https://api.mch.weixin.qq.com/pay/micropay", PaymentKit.toXml(params));
        // 同步返回结果
        log.info("微信扫码支付返回结果：{}",xmlResult);

        Map<String, String> result = PaymentKit.xmlToMap(xmlResult);
        String return_code = result.get("return_code");
        if (StrKit.isBlank(return_code) || !"SUCCESS".equals(return_code)) {
            // 通讯失败
            String err_code = result.get("err_code");
            // 用户支付中，需要输入密码
            if (err_code.equals("USERPAYING")) {
                // 等待5秒后调用【查询订单API】https://pay.weixin.qq.com/wiki/doc/api/micropay.php?chapter=9_2
            }
            return Result.error(ResultEnum.ERROR,"通讯失败",xmlResult);
        }

        String result_code = result.get("result_code");
        if (StrKit.isBlank(result_code) || !"SUCCESS".equals(result_code)) {
            // 支付失败
            return Result.error(ResultEnum.ERROR,"支付失败",xmlResult);
        }
        // 支付成功
        Map<String, String> resultparams = PaymentKit.xmlToMap(xmlResult);

        Weixinorders order = new Weixinorders();
        order.setAppid( resultparams.get("appid"));
        order.setOut_trade_no(out_trade_no);
        order.setOpenId(resultparams.get("openid"));
        // 商户号
        order.setMch_id(resultparams.get("mch_id"));
        // 现金支付金额
        order.setCash_fee(Integer.valueOf(resultparams.get("cash_fee")));
        // 总金额
        order.setTotal_fee(Integer.valueOf(resultparams.get("total_fee")));
        order.setFee_type(resultparams.get("fee_type"));
        order.setResult_code( resultparams.get("result_code"));
        order.setErr_code(resultparams.get("err_code"));
        order.setIs_subscribe(resultparams.get("is_subscribe"));
        order.setTrade_type(resultparams.get("trade_type"));
        order.setBank_type(resultparams.get("bank_type"));
        order.setTransaction_id(resultparams.get("transaction_id"));

        String attach = JsonKit.toJson(new PayAttach(out_trade_no,query.getPayType(), 3, query.getGuid())) + "|" + query.getPayType();
        order.setAttach(attach);
        order.setTime_end(resultparams.get("time_end"));
        order.setCouresCount(0);
        order.setCouresId(query.getPayType());
        order.setUrl("");

        // 注意重复通知的情况，同一订单号可能收到多次通知，请注意一定先判断订单状态
        // 避免已经成功、关闭、退款的订单被再次更新
        Weixinorders hisOrder = weixinordersMapper.getByTransactionId(resultparams.get("transaction_id"));
        if (hisOrder == null) {
            weixinordersMapper.insertOne(order);
        }

        resultparams.put("out_trade_no",out_trade_no);

        return Result.success(resultparams);
    }

    @Override
    public Result<?> weiXinWriteOrder(WeiXinWriteOrderQuery query) {
        Map<String, String> resultparams = PaymentKit.xmlToMap(query.getXmlResult());
        Weixinorders order = new Weixinorders();
        order.setAppid( resultparams.get("appid"));
        order.setOut_trade_no(resultparams.get("out_trade_no"));
        order.setOpenId(resultparams.get("openid"));
        // 商户号
        order.setMch_id(resultparams.get("mch_id"));
        // 现金支付金额
        order.setCash_fee(Integer.valueOf(resultparams.get("cash_fee")));
        // 总金额
        order.setTotal_fee(Integer.valueOf(resultparams.get("total_fee")));
        order.setFee_type(resultparams.get("fee_type"));
        order.setResult_code( resultparams.get("result_code"));
        order.setErr_code(resultparams.get("err_code"));
        order.setIs_subscribe(resultparams.get("is_subscribe"));
        order.setTrade_type(resultparams.get("trade_type"));
        order.setBank_type(resultparams.get("bank_type"));
        order.setTransaction_id(resultparams.get("transaction_id"));

        String attach = JsonKit.toJson(new PayAttach(resultparams.get("out_trade_no"),query.getPayType(), 3, query.getGuid())) + "|" + query.getPayType();
        order.setAttach(attach);
        order.setTime_end(resultparams.get("time_end"));
        order.setCouresCount(0);
        order.setCouresId(query.getPayType());
        order.setUrl("");

        // 注意重复通知的情况，同一订单号可能收到多次通知，请注意一定先判断订单状态
        // 避免已经成功、关闭、退款的订单被再次更新
        Weixinorders hisOrder = weixinordersMapper.getByTransactionId(resultparams.get("transaction_id"));
        if (hisOrder == null) {
            weixinordersMapper.insertOne(order);
        }


        return Result.success(query.getXmlResult());
    }


    private String StopTimes(int StopTime) {
        if (StopTime == 0)
            return "0分钟";
        String dateDiff = "";
        if (StopTime >= 1440)// 判断是否大于24小时
        {
            dateDiff = (StopTime / 1400) + "天" + ((StopTime % 1400) / 60) + "小时" + ((StopTime % 1400) % 60) + "分钟";
        } else {
            dateDiff = (StopTime / 60) + "小时" + (StopTime % 60) + "分钟";
        }
        return dateDiff;
    }
}
