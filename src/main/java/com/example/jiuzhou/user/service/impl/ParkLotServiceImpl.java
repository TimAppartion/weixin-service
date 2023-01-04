package com.example.jiuzhou.user.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.example.jiuzhou.common.Enum.ResultEnum;
import com.example.jiuzhou.common.utils.AbDateUtil;
import com.example.jiuzhou.common.utils.DateUtils;
import com.example.jiuzhou.common.utils.RateCalculate;
import com.example.jiuzhou.common.utils.Result;
import com.example.jiuzhou.user.mapper.*;
import com.example.jiuzhou.user.model.*;
import com.example.jiuzhou.user.model.fee.CarRateModel;
import com.example.jiuzhou.user.model.fee.RateCalculateModel;
import com.example.jiuzhou.user.model.fee.RateModel;
import com.example.jiuzhou.user.model.fee.Rates;
import com.example.jiuzhou.user.query.ParkLotQrPayQuery;
import com.example.jiuzhou.user.query.ParkLotQrQuery;
import com.example.jiuzhou.user.query.ParkPassageQuery;
import com.example.jiuzhou.user.service.ParkLotService;
import com.example.jiuzhou.user.service.PublicBasisService;
import com.example.jiuzhou.user.view.ParkLotQrView;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.weixin.sdk.api.PaymentApi;
import com.jfinal.weixin.sdk.kit.PaymentKit;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import javax.annotation.Resource;
import java.math.BigDecimal;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Appartion
 * @data 2022/12/29
 * 一入代码深似海，从此生活是路人
 */
@Service
@Slf4j
public class ParkLotServiceImpl implements ParkLotService {

    private static final Prop prop = PropKit.use("weixin.properties");
    private static String WxQrLotNotifyUrl=prop.get("QrLotNotifyUrl");
    private static String WxQrPassageNotifyUrl=prop.get("QrPassageNotifyUrl");

    @Resource
    private PublicBasisService publicBasisService;
    @Resource
    private AbpRatesMapper abpRatesMapper;
    @Resource
    private AbpBerthsecsMapper berthsecsMapper;

    @Resource
    private AbpParkadePassRecordMapper parkadePassRecordMapper;

    @Resource
    private AbpParkadePassageMapper parkadePassageMapper;

    @Resource
    private AbpParkadeAccessDetailMapper parkadeAccessDetailMapper;

    @Resource
    private AbpBusinessDetailMapper businessDetailMapper;

    @Resource
    private ParkPayRecordMapper parkPayRecordMapper;

    @Override
    public Result<?> parkLotQr(ParkLotQrQuery query) {
        ParkLotQrView accessDetail = parkadeAccessDetailMapper.getLotQrByPlateNumber(query.getPlateNumber(), query.getParkId());

        if (accessDetail == null) {
            return Result.error("未查询到订单");
        }

        BigDecimal money =this.moneyCount(accessDetail.getBerthsecId(),accessDetail.getCarType(),accessDetail.getCarPayTime(),accessDetail.getCarInTime(),accessDetail.getPlateNumber());
        if(money.compareTo(new BigDecimal(0))==0){
            accessDetail.setThisPay(money);
        }else{
            accessDetail.setThisPay(money.subtract(accessDetail.getReceivable()));
        }
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        accessDetail.setStopTime(DateUtils.getGabTimDes(sdf.format(accessDetail.getCarInTime()),sdf.format(new Date())));
        return Result.success(accessDetail);
    }

