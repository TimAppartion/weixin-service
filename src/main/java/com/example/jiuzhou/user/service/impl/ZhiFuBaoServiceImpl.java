package com.example.jiuzhou.user.service.impl;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipaySystemOauthTokenRequest;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import com.example.jiuzhou.common.Enum.ResultEnum;
import com.example.jiuzhou.common.utils.Result;
import com.example.jiuzhou.user.query.OauthQuery;
import com.example.jiuzhou.user.service.ZhiFuBaoService;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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


    @Override
    public Result<?> index(OauthQuery query) {
        AlipayClient alipayClient = new DefaultAlipayClient(APP_URL,APP_ID,APP_PRIVATE_KEY,"json",CHARSEt,APP_PUBLIC_KEY,"RSA2");
        AlipaySystemOauthTokenRequest request = new AlipaySystemOauthTokenRequest();
        request.setCode(query.getCode());
        request.setGrantType("authorization_code");
        try {
            AlipaySystemOauthTokenResponse response = alipayClient.execute(request);
            return Result.success(response);
        }catch (Exception e){
            e.printStackTrace();
            return Result.error(ResultEnum.ERROR,"获取支付宝授权信息失败");
        }
    }
}
