package com.example.jiuzhou.user.service.impl;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipaySystemOauthTokenRequest;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import com.example.jiuzhou.common.Enum.ResultEnum;
import com.example.jiuzhou.common.utils.Result;
import com.example.jiuzhou.user.query.OauthQuery;
import com.example.jiuzhou.user.service.ZhiFuBaoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ZhiFuBaoServiceImpl implements ZhiFuBaoService {
    @Value("${ZhiFuBao.app_id}")
    private static String APP_ID;

    @Value("${ZhiFuBao.app_private_key}")
    private static String APP_PRIVATE_KEY;

    @Value("${ZhiFuBao.app_public_key}")
    private static String APP_PUBLIC_KEY;

    @Value("${ZhiFuBao.charset}")
    private static String CHARSEt;

    @Value("${ZhiFuBao.app_url}")
    private static String APP_URL;

    @Value("${ZhiFuBao.notify_url}")
    private static String NOTIFY_URL;

    @Value("${ZhiFuBao.return_url}")
    private static String RETURN_URL;


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
