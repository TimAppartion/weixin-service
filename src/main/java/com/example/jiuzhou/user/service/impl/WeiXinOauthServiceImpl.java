package com.example.jiuzhou.user.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.domain.PageInfo;
import com.example.jiuzhou.common.Enum.ResultEnum;
import com.example.jiuzhou.common.utils.Result;
import com.example.jiuzhou.user.mapper.AbpDeductionRecordsMapper;
import com.example.jiuzhou.user.mapper.ExtOtherAccountMapper;
import com.example.jiuzhou.user.mapper.TUserMapper;
import com.example.jiuzhou.user.model.AbpDeductionRecords;
import com.example.jiuzhou.user.model.AbpWeixinConfig;
import com.example.jiuzhou.user.model.ExtOtherAccount;
import com.example.jiuzhou.user.model.TUser;
import com.example.jiuzhou.user.query.OauthQuery;
import com.example.jiuzhou.user.service.WeiXinOauthService;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.weixin.sdk.api.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.beans.Transient;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Service
@Slf4j
public class WeiXinOauthServiceImpl implements WeiXinOauthService {

    private static final Prop prop = PropKit.use("weixin.properties");
    private static String APP_ID=prop.get("appId");
    private static String APP_SECRET=prop.get("appSecret");;
    private static String TOKEN=prop.get("token");;
    private static Integer TENANTID=Integer.valueOf(prop.get("tenantId"));

    @Resource
    private TUserMapper tUserMapper;

    @Resource
    private ExtOtherAccountMapper extOtherAccountMapper;

    @Resource
    private AbpDeductionRecordsMapper abpDeductionRecordsMapper;

    @Resource
    private RedisTemplate redisTemplate;

    @Override
    public Result<?> index(OauthQuery query) {
        //取系统配置信息
        AbpWeixinConfig config=JSONObject.parseObject(redisTemplate.opsForValue().get("config").toString(),AbpWeixinConfig.class);

        ApiConfig ac = new ApiConfig();
        ac.setToken(TOKEN);
        ac.setAppId(APP_ID);
        ac.setAppSecret(APP_SECRET);
        ac.setEncryptMessage(false);
        ac.setEncodingAesKey(config.getEncodingAESKey());
        ApiConfigKit.setThreadLocalApiConfig(ac);

        String appId = ApiConfigKit.getApiConfig().getAppId();
        String secret = ApiConfigKit.getApiConfig().getAppSecret();
        // 通过code换取网页授权access_token
        SnsAccessToken snsAccessToken = SnsAccessTokenApi.getSnsAccessToken(appId, secret, query.getCode());
        // String json=snsAccessToken.getJson();
        String token = snsAccessToken.getAccessToken();
        String openId = snsAccessToken.getOpenid();

        log.info("snsAccessToken:{},token:{},openId:{}",snsAccessToken,token,openId);
        TUser tUser=tUserMapper.getByOpenId(openId);

        if(tUser!=null){
            return Result.success(tUser);
        }
        // 拉取用户信息(需scope为 snsapi_userinfo)
        ApiResult apiResult = SnsApi.getUserInfo(token, openId);
        log.warn("getUserInfo:" + apiResult.getJson());
        if (apiResult.isSucceed()) {
            TUser newUser=new TUser();
            JSONObject jsonObject = JSON.parseObject(apiResult.getJson());
            newUser.setOpenId(jsonObject.getString("openid"));
            newUser.setNickName(jsonObject.getString("nickname"));
            // 用户的性别，值为0时是男性，值为1时是女性
            newUser.setSex(jsonObject.getIntValue("sex"));
            newUser.setHeadImgUrl(jsonObject.getString("headimgurl"));
            newUser.setTenantId(TENANTID);
            newUser.setUId(UUID.randomUUID().toString().replace("-",""));
            newUser.setRegisterDate(new Date());
            newUser.setLevels(1);
            newUser.setSendWeixinNumber(0);
            tUserMapper.insert(newUser);
            return Result.success(newUser);
        }
        return Result.error(ResultEnum.ERROR,"没有用户信息");

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> bindPhone(OauthQuery query) {
        TUser tUser=tUserMapper.getByTel(query.getMobile());
        if(null!=tUser){
            tUser.setOpenId(query.getOpenId());
            tUser.setUserId(query.getUserId());
            tUserMapper.updateByPrimaryKey(tUser);
            createAccount(query.getMobile());
            return Result.success(tUser);
        }

        return Result.error(ResultEnum.ERROR,"绑定失败");
    }

    /**
     * 创建储值卡
     * @param mobile
     */
    private void createAccount(String mobile){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //取系统配置信息
        AbpWeixinConfig config=JSONObject.parseObject(redisTemplate.opsForValue().get("config").toString(),AbpWeixinConfig.class);

        //取系统用户信息
        TUser tUser=tUserMapper.getByTel(mobile);

        String carNo=UUID.randomUUID().toString().replace("-", "").substring(0, 20);
        if(tUser!=null){
            //开通储值卡
            ExtOtherAccount extOtherAccount=new ExtOtherAccount();
            extOtherAccount.setAuthenticationSource("WeiXin");
            extOtherAccount.setUserName(mobile);
            extOtherAccount.setName(mobile);
            extOtherAccount.setPassword("123456");
            extOtherAccount.setTelePhone(mobile);
            extOtherAccount.setIsActive(1);
            extOtherAccount.setCreatorUserId(config.getDepositCard());
            extOtherAccount.setIsDeleted(0);
            extOtherAccount.setCardNo(carNo);
            extOtherAccount.setWallet(new BigDecimal(0));
            extOtherAccount.setTenantId(TENANTID);
            extOtherAccount.setIsLock(0);
            extOtherAccount.setIsEnabled(1);
            extOtherAccount.setProductNo(mobile);
            extOtherAccount.setCompanyId(TENANTID);
            extOtherAccount.setIsPhoneConfirmed(1);
            extOtherAccount.setUId(tUser.getUId());
            extOtherAccountMapper.insert(extOtherAccount);
            //开通记录
            AbpDeductionRecords abpDeductionRecords=new AbpDeductionRecords();
            abpDeductionRecords.setOtherAccountId(extOtherAccount.getId());
            abpDeductionRecords.setOperType(1);
            abpDeductionRecords.setMoney(new BigDecimal(0));
            abpDeductionRecords.setPayStatus(1);
            abpDeductionRecords.setRemark("微信开户");
            abpDeductionRecords.setEmployeeId(config.getDepositCard());
            abpDeductionRecords.setTenantId(TENANTID);
            abpDeductionRecords.setCompanyId(TENANTID);
            abpDeductionRecords.setUserId(config.getDepositCard());
            abpDeductionRecords.setCardNo(carNo);
            abpDeductionRecords.setBeginMoney(new BigDecimal(0));
            abpDeductionRecords.setEndMoney(new BigDecimal(0));
            abpDeductionRecordsMapper.insert(abpDeductionRecords);
        }
    }
}