    @Override
    public Result<?> WXParkLotQrPay(ParkLotQrPayQuery query) {
        //订单信息
        AbpBusinessDetail businessDetail =  businessDetailMapper.getByGuid(query.getGuid());

        //订单id
        String device_info= UUID.randomUUID().toString().replace("-","");

        //读取支付配置信息
        AbpWeixinConfig config = publicBasisService.getConfigByTenantId(businessDetail.getTenantId());

        //支付下单组装请求参数
        Map<String,String> params = new HashMap<>();

        String out_trade_no=System.currentTimeMillis()+"";
        params.put("appid",config.getAppId());
        params.put("mch_id",config.getMch_id());
        params.put("body",config.getAppName());
        params.put("out_trade_no",out_trade_no);
        params.put("total_fee",String.valueOf((int) (Float.valueOf(query.getMoney().toString()) * 100)));


        params.put("attach", businessDetail.getGuid());//临时信息存订单guid
        params.put("spbill_create_ip","127.0.0.1");
        params.put("trade_type", PaymentApi.TradeType.JSAPI.name());
        params.put("nonce_str",System.currentTimeMillis()/1000+"");
        params.put("notify_url",query.getQrType()==1? WxQrLotNotifyUrl:WxQrPassageNotifyUrl);
        params.put("openid",query.getOpenId());
        params.put("device_info",device_info);
        params.put("sign", PaymentKit.createSign(params,config.getPaternerKey()));
        log.info("扫码下单支付信息:{}",params);
        String xmlResult = PaymentApi.pushOrder(params);
        log.info("扫码订单返回信息:{}",xmlResult);

        Map<String,String> result = PaymentKit.xmlToMap(xmlResult);
        String return_code = result.get("return_code");
        String return_msg = result.get("return_msg");
        if (StrKit.isBlank(return_code) || !"SUCCESS".equals(return_code)) {
            return Result.error(return_msg);
        }

        //组装返回前端参数
        String prepay_id = result.get("prepay_id");

        Map<String, String> packageParams = new HashMap<String, String>();
        packageParams.put("appId", config.getAppId());
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
    public Result<?> WXParkLotQrPayBack(Map<String, String> map) {
        log.info("微信扫码下单回调转换为map：{}",map);
        AbpBusinessDetail businessDetail =  businessDetailMapper.getByGuid(map.get("attach"));

        BigDecimal money = new BigDecimal(Double.valueOf(map.get("total_fee"))*0.01);
        //更新订单信息
        businessDetail.setStatus(6);
        businessDetail.setCarPayTime(new Date());
        businessDetail.setReceivable(businessDetail.getReceivable().add(money));
        businessDetailMapper.updateByPrimaryKey(businessDetail);

        //记录流水
        AbpParkadeAccessDetail accessDetail = parkadeAccessDetailMapper.getByGuid(businessDetail.getGuid());
        ParkPayRecord parkPayRecord = new ParkPayRecord();
        parkPayRecord.setTenantId(businessDetail.getTenantId());
        parkPayRecord.setMoney(money);
        parkPayRecord.setParkadeAccessId(accessDetail.getId());
        parkPayRecord.setOpenId(map.get("openid"));
        parkPayRecord.setPlateNumber(businessDetail.getPlateNumber());
        parkPayRecord.setCreationTime(new Date());
        parkPayRecordMapper.insert(parkPayRecord);
        return Result.success();
    }

    @Override
    public Result<?> passageQr(ParkPassageQuery query) {

        try{
            AbpParkadePassRecord passRecord =  parkadePassRecordMapper.getByPassageId(query.getPassageId());
            ParkLotQrView parkLotQrView;
            if(passRecord == null){
                if(StringUtils.isNotEmpty(query.getPlateNumber())){
                    parkLotQrView=parkadeAccessDetailMapper.getLotQrByPlateNumber(query.getPlateNumber(), query.getParkId());
                }else{
                    return Result.error(ResultEnum.MISS_DATA,"未查询到通道记录");
                }
            }else{
                 parkLotQrView = parkadeAccessDetailMapper.getLotQrByPlateNumber(query.getPlateNumber(), query.getParkId());
            }
            BigDecimal money =this.moneyCount(parkLotQrView.getBerthsecId(),parkLotQrView.getCarType(),parkLotQrView.getCarPayTime(),parkLotQrView.getCarInTime(),parkLotQrView.getPlateNumber());
            if(money.compareTo(new BigDecimal(0))==0){
                parkLotQrView.setThisPay(money);
            }else{
                parkLotQrView.setThisPay(money.subtract(parkLotQrView.getReceivable()));
            }
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            parkLotQrView.setStopTime(DateUtils.getGabTimDes(sdf.format(parkLotQrView.getCarInTime()),sdf.format(new Date())));
            return Result.success(parkLotQrView);
        }catch (Exception e){
            e.printStackTrace();
            return Result.error("未查询到订单信息");
        }
    }

    public  BigDecimal moneyCount(Integer BerthsecId,String carType,Date payTime,Date carInTime,String plateNumber){
        //费率参数填补
        AbpBerthsecs berthsec = berthsecsMapper.getById(BerthsecId);
        Rates rates = new Rates();
        AbpRates abpRates=abpRatesMapper.getById(berthsec.getRateId());
        rates.rateMode = JSONObject.parseObject(abpRates.getRatePDA(), RateModel.class);
        Map<String,CarRateModel> map = rates.rateMode.CarRateList.stream().collect(Collectors.toMap(CarRateModel::getCarType, k->k));
        //查询免费时间
        String freeTime = map.get(carType).getFreeTime();
        Date now = new Date();
        if(freeTime!=null && payTime!=null){
            //比较对应车辆类型的免费时间
            if (AbDateUtil.getOffectMinutes( payTime.getTime(),now.getTime()) < Integer.parseInt(freeTime)){

                return new BigDecimal(0);
            }
        }
        //计算应收
        RateCalculateModel calModel = new RateCalculateModel();
        calModel = RateCalculate.ProcessRateCalculate(
                carInTime, new Date(),
                2, plateNumber, rates, 1, new AbpMonthlyCars());
        BigDecimal money =new BigDecimal(String.format("%.2f",calModel.CalculateMoney));

        return money;
    }

}
