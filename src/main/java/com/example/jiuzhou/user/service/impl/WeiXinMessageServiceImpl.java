package com.example.jiuzhou.user.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.jiuzhou.common.utils.DateTimeUtils;
import com.example.jiuzhou.common.utils.Result;
import com.example.jiuzhou.user.mapper.AbpSettingsMapper;
import com.example.jiuzhou.user.model.AbpSettings;
import com.example.jiuzhou.user.model.AbpWeixinConfig;
import com.example.jiuzhou.user.query.WeiXinMessageQuery;
import com.example.jiuzhou.user.service.WeiXinMessageService;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.weixin.sdk.api.*;
import com.jfinal.weixin.sdk.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author Appartion
 * @data 2022/10/14
 * 一入代码深似海，从此生活是路人
 */
@Service
@Slf4j
public class WeiXinMessageServiceImpl implements WeiXinMessageService {
    private static final Prop prop = PropKit.use("weixin.properties");
    private static Integer TENANTID=Integer.valueOf(prop.get("tenantId"));

    @Autowired
    private RedisTemplate redisTemplate;

    @Resource
    private AbpSettingsMapper abpSettingsMapper;

    @Override
    public Result<?> SendParkPay(WeiXinMessageQuery query) {
        AbpWeixinConfig config= JSONObject.parseObject(redisTemplate.opsForValue().get("config").toString(),AbpWeixinConfig.class);
        AbpSettings settings=abpSettingsMapper.getByName("SendMsgOrder",TENANTID);

        //配置微信Api
        ApiConfig ac=new ApiConfig();

        String appId = config.getAppId();
        String appSecret = config.getAppSecret();
        String GetPageAccessTokenUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+appId+"&secret="+appSecret;
        ac.setAppId(appId);
        ac.setAppSecret(appSecret);
        try {
            String Str = HttpUtils.get(GetPageAccessTokenUrl);
            JSONObject jsonObject = JSON.parseObject(Str);
            ac.setToken( String.valueOf(jsonObject.get("access_token")));
        }catch (Exception e){
            throw new RuntimeException("获取access_token失败");
        }

        /**
         * 是否对消息进行加密，对应于微信平台的消息加解密方式： 1：true进行加密且必须配置 encodingAesKey
         * 2：false采用明文模式，同时也支持混合模式
         */
        ac.setEncryptMessage(PropKit.getBoolean("encryptMessage", false));
        ac.setEncodingAesKey(config.getEncodingAESKey());
        ApiConfigKit.setThreadLocalApiConfig(ac);


        String json = TemplateData.New().setTouser(query.getOpenId())
                .setTemplate_id(settings.getValue())
                .setTopcolor("#743A3A").setUrl(PropKit.get("domain_h5") + "pages/record/record").add("first", "停车缴费成功通知。", "#743A3A")
                .add("keyword1", query.getCarNumber(), "#0000FF").add("keyword2", query.getParkName() , "#0000FF")
                .add("keyword3", query.getCarInTime(), "#0000FF").add("keyword4", query.getCarOutTime(), "#0000FF")
                .add("keyword5", DateTimeUtils.getTimeDifference(query.getCarInTime(), query.getCarOutTime()), "#0000FF")
                .add("remark", "消费金额:"+query.getRemake()+ "元,"+ config.getAppName() + "谢谢您的信任，祝生活愉快", "#008000")
                .build();
        ApiResult result = TemplateMsgApi.send(json);
        return Result.success(result);
    }

