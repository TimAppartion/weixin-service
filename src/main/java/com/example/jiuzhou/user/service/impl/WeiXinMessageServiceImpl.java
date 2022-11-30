package com.example.jiuzhou.user.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.jiuzhou.common.utils.DateTimeUtils;
import com.example.jiuzhou.common.utils.DateUtils;
import com.example.jiuzhou.common.utils.Result;
import com.example.jiuzhou.user.mapper.AbpSettingsMapper;
import com.example.jiuzhou.user.mapper.TUserMapper;
import com.example.jiuzhou.user.model.AbpSettings;
import com.example.jiuzhou.user.model.AbpWeixinConfig;
import com.example.jiuzhou.user.model.TUser;
import com.example.jiuzhou.user.query.WeiXinMessageQuery;
import com.example.jiuzhou.user.service.WeiXinMessageService;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.weixin.sdk.api.*;
import com.jfinal.weixin.sdk.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

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
    private static String domain_h5=prop.get("domain_h5");
    @Autowired
    private RedisTemplate redisTemplate;

    @Resource
    private AbpSettingsMapper abpSettingsMapper;

    @Resource
    private TUserMapper tUserMapper;

    @Override
    public Result<?> SendParkPay(WeiXinMessageQuery query) {
        log.info("SendParkPay  query:{}",query);
        AbpSettings settings=abpSettingsMapper.getByName("SendMsgOrder",TENANTID);
        AbpWeixinConfig config= JSONObject.parseObject(redisTemplate.opsForValue().get("config").toString(),AbpWeixinConfig.class);
        //配置微信Api
        ApiConfig ac=new ApiConfig();

        ac.setToken(this.getWeiXinToken().getData().toString());

        /**
         * 是否对消息进行加密，对应于微信平台的消息加解密方式： 1：true进行加密且必须配置 encodingAesKey
         * 2：false采用明文模式，同时也支持混合模式
         */
        ac.setEncryptMessage(PropKit.getBoolean("encryptMessage", false));
        ac.setEncodingAesKey(config.getEncodingAESKey());
        ApiConfigKit.setThreadLocalApiConfig(ac);

        TUser tUser=tUserMapper.getByCarNumber(query.getCarNumber());
        String json = TemplateData.New().setTouser(tUser.getOpenId())
                .setTemplate_id(settings.getValue())
                .setTopcolor("#743A3A").setUrl(domain_h5 + "pages/record/record").add("first", "停车缴费成功通知。", "#743A3A")
                .add("keyword1", query.getCarNumber(), "#0000FF").add("keyword2", query.getParkName() , "#0000FF")
                .add("keyword3", query.getCarInTime(), "#0000FF").add("keyword4", query.getCarOutTime(), "#0000FF")
                .add("keyword5", DateUtils.getGabTimDes(query.getCarInTime(), query.getCarOutTime()), "#0000FF")
                .add("remark", "消费金额:"+query.getRemake()+ "元,"+ config.getAppName() + "谢谢您的信任，祝生活愉快", "#008000")
                .build();
        ApiResult result = TemplateMsgApi.send(json);
        return Result.success(result);
    }

    @Override
    public Result<?> SendInPark(WeiXinMessageQuery query) {
        log.info("SendInPark  query:{}",query);
        AbpWeixinConfig config= JSONObject.parseObject(redisTemplate.opsForValue().get("config").toString(),AbpWeixinConfig.class);
        AbpSettings settings=abpSettingsMapper.getByName("SendMsgStopCar",TENANTID);

        ApiConfig ac = new ApiConfig();
        // 配置微信 API 相关常量

        ac.setToken(this.getWeiXinToken().getData().toString());

        ac.setAppId(config.getAppId());
        ac.setAppSecret(config.getAppSecret());
        /**
         * 是否对消息进行加密，对应于微信平台的消息加解密方式： 1：true进行加密且必须配置 encodingAesKey
         * 2：false采用明文模式，同时也支持混合模式
         */
        ac.setEncryptMessage(PropKit.getBoolean("encryptMessage", false));
        ac.setEncodingAesKey(config.getEncodingAESKey());
        ApiConfigKit.setThreadLocalApiConfig(ac);
        TUser tUser=tUserMapper.getByCarNumber(query.getCarNumber());
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日  HH:mm:ss");
//        String time = sdf.format(new Date());
        String json = TemplateData.New().setTouser(tUser.getOpenId())
                .setTemplate_id(settings.getValue())
                .setTopcolor("#743A3A").setUrl(domain_h5 + "pages/index/index").add("first", "您好，您的爱车"+query.getCarNumber()+"已驶入停车位。", "#743A3A")
                .add("keyword1", query.getCarNumber(), "#0000FF")
                .add("keyword2", query.getParkName() , "#0000FF")
                .add("keyword3", query.getCarInTime(), "#0000FF")
                .add("remark", config.getAppName() + "欢迎您", "#008000")
                .build();

        ApiResult result = TemplateMsgApi.send(json);
        log.info("SendInPark  result:{}",result);
        return Result.success(result);
    }

    @Override
    public Result<?> SendOutPark(WeiXinMessageQuery query) {
        log.info("sendOutPark  query:{}",query);
        AbpWeixinConfig config= JSONObject.parseObject(redisTemplate.opsForValue().get("config").toString(),AbpWeixinConfig.class);
        AbpSettings settings=abpSettingsMapper.getByName("SendMsgOrderOut",TENANTID);
        ApiConfig ac = new ApiConfig();
        // 配置微信 API 相关常量

        ac.setToken(this.getWeiXinToken().getData().toString());

        ac.setAppId(config.getAppId());
        ac.setAppSecret(config.getAppSecret());
        /**
         * 是否对消息进行加密，对应于微信平台的消息加解密方式： 1：true进行加密且必须配置 encodingAesKey
         * 2：false采用明文模式，同时也支持混合模式
         */
        ac.setEncryptMessage(PropKit.getBoolean("encryptMessage", false));
        ac.setEncodingAesKey(config.getEncodingAESKey());
        ApiConfigKit.setThreadLocalApiConfig(ac);
        TUser tUser=tUserMapper.getByCarNumber(query.getCarNumber());
        String json = TemplateData.New().setTouser(tUser.getOpenId())
                .setTemplate_id(settings.getValue())
                .setTopcolor("#743A3A").setUrl(domain_h5 + "pages/record/record").add("first", "您好，您的车辆已驶出"+query.getParkName()+"，祝您一路顺风。", "#743A3A")
                .add("keyword1", query.getCarNumber(), "#0000FF")
                .add("keyword2", query.getParkName() , "#0000FF")
                .add("keyword3", query.getCarInTime(), "#0000FF")
                .add("keyword4", query.getCarOutTime(), "#0000FF")
                .add("keyword5", DateUtils.getGabTimDes(query.getCarInTime(),query.getCarOutTime()), "#0000FF")
                .add("remark", config.getAppName()+ "谢谢您的信任，祝生活愉快", "#008000")
                .build();
        ApiResult result = TemplateMsgApi.send(json);
        log.info("SendOutPark result:{}",result);
        return Result.success(result);
    }

    @Override
    public Result<?> getWeiXinToken() {
        Object token = redisTemplate.opsForValue().get("WeiXinToken");
        try {
            if (token==null  ) {
                AbpWeixinConfig config = JSONObject.parseObject(redisTemplate.opsForValue().get("config").toString(), AbpWeixinConfig.class);
                String GetPageAccessTokenUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + config.getAppId() + "&secret=" + config.getAppSecret();
                String str = HttpUtils.get(GetPageAccessTokenUrl);
                log.info("微信获取token：{}",str);
                JSONObject jsonObject = JSON.parseObject(str);

                token = String.valueOf(jsonObject.get("access_token"));

                redisTemplate.opsForValue().set("WeiXinToken", token,7100, TimeUnit.SECONDS);
            }
        }catch (Exception e){
            throw new RuntimeException("获取access_token失败");
        }
        return Result.success(token);

    }
}
