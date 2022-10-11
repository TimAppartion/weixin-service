package com.example.jiuzhou.user.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.jiuzhou.common.Enum.ResultEnum;
import com.example.jiuzhou.common.utils.Result;
import com.example.jiuzhou.user.mapper.TUserMapper;
import com.example.jiuzhou.user.model.TUser;
import com.example.jiuzhou.user.query.OauthQuery;
import com.example.jiuzhou.user.service.WeiXinOauthService;
import com.jfinal.weixin.sdk.api.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;

@Service
@Slf4j
public class WeiXinOauthServiceImpl implements WeiXinOauthService {

    @Value("${WeiXin.appId}")
    private String APP_ID;

    @Value("${WeiXin.appSecret}")
    private String APP_SECRET;

    @Value("${WeiXin.token}")
    private String TOKEN;

    @Value("${WeiXin.TenantId}")
    private Integer TENANTID;

    @Resource
    private TUserMapper tUserMapper;

    @Override
    public Result<?> index(OauthQuery query) {
        // 通过code换取网页授权access_token
        SnsAccessToken snsAccessToken = SnsAccessTokenApi.getSnsAccessToken(APP_ID, APP_SECRET, query.getCode());
        // String json=snsAccessToken.getJson();
        String token = snsAccessToken.getAccessToken();
        String openId = snsAccessToken.getOpenid();

        log.info("snsAccessToken:{},token:{},openId:{}",snsAccessToken,token,openId);
        Example example=new Example(TUser.class);
        example.createCriteria()
                .andEqualTo("tel", query.getMobile());
        example.orderBy("registerDate").asc();
        TUser tUser=tUserMapper.selectByExample(example).get(0);

        if(tUser!=null){
            return Result.success(tUser);
        }
        // 拉取用户信息(需scope为 snsapi_userinfo)
        ApiResult apiResult = SnsApi.getUserInfo(token, openId);
        log.warn("getUserInfo:" + apiResult.getJson());
        if (apiResult.isSucceed()) {
            JSONObject jsonObject = JSON.parseObject(apiResult.getJson());
            tUser.setNickName(jsonObject.getString("nickname"));
            // 用户的性别，值为1时是男性，值为2时是女性，值为0时是未知
            tUser.setSex(jsonObject.getIntValue("sex"));
            tUser.setTenantId(TENANTID);
            tUser.setUId(UUID.randomUUID().toString().replace("-",""));
            tUser.setRegisterDate(new Date());
            tUser.setLevels(1);
            tUser.setSendWeixinNumber(0);
            tUserMapper.insert(tUser);
            return Result.success(tUser);
        }
        return Result.error(ResultEnum.ERROR,"没有用户信息");

    }

    @Override
    public Result<?> bindPhone(OauthQuery query) {
        TUser t=new TUser();
        t.setTel(query.getMobile());
        TUser tUser=tUserMapper.selectOneByExample(t);
        if(null!=tUser){
            tUser.setOpenId(query.getOpenId());
            tUserMapper.updateByPrimaryKey(tUser);
            return Result.success(tUser);
        }
        return Result.error(ResultEnum.ERROR,"绑定失败");
    }
}
