package com.example.jiuzhou.user.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.example.jiuzhou.common.utils.Result;
import com.example.jiuzhou.user.mapper.AbpSettingsMapper;
import com.example.jiuzhou.user.model.AbpSettings;
import com.example.jiuzhou.user.model.AbpWeixinConfig;
import com.example.jiuzhou.user.service.PushMessageService;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.weixin.sdk.api.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Appartion
 * @data 2022/10/18
 * 一入代码深似海，从此生活是路人
 */
@Slf4j
@Service
public class PushMessageServiceImpl implements PushMessageService {
    private static final Prop prop = PropKit.use("weixin.properties");
    private static Integer TENANTID=Integer.valueOf(prop.get("tenantId"));

    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private AbpSettingsMapper settingsMapper;

    @Override
    public void sendMonthlyCarRenewalMsg(String touser, BigDecimal Money, String CarNumber, boolean isRenewal) {

        AbpWeixinConfig config= JSONObject.parseObject(redisTemplate.opsForValue().get("config").toString(),AbpWeixinConfig.class);

        ApiConfig ac = new ApiConfig();
        // 配置微信 API 相关常量
        ac.setToken(config.getToken());
        ac.setAppId(config.getAppId());
        ac.setAppSecret(config.getAppSecret());
        /**
         * 是否对消息进行加密，对应于微信平台的消息加解密方式： 1：true进行加密且必须配置 encodingAesKey
         * 2：false采用明文模式，同时也支持混合模式
         */
        ac.setEncryptMessage(PropKit.getBoolean("encryptMessage", false));
        ac.setEncodingAesKey(config.getEncodingAESKey());
        ApiConfigKit.setThreadLocalApiConfig(ac);

        AbpSettings settings=settingsMapper.getByName("SendMonthRechargeMsg",config.getTenantId());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日  HH:mm:ss");
        String time = sdf.format(new Date());
        String json = TemplateData.New().setTouser(touser)
                .setTemplate_id(settings.getValue())
                .setTopcolor("#743A3A").setUrl(PropKit.get("domain_h5") + "pages/Monthly")
                .add("first", isRenewal ? "道路停车包月续费通知单。" : "道路停车包月成功通知单。", "#743A3A").add("product", CarNumber + "包月服务", "#0000FF")
                .add("price", String.valueOf(Money), "#0000FF").add("time", time, "#0000FF")
                .add("remark", config.getAppName() + "感谢您对我们的信任，我们将为您提供更优质的服务。",
                        "#008000")
                .build();
        log.info("包月短信发送json：{}",json);
        ApiResult result = TemplateMsgApi.send(json);
        log.info("包月短信返回信息:{}",result);
    }

    @Override
    public Result<?> sendMsgOrder(String openId, BigDecimal money, String plateNumber, String berthNumber, String stopTime) {
        AbpWeixinConfig config= JSONObject.parseObject(redisTemplate.opsForValue().get("config").toString(),AbpWeixinConfig.class);
        AbpSettings settings=settingsMapper.getByName("SendMsgOrder",TENANTID);


        ApiConfig ac = new ApiConfig();
        // 配置微信 API 相关常量
        ac.setToken(config.getToken());
        ac.setAppId(config.getAppId());
        ac.setAppSecret(config.getAppSecret());
        /**
         * 是否对消息进行加密，对应于微信平台的消息加解密方式： 1：true进行加密且必须配置 encodingAesKey
         * 2：false采用明文模式，同时也支持混合模式
         */
        ac.setEncryptMessage(PropKit.getBoolean("encryptMessage", false));
        ac.setEncodingAesKey(config.getEncodingAESKey());
        ApiConfigKit.setThreadLocalApiConfig(ac);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日  HH:mm:ss");
        String time = sdf.format(new Date());
        String json = TemplateData.New().setTouser(openId)
                .setTemplate_id(settings.getValue())
                .setTopcolor("#743A3A").setUrl(PropKit.get("domain_h5") + "pages/record/record").add("first", "道路停车缴费通知单。", "#743A3A")
                .add("keyword1", plateNumber, "#0000FF").add("keyword2", berthNumber + "泊位", "#0000FF")
                .add("keyword3", stopTime, "#0000FF").add("keyword4", money + "元", "#0000FF")
                .add("keyword5", time, "#0000FF")
                .add("remark", config.getAppName()+ "谢谢您的信任，祝生活愉快", "#008000")
                .build();
        ApiResult result = TemplateMsgApi.send(json);
        return Result.success(result);
    }

    @Override
    public Result<?> SendMsgOrderOut(String openId, String plateNumber, String berthNumber, String payType, BigDecimal money, String stopTime, String tel, Date carOutTime) {
        AbpWeixinConfig config= JSONObject.parseObject(redisTemplate.opsForValue().get("config").toString(),AbpWeixinConfig.class);
        AbpSettings settings=settingsMapper.getByName("SendMsgOrderOut",TENANTID);

        ApiConfig ac = new ApiConfig();
        // 配置微信 API 相关常量
        ac.setToken(config.getToken());
        ac.setAppId(config.getAppId());
        ac.setAppSecret(config.getAppSecret());
        /**
         * 是否对消息进行加密，对应于微信平台的消息加解密方式： 1：true进行加密且必须配置 encodingAesKey
         * 2：false采用明文模式，同时也支持混合模式
         */
        ac.setEncryptMessage(PropKit.getBoolean("encryptMessage", false));
        ac.setEncodingAesKey(config.getEncodingAESKey());
        ApiConfigKit.setThreadLocalApiConfig(ac);

        // SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        // String time = sdf.format(new Date());
        String json = TemplateData.New().setTouser(openId)
                .setTemplate_id(settings.getValue())
                .setTopcolor("#743A3A").setUrl(PropKit.get("domain_h5") + "pages/record/record")
                .add("first", "您好！感谢您的光临。您的爱车已经驶离停车场，停车计时已经停止。", "#743A3A").add("keyword1", tel, "#0000FF")
                .add("keyword2", plateNumber, "#0000FF").add("keyword3", String.valueOf(carOutTime), "#0000FF")
                .add("remark",
                        "总停车时长" + stopTime + "，消费金额" + money + "元，支付方式:" + payType + ","
                                + config.getAppName()+ "欢迎您，祝生活愉快",
                        "#008000")
                .build();
        ApiResult result = TemplateMsgApi.send(json);
        return Result.success(result);
    }
}
