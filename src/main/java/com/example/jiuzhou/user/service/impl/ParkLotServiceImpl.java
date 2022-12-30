package com.example.jiuzhou.user.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.example.jiuzhou.common.utils.AbDateUtil;
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
import com.example.jiuzhou.user.service.ParkLotService;
import com.example.jiuzhou.user.service.PublicBasisService;
import com.example.jiuzhou.user.view.ParkLotQrView;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.weixin.sdk.api.PaymentApi;
import com.jfinal.weixin.sdk.kit.PaymentKit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


import javax.annotation.Resource;
import java.math.BigDecimal;

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
    private static String QRCodeNotifyUrl=prop.get("QRCodeNotifyUrl");

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

    @Override
    public Result<?> parkLotQr(ParkLotQrQuery query) {
        ParkLotQrView accessDetail = parkadeAccessDetailMapper.getLotQrByPlateNumber(query.getPlateNumber(), query.getParkId());

        if (accessDetail == null) {
            return Result.error("未查询到订单");
        }

        //费率计算
        AbpBerthsecs berthsec = berthsecsMapper.getById(accessDetail.getBerthsecId());
        Rates rates = new Rates();
        AbpRates abpRates=abpRatesMapper.getById(berthsec.getRateId());
        rates.rateMode = JSONObject.parseObject(abpRates.getRatePDA(), RateModel.class);
        Map<String,CarRateModel> map = rates.rateMode.CarRateList.stream().collect(Collectors.toMap(CarRateModel::getCarType, k->k));
        //查询免费时间
        String freeTime = map.get(accessDetail.getCarType()).getFreeTime();
        if(freeTime!=null){
            //比较对应车辆类型的免费时间
            if (AbDateUtil.getOffectMinutes(accessDetail.getCarPayTime().getTime(), new Date().getTime()) < Integer.parseInt(freeTime)){
                accessDetail.setThisPay(new BigDecimal(0));
                return Result.success(accessDetail);
            }
        }
        RateCalculateModel calModel = new RateCalculateModel();
        calModel = RateCalculate.ProcessRateCalculate(
                accessDetail.getCarInTime(), new Date(),
                2, accessDetail.getPlateNumber(), rates, 1, new AbpMonthlyCars());
        BigDecimal money =new BigDecimal(String.format("%.2f",calModel.CalculateMoney));

        accessDetail.setThisPay(money.multiply(accessDetail.getReceivable()));
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
        params.put("notify_url",QRCodeNotifyUrl);
        params.put("openid",query.getOpenId());
        params.put("device_info",device_info);
        params.put("sign", PaymentKit.createSign(params,config.getPaternerKey()));



        return Result.success();
    }


}