    @Override
    public Result<?> SendInPark(WeiXinMessageQuery query) {
        AbpWeixinConfig config= JSONObject.parseObject(redisTemplate.opsForValue().get("config").toString(),AbpWeixinConfig.class);
        AbpSettings settings=abpSettingsMapper.getByName("SendInPark",TENANTID);

        ApiConfig ac = new ApiConfig();
        // 配置微信 API 相关常量
        String GetPageAccessTokenUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+config.getAppId()+"&secret="+config.getAppSecret();

        try {
            String Str = HttpUtils.get(GetPageAccessTokenUrl);
            JSONObject jsonObject = JSON.parseObject(Str);
            System.out.println("返回信息"+jsonObject);
            ac.setToken( String.valueOf(jsonObject.get("access_token")));
        }catch (Exception e){
            throw new RuntimeException("获取access_token失败");
        }

        ac.setAppId(config.getAppId());
        ac.setAppSecret(config.getAppSecret());
        /**
         * 是否对消息进行加密，对应于微信平台的消息加解密方式： 1：true进行加密且必须配置 encodingAesKey
         * 2：false采用明文模式，同时也支持混合模式
         */
        ac.setEncryptMessage(PropKit.getBoolean("encryptMessage", false));
        ac.setEncodingAesKey(config.getEncodingAESKey());
        ApiConfigKit.setThreadLocalApiConfig(ac);

//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日  HH:mm:ss");
//        String time = sdf.format(new Date());
        String json = TemplateData.New().setTouser(query.getOpenId())
                .setTemplate_id(settings.getValue())
                .setTopcolor("#743A3A").setUrl(PropKit.get("domain_h5") + "pages/record/record").add("first", "您好，您的爱车"+query.getCarNumber()+"已驶入停车位。", "#743A3A")
                .add("keyword1", query.getCarNumber(), "#0000FF")
                .add("keyword2", query.getParkName() , "#0000FF")
                .add("keyword3", query.getCarInTime(), "#0000FF")
                .add("remark", config.getAppName() + "欢迎您", "#008000")
                .build();

        ApiResult result = TemplateMsgApi.send(json);
        return Result.success(result);
    }

    @Override
    public Result<?> SendOutPark(WeiXinMessageQuery query) {
        AbpWeixinConfig config= JSONObject.parseObject(redisTemplate.opsForValue().get("config").toString(),AbpWeixinConfig.class);
        AbpSettings settings=abpSettingsMapper.getByName("SendOutPark",TENANTID);

        ApiConfig ac = new ApiConfig();
        // 配置微信 API 相关常量

        String GetPageAccessTokenUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+config.getAppId()+"&secret="+config.getAppSecret();

        try {
            String Str = HttpUtils.get(GetPageAccessTokenUrl);
            JSONObject jsonObject = JSON.parseObject(Str);
            System.out.println("返回信息"+jsonObject);
            ac.setToken( String.valueOf(jsonObject.get("access_token")));
        }catch (Exception e){
            throw new RuntimeException("获取access_token失败");
        }

        ac.setAppId(config.getAppId());
        ac.setAppSecret(config.getAppSecret());
        /**
         * 是否对消息进行加密，对应于微信平台的消息加解密方式： 1：true进行加密且必须配置 encodingAesKey
         * 2：false采用明文模式，同时也支持混合模式
         */
        ac.setEncryptMessage(PropKit.getBoolean("encryptMessage", false));
        ac.setEncodingAesKey(config.getEncodingAESKey());
        ApiConfigKit.setThreadLocalApiConfig(ac);

//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日  HH:mm:ss");
//        String time = sdf.format(new Date());
        String json = TemplateData.New().setTouser(query.getOpenId())
                .setTemplate_id(settings.getValue())
                .setTopcolor("#743A3A").setUrl(PropKit.get("domain_h5") + "pages/record/record").add("first", "您好，您的车辆已驶出"+query.getParkName()+"，祝您一路顺风。", "#743A3A")
                .add("keyword1", query.getCarNumber(), "#0000FF")
                .add("keyword2", query.getParkName() , "#0000FF")
                .add("keyword3", query.getCarInTime(), "#0000FF")
                .add("keyword4", query.getCarOutTime(), "#0000FF")
                .add("keyword5", DateTimeUtils.getTimeDifference(query.getCarInTime(),query.getCarOutTime()), "#0000FF")
                .add("remark", config.getAppName()+ "谢谢您的信任，祝生活愉快", "#008000")
                .build();
        ApiResult result = TemplateMsgApi.send(json);
        return Result.success(result);
    }
}
