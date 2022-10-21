package com.example.jiuzhou.user.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.jiuzhou.common.utils.Result;
import com.example.jiuzhou.common.utils.SingUtil;
import com.example.jiuzhou.user.mapper.AbpBerthsecsMapper;
import com.example.jiuzhou.user.mapper.AbpBusinessDetailMapper;
import com.example.jiuzhou.user.model.AbpBerthsecs;
import com.example.jiuzhou.user.model.AbpWeixinConfig;
import com.example.jiuzhou.user.model.fee.Rates;
import com.example.jiuzhou.user.service.HomePageService;
import com.example.jiuzhou.user.view.ParkOrderView;
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

    @Override
    public Result<?> onlineCar(String uid) {
        AbpWeixinConfig config= JSONObject.parseObject(redisTemplate.opsForValue().get("config").toString(),AbpWeixinConfig.class);
        List<ParkOrderView> orderList  = businessDetailMapper.getAllOrder(uid);
        if(orderList.size()>0){
            for(ParkOrderView order:orderList){
                Map<String,Object> map=new HashMap<>();
                AbpBerthsecs berthsec = berthsecsMapper.getById(order.getBerthsecId());
                Rates rates = new Rates();
                AbpRates.dao.findById(berthsec.getInt("RateId")).getStr("RatePDA");
                System.out.println("ces"+AbpRates.dao.findById(berthsec.getInt("RateId")).getStr("RatePDA"));
                rates.rateMode = (RateModel) fromJson(AbpRates.dao.findById(berthsec.getInt("RateId")).getStr("RatePDA"),
                        RateModel.class);

                calModel = RateCalculate.ProcessRateCalculate(
                        AbDateUtil.getDateByFormat(order.get("CarInTime").toString(), AbDateUtil.dateFormatYMDHM2), new Date(),
                        2, order.getStr("PlateNumber"), rates, 1, new AbpMonthlyCars());

                Integer berthsecId = order.getInt("berthsecId");
                AbpBerthsecs abpBerthsVos = AbpBerthsecs.dao.QueryOrder(berthsecId);
                Integer count = abpBerthsVos.getInt("count");
                Integer orderCount = abpBerthsVos.getInt("orderCount");
                map.put("count",count);
                map.put("orderCount",orderCount);
                order.put("carStopTime",DateTime.getTimeDifference(order.get("CarInTime").toString(),null));
                ExtOtherAccount account = ExtOtherAccount.dao.GetAccount(openId);
                setAttr("account", account);

                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    double diff = (new Date().getTime() - df.parse(order.get("carintime").toString()).getTime()) * 0.001;
                    map.put("Surplus",diff);
                    map.put("Minute", (int) (diff % 3600 / 60));
                    map.put("Hour",(int) (diff / 3600));
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                map.put("Money",String.format("%.2f",calModel.CalculateMoney));
                map.put("order",order);
                listMap.add(map);
            }

            result.success(listMap);
            renderJson(result);
        }else{
            TUser tUser = TUser.dao.findByOpenId(openId);
            renderJson(result);
            return;
        }

        return Result.success();
    }

    @Override
    public Result<?> getWexSDK(String url) {
        String accessToken = getAccessToken();
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

    public String getAccessToken(){
        //取系统配置信息
        AbpWeixinConfig config= JSONObject.parseObject(redisTemplate.opsForValue().get("config").toString(),AbpWeixinConfig.class);

        String access_token=null;
        String appId=config.getAppId();
        String appSecret=config.getAppSecret();
        String GetPageAccessTokenUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+appId+"&secret="+appSecret;

        try{
            String Str = HttpUtils.get(GetPageAccessTokenUrl);
            JSONObject jsonObject = JSON.parseObject(Str);
            log.info("获取微信token返回信息:{}",jsonObject);
            access_token=String.valueOf(jsonObject.get("access_token"));
        }catch (Exception e){
            e.printStackTrace();
            return "error";
        }

        return access_token;
    }

    public String getTicket(String accessToken){
        String ticket = "";
        String GetPageAccessTokenUrl = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token="+accessToken+"&type=jsapi";
        try {
            String Str = HttpUtils.get(GetPageAccessTokenUrl);
            JSONObject jsonObject = JSON.parseObject(Str);
            ticket = String.valueOf(jsonObject.get("ticket"));//获取ticket		}catch (Exception e){
        }catch (Exception e){
            e.printStackTrace();
            return "error";

        }
        return ticket;
    }
}
