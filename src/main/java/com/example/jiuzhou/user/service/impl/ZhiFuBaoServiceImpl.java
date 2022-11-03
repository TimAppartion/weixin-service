package com.example.jiuzhou.user.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipaySystemOauthTokenRequest;
import com.alipay.api.request.AlipayUserInfoShareRequest;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import com.alipay.api.response.AlipayUserInfoShareResponse;
import com.example.jiuzhou.common.Enum.ResultEnum;
import com.example.jiuzhou.common.utils.Result;
import com.example.jiuzhou.user.mapper.TUserMapper;
import com.example.jiuzhou.user.model.TUser;
import com.example.jiuzhou.user.query.OauthQuery;
import com.example.jiuzhou.user.service.ZhiFuBaoService;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class ZhiFuBaoServiceImpl implements ZhiFuBaoService {

    private static final Prop prop = PropKit.use("zfb.properties");
    private static String APP_ID=prop.get("app_id");
    private static String APP_PRIVATE_KEY=prop.get("app_private_key");
    private static String APP_PUBLIC_KEY=prop.get("app_public_key");
    private static String CHARSEt=prop.get("charset");
    private static String APP_URL=prop.get("app_url");
    private static String NOTIFY_URL=prop.get("notify_url");
    private static String RETURN_URL=prop.get("return_url");

    @Resource
    private TUserMapper tUserMapper;
    @Override
    public Result<?> index(OauthQuery query) {
        AlipayClient alipayClient = new DefaultAlipayClient(APP_URL,APP_ID,APP_PRIVATE_KEY,"json",CHARSEt,APP_PUBLIC_KEY,"RSA2");
        AlipaySystemOauthTokenRequest request = new AlipaySystemOauthTokenRequest();
        request.setCode(query.getCode());
        request.setGrantType("authorization_code");

        //返回前端的结果参数
        Map<String ,Object> responseMap=new HashMap<>();
        //授权获取token
        try {
            AlipaySystemOauthTokenResponse response = alipayClient.execute(request);
            log.info("zfbOauthToken"+response);
            if(response.isSuccess()){
                AlipayUserInfoShareResponse userInfoShareResponse = alipayClient.execute(new AlipayUserInfoShareRequest(),response.getAccessToken());
                log.info("zfbShare:"+userInfoShareResponse);
                if(userInfoShareResponse.isSuccess()){
                    JSONObject result = JSONObject.parseObject(userInfoShareResponse.getBody());
                    JSONObject userInfo=result.getJSONObject("alipay_user_info_share_response");
//                    if(!userInfo.isEmpty() && userInfo.getInteger("code").intValue() ==1000){
                    responseMap.put("user_id",userInfo.get("user_id"));
                    responseMap.put("nick_name",userInfo.get("nick_name"));
                    responseMap.put("header_img",userInfo.get("avatar"));
                    return Result.success(responseMap);
//                    }
                }
                return Result.success(userInfoShareResponse);
            }
            return Result.success(response);
        }catch (Exception e){
            e.printStackTrace();
            return Result.error(ResultEnum.ERROR,"获取支付宝授权信息失败");
        }
    }
}
