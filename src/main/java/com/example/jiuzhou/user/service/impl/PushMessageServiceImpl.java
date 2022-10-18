package com.example.jiuzhou.user.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.example.jiuzhou.user.mapper.AbpSettingsMapper;
import com.example.jiuzhou.user.model.AbpSettings;
import com.example.jiuzhou.user.model.AbpWeixinConfig;
import com.example.jiuzhou.user.service.PushMessageService;
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
}
