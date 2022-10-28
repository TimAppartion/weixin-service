package com.example.jiuzhou.user.controller;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConfig;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.example.jiuzhou.common.Enum.ResultEnum;
import com.example.jiuzhou.common.utils.Result;
import com.example.jiuzhou.user.model.AbpWeixinConfig;
import com.example.jiuzhou.user.query.RecordQuery;
import com.example.jiuzhou.user.query.ZhiFuBaoPayQuery;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.redis.Redis;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * @author Appartion
 * @data 2022/10/28
 * 一入代码深似海，从此生活是路人
 */
@Slf4j
@RestController
@RequestMapping("/zfb")
public class ZhiFuBaoPayController {
    private static final Prop prop = PropKit.use("zfb.properties");
    private static String APP_ID=prop.get("app_id");
    private static String APP_PRIVATE_KEY=prop.get("app_private_key");
    private static String APP_PUBLIC_KEY=prop.get("app_public_key");
    private static String CHARSEt=prop.get("charset");
    private static String APP_URL=prop.get("app_url");
    private static String NOTIFY_URL=prop.get("notify_url");
    private static String RETURN_URL=prop.get("return_url");

    /**
     * 支付宝支付 返回链接
     *
     * @return
     */
    @RequestMapping("/pay")
    public Result<?> pay(@RequestBody ZhiFuBaoPayQuery query)throws Exception{

        //获得初始化的AlipayClient
        AlipayClient alipayClient = new DefaultAlipayClient(APP_URL,APP_ID, APP_PRIVATE_KEY,
                "json", "UTF-8", APP_PUBLIC_KEY,"RSA2");

        AlipayTradePrecreateRequest alipayRequest = new AlipayTradePrecreateRequest();
        alipayRequest.setReturnUrl(RETURN_URL);
        alipayRequest.setNotifyUrl(NOTIFY_URL);


        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", query.getOut_trade_no());
        bizContent.put("total_amount", query.getTotal_amount());
        bizContent.put("subject", query.getSubject());
        bizContent.put("body", query.getBody());

        alipayRequest.setBizContent(bizContent.toString());
        AlipayTradePrecreateResponse response = alipayClient.execute(alipayRequest);

        if(response.isSuccess()){
            return Result.success(response);
        } else {
            return Result.error(ResultEnum.ERROR, response.getMsg());
        }
    }


    /**
     * 下单返回支付页面
     * @param response
     * @param query
     * @throws Exception
     */
    @RequestMapping("/alipayPay")
    public void alipayPay(HttpServletResponse response, @RequestBody ZhiFuBaoPayQuery query) throws Exception {


        //获得初始化的AlipayClient
        AlipayClient alipayClient = new DefaultAlipayClient(APP_URL,APP_ID, APP_PRIVATE_KEY,
                "json", "UTF-8", APP_PUBLIC_KEY,"RSA2");
        //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(RETURN_URL);
        alipayRequest.setNotifyUrl(NOTIFY_URL);

        alipayRequest.setBizContent("{\"out_trade_no\":\"" + query.getOut_trade_no() + "\","
                + "\"total_amount\":\"" + query.getTotal_amount() + "\","
                + "\"subject\":\"" + query.getSubject() + "\","
                + "\"body\":\"" + query.getBody() + "\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        //请求
        String result = alipayClient.pageExecute(alipayRequest).getBody();
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        //alipayClient.pageExecute(alipayRequest).getQrCode();
        out.write(result);

    }
    @RequestMapping("/callback")
    public Result<?>callback(){
        return Result.success();
    }
}
