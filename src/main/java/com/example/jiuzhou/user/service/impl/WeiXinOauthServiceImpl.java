package com.example.jiuzhou.user.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.domain.PageInfo;
import com.example.jiuzhou.common.Enum.ResultEnum;
import com.example.jiuzhou.common.utils.Result;
import com.example.jiuzhou.user.mapper.AbpDeductionRecordsMapper;
import com.example.jiuzhou.user.mapper.ExtOtherAccountMapper;
import com.example.jiuzhou.user.mapper.TUserMapper;
import com.example.jiuzhou.user.model.*;
import com.example.jiuzhou.user.query.OauthQuery;
import com.example.jiuzhou.user.service.WeiXinOauthService;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.weixin.sdk.api.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;


import javax.annotation.Resource;

import java.math.BigDecimal;

import java.util.*;

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
//        TUser tUser=tUserMapper.getByOpenId(openId);
//
//        if(tUser!=null){
//            return Result.success(tUser);
//        }
        // 拉取用户信息(需scope为 snsapi_userinfo)
        ApiResult apiResult = SnsApi.getUserInfo(token, openId);
        log.warn("微信授权获取微信用户信息:" + apiResult.getJson());
        if (apiResult.isSucceed()) {
            Map<String ,Object> responseMap=new HashMap<>();
            JSONObject jsonObject = JSON.parseObject(apiResult.getJson());
            responseMap.put("openId",jsonObject.getString("openid"));
            responseMap.put("nick_name",jsonObject.getString("nickname"));
            responseMap.put("sex",jsonObject.getString("sex"));
            responseMap.put("headimgurl",jsonObject.getString("headimgurl"));
            return Result.success(responseMap);
        }
        return Result.error(ResultEnum.ERROR,"没有用户信息");

    }

    /**
     * 绑定手机号的时候生成用户信息进TUser表
     * 如果微信或者支付宝中有一个有了昵称头像性别就不更新
     * @param query
     * @return
     */
    @Override
    public Result<?> bindPhone(OauthQuery query) {
        Example example = new Example(TUser.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("tel", query.getMobile());
        List<TUser> tUsersByTel = tUserMapper.selectByExample(example);
        TUser newUser=new TUser();
        if(tUsersByTel.size()==0){
            //头一次注册直接生成用户信息
            newUser.setNickName(query.getNickName());
            // 用户的性别，值为0时是男性，值为1时是女性
            newUser.setSex(query.getSex());
            newUser.setTel(query.getMobile());
            newUser.setOpenId(query.getOpenId());
            newUser.setUserId(query.getUserId());
            newUser.setHeadImgUrl(query.getHeadImgUrl());
            newUser.setTenantId(TENANTID);
            newUser.setUid(UUID.randomUUID().toString().replace("-",""));
            newUser.setRegisterDate(new Date());
            newUser.setLevels(1);
            newUser.setSendWeixinNumber(0);

            tUserMapper.insetOne(newUser);
            createAccount(query.getMobile());
            return Result.success(newUser);
        }
        if(tUsersByTel.size()>=2){
            return Result.error(ResultEnum.ERROR);
        }
        //非第一次注册 每次绑定更新手机号对应的openid或者userid
        newUser=tUsersByTel.get(0);
        if(StringUtils.isEmpty(newUser.getNickName()) && StringUtils.isNotEmpty(query.getNickName())){
            newUser.setNickName(query.getNickName());
        }
        if(StringUtils.isEmpty(newUser.getHeadImgUrl()) && StringUtils.isNotEmpty(query.getHeadImgUrl())){
            newUser.setHeadImgUrl(query.getHeadImgUrl());
        }
        if(newUser.getSex()==null && newUser.getSex()!=null){
            newUser.setSex(query.getSex());
        }
        if(StringUtils.isNotEmpty(query.getOpenId())){
            newUser.setOpenId(query.getOpenId());
        }
        if(StringUtils.isNotEmpty(query.getUserId())){
            newUser.setUserId(query.getUserId());
        }
        tUserMapper.updateByPrimaryKey(newUser);


        return Result.success(newUser);
    }

    /**
     * 创建储值卡
     * @param mobile
     */
    private void createAccount(String mobile){
        //取系统配置信息
        AbpWeixinConfig config=JSONObject.parseObject(redisTemplate.opsForValue().get("config").toString(),AbpWeixinConfig.class);

        //取系统用户信息
        TUser tUser=tUserMapper.getByTel(mobile);

        String carNo=UUID.randomUUID().toString().replace("-", "").substring(0, 20);

        ExtOtherAccount a=extOtherAccountMapper.getByUid(tUser.getUid());
        //有用户并且没开通过卡
        if(tUser!=null&&a==null){
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
            extOtherAccount.setUid(tUser.getUid());
            extOtherAccount.setAccountLoginDatetime(new Date());
            extOtherAccount.setCreationTime(new Date());
            extOtherAccount.setSendSmsDatetime(new Date());
            extOtherAccount.setSendSmsNumber(0);
            extOtherAccountMapper.insetOne(extOtherAccount);
            ExtOtherAccount account=extOtherAccountMapper.getByCardNo(carNo);
            //开通记录
            AbpDeductionRecords abpDeductionRecords=new AbpDeductionRecords();
            abpDeductionRecords.setOtherAccountId(account.getId());
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
            abpDeductionRecords.setInTime(new Date());
            abpDeductionRecordsMapper.insetOne(abpDeductionRecords);
        }
    }
}
