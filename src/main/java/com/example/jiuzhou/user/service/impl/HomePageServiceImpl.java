package com.example.jiuzhou.user.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.jiuzhou.common.utils.*;
import com.example.jiuzhou.user.mapper.AbpBerthsecsMapper;
import com.example.jiuzhou.user.mapper.AbpBusinessDetailMapper;
import com.example.jiuzhou.user.mapper.AbpRatesMapper;
import com.example.jiuzhou.user.mapper.TUserMapper;
import com.example.jiuzhou.user.model.*;
import com.example.jiuzhou.user.model.fee.RateCalculateModel;
import com.example.jiuzhou.user.model.fee.RateModel;
import com.example.jiuzhou.user.model.fee.Rates;
import com.example.jiuzhou.user.query.ArrearageQuery;
import com.example.jiuzhou.user.service.HomePageService;
import com.example.jiuzhou.user.service.WeiXinMessageService;
import com.example.jiuzhou.user.view.ArrearageListView;
import com.example.jiuzhou.user.view.ParkOrderView;
import com.example.jiuzhou.user.view.QueryOrderView;
import com.jfinal.weixin.sdk.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Appartion
 * @data 2022/10/21
 * 一入代码深似海，从此生活是路人
 */

@Slf4j
@Service
public class HomePageServiceImpl implements HomePageService {
    @Autowired
    private RedisTemplate redisTemplate;
    @Resource
    private AbpBusinessDetailMapper businessDetailMapper;
    @Resource
    private AbpBerthsecsMapper berthsecsMapper;
    @Resource
    private TUserMapper tUserMapper;
    @Resource
    private AbpRatesMapper abpRatesMapper;
    @Resource
    private WeiXinMessageService weiXinMessageService;

    @Override
    public Result<?> onlineCar(String uid) {
//        AbpWeixinConfig config= JSONObject.parseObject(redisTemplate.opsForValue().get("config").toString(),AbpWeixinConfig.class);
        List<Map<String,Object>> listMap = new ArrayList<>();
        List<ParkOrderView> orderList  = businessDetailMapper.getAllOrder(uid);
        RateCalculateModel calModel = new RateCalculateModel();
        if(orderList.size()>0){
            for(ParkOrderView order:orderList) {
                Map<String, Object> map = new HashMap<>();
                AbpBerthsecs berthsec = berthsecsMapper.getById(order.getBerthsecId());
                Rates rates = new Rates();
                AbpRates abpRates=abpRatesMapper.getById(berthsec.getRateId());

                rates.rateMode = JSONObject.parseObject(abpRates.getRatePDA(),RateModel.class);


                calModel = RateCalculate.ProcessRateCalculate(
                       order.getCarInTime(), new Date(),
                        2, order.getPlateNumber(), rates, 1, new AbpMonthlyCars());

                Integer berthsecId = order.getBerthsecId();
                QueryOrderView orderView =berthsecsMapper.queryOrder(berthsecId);
                map.put("count",orderView.getCount());
                map.put("orderCount",orderView.getOrderCount());
                SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                order.setCarStopTime(DateUtils.getGabTimDes(sdf.format(order.getCarInTime()),sdf.format(new Date())));


//                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    double diff = (new Date().getTime() - order.getCarInTime().getTime()) * 0.001;
                    map.put("Surplus",diff);
                    map.put("Minute", (int) (diff % 3600 / 60));
                    map.put("Hour",(int) (diff / 3600));
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                map.put("Money",String.format("%.2f",calModel.CalculateMoney));
                map.put("order",order);
                listMap.add(map);
            }

            return Result.success(listMap);
        }else{
            return Result.success();
        }
    }

    @Override
    public Result<?> getWexSDK(String url) {
        String accessToken = weiXinMessageService.getWeiXinToken().getData().toString();
        String jsapi_ticket  = getTicket(accessToken);
        if(accessToken.equals("error")){
            return Result.error("获取accessToken失败");
        }
        if(jsapi_ticket.equals("error")){
            return Result.error("获取ticket失败");
        }
        //获取noncestr
        String noncestr = UUID.randomUUID().toString().replaceAll("-", "").toLowerCase();;
        //获取timestamp
        String timestamp = Long.toString(System.currentTimeMillis() / 1000);
        //String str = "jsapi_ticket=" + jsapi_ticket + "&noncestr=" + noncestr + "×tamp=" + timestamp + "&url=" + url;
        String signature = "";
        try{
            signature= SingUtil.getSignature(jsapi_ticket,timestamp,noncestr,url);
        }catch (IOException e){
            e.printStackTrace();
            return Result.error("获取微信sdk失败");
        }
        Map<String,Object>  map = new HashMap<>();
        map.put("timeStamp",timestamp);
        map.put("nonceStr", noncestr);
        map.put("signature", signature);
        map.put("jsapi_ticket", jsapi_ticket);
        return Result.success(map);
    }

    @Override
    public Result<?> arrearage(ArrearageQuery query) {
        List<ArrearageListView> list=businessDetailMapper.arrearageList(query);
        return Result.success(list);
    }

    @Override
    public Result<?> getDJOrder(String plateNumber) {
        List<ArrearageListView> list=businessDetailMapper.getDJOrder(plateNumber);
        if(list.size()>0) {
            return Result.success(list);
        }else {
            return Result.error("未查询到车辆信息");
        }
    }

//    public String getAccessToken(){
//        //取系统配置信息
//        AbpWeixinConfig config= JSONObject.parseObject(redisTemplate.opsForValue().get("config").toString(),AbpWeixinConfig.class);
//
//        String access_token=null;
//        String appId=config.getAppId();
//        String appSecret=config.getAppSecret();
//        try{
//            access_token=String.valueOf(weiXinMessageService.getWeiXinToken().getData().toString());
//        }catch (Exception e){
//            e.printStackTrace();
//            return "error";
//        }
//
//        return access_token;
//    }

    public String getTicket(String accessToken){

        String GetPageAccessTokenUrl = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token="+accessToken+"&type=jsapi";
        Object ticket=redisTemplate.opsForValue().get("ticket");
        if(ticket==null){
            try {
                String Str = HttpUtils.get(GetPageAccessTokenUrl);
                log.info("获取ticket：{}",Str);
                JSONObject jsonObject = JSON.parseObject(Str);
                ticket = String.valueOf(jsonObject.get("ticket"));//获取ticket
                redisTemplate.opsForValue().set("ticket",ticket,7100, TimeUnit.SECONDS);

            }catch (Exception e){
                e.printStackTrace();
                return "error";

            }
        }

        return ticket.toString();
    }
}
