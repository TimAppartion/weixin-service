package com.example.jiuzhou.user.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.jiuzhou.common.Enum.ResultEnum;
import com.example.jiuzhou.common.utils.DateTimeUtils;
import com.example.jiuzhou.common.utils.Result;
import com.example.jiuzhou.user.entity.PayAttach;
import com.example.jiuzhou.user.mapper.*;
import com.example.jiuzhou.user.model.*;
import com.example.jiuzhou.user.query.BalancePayQuery;
import com.example.jiuzhou.user.query.WeiXinPayQuery;
import com.example.jiuzhou.user.service.CouponsService;
import com.example.jiuzhou.user.service.PublicBasisService;
import com.example.jiuzhou.user.service.PushMessageService;
import com.example.jiuzhou.user.view.CouponsView;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.weixin.sdk.api.PaymentApi;

import com.jfinal.weixin.sdk.kit.PaymentKit;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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



    @Override
    public Result<?> WeiXinPay(WeiXinPayQuery query) {
        String device_info= UUID.randomUUID().toString().replace("-","");

        if(query.getType()==2){
            if(query.getIsMonthlyRenewal()==null){
                return Result.error(ResultEnum.MISS_DATA,"是否包月不可为空");
            }
            if(checkMonthlyCar(query.getIsMonthlyRenewal(),query.getPlateNumber(),query.getParkId())){
                return Result.error("包月失败");
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
            monthRecordMapper.insert(monthRecord);
        }

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
        params.put("attach", JsonKit.toJson(new PayAttach(out_trade_no,query.getType(),3, query.getGuid(),query.getCouponId()!=null?query.getCouponId().toString():"",query.getFee().toString()))+"|"+query.getType());
        params.put("spbill_create_ip",ip);
        params.put("trade_type", PaymentApi.TradeType.JSAPI.name());
        params.put("nonce_str",System.currentTimeMillis()/1000+"");
        params.put("notify_url",NOTIFYURL);
        params.put("openid",query.getOpenId());
        params.put("device_info",device_info);
        params.put("sign", PaymentKit.createSign(params,config.getPaternerKey()));

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
    public Result<?> weiXinCallBack(Map<String, String> params) {

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
        order.setAttach(params.get("attach"));
        order.setTime_end( params.get("time_end"));
        order.setCouresCount(0);
        order.setUrl("");

        ///////////////////////////// 以下是附加参数///////////////////////////////////
        //月租车唯一标识id
        String device_info=params.get("device_info");
        String attach = params.get("attach");
        String fee_type = params.get("fee_type");
        String is_subscribe = params.get("is_subscribe");
        String err_code = params.get("err_code");
        String err_code_des = params.get("err_code_des");
        Weixinorders weixinorders = weixinordersMapper.getByTransactionId(order.getTransaction_id());
        Boolean payKit=PaymentKit.verifyNotify(params, config.getPaternerKey());
        log.info("order:{},payKit:{}",weixinorders,payKit);
        // 注意重复通知的情况，同一订单号可能收到多次通知，请注意一定先判断订单状态
        if(weixinorders==null && payKit){
            if("SUCCESS".equals(order.getResult_code())){
                log.info("更新订单信息:{}",attach);
                int courseId = Integer.parseInt(attach.split("\\|")[1]);
                weixinordersMapper.insert(order);

                String guid = attach.replace("\"guid\":\"", "|").split("\\|")[1].split("\"")[0];
                String couponId = attach.replace("\"couponId\":\"", "|").split("\\|")[1].split("\"")[0];
                //优惠前的总金额
                String fee=	attach.replace("\"fee\":\"", "|").split("\\|")[1].split("\"")[0];

                //先使用优惠券
                if(StringUtils.isNotEmpty(couponId)){
                    couponsService.useCoupon(Integer.parseInt(couponId),2);
                }


                if(courseId == 4 && StringUtils.isNotEmpty(guid)){//处理在线补缴

                    ExtOtherAccount account = extOtherAccountMapper.getByUid(order.getUid());
                    String tf = String.valueOf(Double.valueOf(order.getTotal_fee())*0.01);
                    AbpDeductionRecords deductionRecords=new AbpDeductionRecords();
                    deductionRecords.setOtherAccountId(account.getId());
                    deductionRecords.setOperType(1);
                    deductionRecords.setMoney(new BigDecimal(tf));
                    deductionRecords.setPayStatus(1);
                    deductionRecords.setRemark("欠费补缴");
                    deductionRecords.setEmployeeId(config.getDepositCard());
                    deductionRecords.setTenantId(TENANTID);
                    deductionRecords.setCompanyId(MONECOMPANYID);
                    deductionRecords.setUserId(MONEUSERID);
                    deductionRecords.setCardNo(account.getCardNo());
                    deductionRecords.setBeginMoney(account.getWallet());
                    deductionRecords.setEndMoney(new BigDecimal(fee));
                    deductionRecords.setPayFrom(1);
                    abpDeductionRecordsMapper.insetOne(deductionRecords);
                }
                if(courseId == 5){//账号充值
                    log.info("自主结单，在线支付");
                    this.saveRecharge(new BigDecimal(fee),order.getUid());
                }
                if(courseId == 3) {//自主结单
                    log.info("自主结单");
                    this.carOut(guid,new BigDecimal(fee));
                    ExtOtherAccount account = extOtherAccountMapper.getByUid(order.getUid());
                    String tf = String.valueOf(Double.valueOf(order.getTotal_fee())*0.01);
                    AbpDeductionRecords deductionRecords=new AbpDeductionRecords();
                    deductionRecords.setOtherAccountId(account.getId());
                    deductionRecords.setOperType(1);
                    deductionRecords.setMoney(new BigDecimal(tf));
                    deductionRecords.setPayStatus(1);
                    deductionRecords.setRemark("停车缴费");
                    deductionRecords.setEmployeeId(config.getDepositCard());
                    deductionRecords.setTenantId(TENANTID);
                    deductionRecords.setCompanyId(MONECOMPANYID);
                    deductionRecords.setUserId(MONEUSERID);
                    deductionRecords.setCardNo(account.getCardNo());
                    deductionRecords.setBeginMoney(account.getWallet());
                    deductionRecords.setEndMoney(account.getWallet());
                    abpDeductionRecordsMapper.insetOne(deductionRecords);

                }
                if(courseId == 2){
                    MonthRecord record = monthRecordMapper.getById(device_info);
                    if(record!=null){
                        monthlyCar(order.getUid(),record.getPlateNumber(),record.getMonthly_total_fee(),record.getParkId(),record.getMonth(),record.getMonthlyType());
                    }

                    ExtOtherAccount account = extOtherAccountMapper.getByUid(order.getUid());
                    String tf = String.valueOf(Double.valueOf(order.getTotal_fee())*0.01);
                    AbpDeductionRecords deductionRecords=new AbpDeductionRecords();
                    deductionRecords.setOtherAccountId(account.getId());
                    deductionRecords.setOperType(1);
                    deductionRecords.setMoney(new BigDecimal(tf));
                    deductionRecords.setPayStatus(1);
                    deductionRecords.setRemark("包月缴费");
                    deductionRecords.setEmployeeId(config.getDepositCard());
                    deductionRecords.setTenantId(TENANTID);
                    deductionRecords.setCompanyId(MONECOMPANYID);
                    deductionRecords.setUserId(MONEUSERID);
                    deductionRecords.setCardNo(account.getCardNo());
                    deductionRecords.setBeginMoney(account.getWallet());
                    deductionRecords.setEndMoney(new BigDecimal(tf));
                    abpDeductionRecordsMapper.insetOne(deductionRecords);
                }
            }
        }
        return Result.success();
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
        abpRemoteGuidsMapper.insert(remoteGuids);
        //修改泊位表状态
        AbpBerths berths = abpBerthsMapper.getByGuid(businessDetail.getGuid());
        berths.setOutCarTime(time);
        berths.setBerthStatus("2");
        abpBerthsMapper.updateByPrimaryKey(berths);
    }

    @Override
    public String monthlyCar(String uid,String plateNumber, BigDecimal fee, Integer parkId, Integer month, String monthlyType) {
        log.info("车辆包月续费plateNumber:"+plateNumber+" fee:"+fee+" parkId:"+parkId+" month:"+month+" monthlyType:"+monthlyType);
        AbpMonthlyCars monthlyCars = abpMonthlyCarsMapper.getByPlateNumber(TENANTID,plateNumber);
        TUser tUser = tUserMapper.getByUid(uid);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if(monthlyCars==null){
            //新增包月车辆
            try{

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
                cars.setMonthyType( monthlyType);
                abpMonthlyCarsMapper.insert(cars);
                addMonthlyCarHistory(plateNumber,fee,parkId,sdf.parse(beginTime),sdf.parse(endTime),monthlyType,1,3);
            }catch (Exception e){
                log.error("新增包月失败");
                e.printStackTrace();
                return "error";
            }
            //推送包月消息
            pushMessageService.sendMonthlyCarRenewalMsg(tUser.getOpenId(),fee,plateNumber,false);
        }else{
            //包月续费
            try {
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
                abpMonthlyCarsMapper.updateByPrimaryKey(monthlyCars);
                addMonthlyCarHistory(plateNumber, fee, parkId, historyBeginTime, newEndTime, monthlyType, 0, 3);
            }catch (Exception e){
                log.error("包月续费失败");
                e.printStackTrace();
                return "error";
            }
            pushMessageService.sendMonthlyCarRenewalMsg(tUser.getOpenId(),fee,plateNumber,true);
        }
        return "success";
    }

    @Override
    public Result<?> balancePay(BalancePayQuery query) {
        //取系统配置信息
        AbpWeixinConfig config=JSONObject.parseObject(redisTemplate.opsForValue().get("config").toString(),AbpWeixinConfig.class);


        if(query.getCouponId()!=null){
            Result result=couponsService.canUseCoupon(query.getFee(),query.getUid(),query.getCouponId());
            if(result.getCode()!=200){
                return result;
            }
            query.setFee(new BigDecimal(result.getData().toString()));
        }
        //获取用户信息
        TUser tUser = tUserMapper.getByUid(query.getUid());
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

            account.setWallet(money);
        }

        if(query.getType()==2){
            //包月
            if(StringUtils.isEmpty(query.getPlateNumber()) || query.getParkId()==null || query.getIsMonthlyRenewal()!=null){
                return Result.error(ResultEnum.MISS_DATA);
            }
            deductionRecords.setOperType(1);
            deductionRecords.setRemark("包月缴费");
        }
        String tf = String.valueOf(Double.valueOf(query.getFee().toString()) * 0.01);
        if(query.getType()==3){
            this.carOut(query.getGuid(), query.getFee());

            deductionRecords.setMoney(new BigDecimal(tf));
            deductionRecords.setOperType(2);
            deductionRecords.setRemark("停车缴费");
        }
        if (query.getType()==4){
            deductionRecords.setMoney(new BigDecimal(tf));
            deductionRecords.setOperType(2);
            deductionRecords.setRemark("欠费补缴");
        }
        if(query.getType()==5){
            deductionRecords.setOperType(1);
            deductionRecords.setRemark("账号充值");
            saveRecharge(query.getFee(),query.getUid());
        }
        abpDeductionRecordsMapper.insetOne(deductionRecords);
        extOtherAccountMapper.updateByPrimaryKey(account);
        if(query.getCouponId()!=null){
            couponsService.useCoupon(query.getCouponId(), 2);
        }
        return Result.success();
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
        monthlyCarHistoryMapper.insert(monthlyCarHistory);
        //TODO
    }


    private void saveRecharge(BigDecimal money , String uid){
        AbpWeixinConfig config=JSONObject.parseObject(redisTemplate.opsForValue().get("config").toString(),AbpWeixinConfig.class);

        ExtOtherAccount account = extOtherAccountMapper.getByUid(uid);
        account.setWallet(account.getWallet().add(money));
        extOtherAccountMapper.updateByPrimaryKey(account);

        AbpDeductionRecords deductionRecords = new AbpDeductionRecords();
        deductionRecords.setOtherAccountId(account.getId());
        deductionRecords.setOperType(1);
        deductionRecords.setMoney(money);
        deductionRecords.setPayStatus(1);
        deductionRecords.setRemark("微信充值");
        deductionRecords.setEmployeeId(config.getDepositCard());
        deductionRecords.setTenantId(TENANTID);
        deductionRecords.setCompanyId(MONECOMPANYID);
        deductionRecords.setUserId(MONEUSERID);
        deductionRecords.setCardNo(account.getCardNo());
        deductionRecords.setBeginMoney(account.getWallet().subtract(money));
        deductionRecords.setEndMoney(account.getWallet());
        abpDeductionRecordsMapper.insetOne(deductionRecords);
    }


    private boolean checkMonthlyCar(boolean isMonthlyRenewal,String plateNumber,Integer parkId){
        AbpMonthlyCars abpMonthlyCars= abpMonthlyCarsMapper.getByPlateNumber(TENANTID,plateNumber);
        log.info("月卡信息:{},plateNumber:{}",abpMonthlyCars,plateNumber);
        if(isMonthlyRenewal){
            //续费
            Date now=new Date();
            if(abpMonthlyCars.getEndTime().getTime()>=now.getTime() && !abpMonthlyCars.getParkIds().equals(parkId)){
                return false;
            }
        }else{
            //购买月卡
            if(abpMonthlyCars!=null){
                return false;
            }
        }
        return true;
    }
}
