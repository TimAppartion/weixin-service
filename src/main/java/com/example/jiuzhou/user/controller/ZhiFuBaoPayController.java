package com.example.jiuzhou.user.controller;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.*;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Map;
import java.util.UUID;

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
    private static String PID=prop.get("pid");

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
            return Result.error(ResultEnum.ERROR, response);
        }
    }

    @RequestMapping("/payByFrom")
    @Transactional
    public Result<?> payByFrom(@RequestBody ZhiFuBaoPayQuery query) throws Exception{
        AlipayClient alipayClient = new DefaultAlipayClient(APP_URL,APP_ID,APP_PRIVATE_KEY,"json",CHARSEt,APP_PUBLIC_KEY,"RSA2");


        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        //支付宝公共参数
        request.setNotifyUrl(NOTIFY_URL);
        request.setReturnUrl(RETURN_URL);
        //面向对象封装业务参数
        AlipayTradePagePayModel model =new AlipayTradePagePayModel();
        model.setOutTradeNo(query.getOut_trade_no());
        model.setTotalAmount(query.getTotal_amount().toString());
        model.setSubject(query.getSubject());
        model.setProductCode("FAST_INSTANT_TRADE_PAY");
        request.setBizModel(model);
        //执行请求,调用支付宝
        AlipayTradePagePayResponse response = alipayClient.pageExecute(request);
        if (response.isSuccess()) {
            log.info("调用成功,返回结果:[{}]",response);
            return Result.success(response);
        } else {
            log.info("调用失败!!");
        }
        return null;
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
    public String callback(@RequestBody Map<String, String> params){
        log.info("支付宝支付异步通知返回：{}",params);
        String result = "failure";
        try {
            //异步通知验签
            boolean signVerified = AlipaySignature.rsaCheckV1(params,
                    APP_PUBLIC_KEY,
                    AlipayConstants.CHARSET_UTF8,
                    AlipayConstants.SIGN_TYPE_RSA2);
            if (!signVerified) {
                // TODO 验签失败则记录异常日志，并在response中返回failure.
                log.error("支付成功,异步通知验签失败!");
                return result;
            }
            log.info("支付成功,异步通知验签成功!");
            //TODO 验签成功后，按照支付结果异步通知中的描述，对支付结果中的业务内容进行二次校验
            //1.验证out_trade_no 是否为商家系统中创建的订单号
            String outTradeNo = params.get("out_trade_no");
            //2.判断 total_amount 是否确实为该订单的实际金额
            String totalAmount = params.get("total_amount");
            //3.校验通知中的 seller_id是否为 out_trade_no 这笔单据的对应的操作方
            String sellerId = params.get("seller_id");
            if (!sellerId.equals(PID)) {
                log.error("商家PID校验失败");
                return result;
            }
            //4.验证 app_id 是否为该商家本身
            String appId = params.get("app_id");
            if (!appId.equals(APP_ID)){
                log.error("app_id校验失败");
                return result;
            }
            //在支付宝的业务通知中，只有交易通知状态为 TRADE_SUCCESS 或 TRADE_FINISHED 时，支付宝才会认定为买家付款成功
            String tradeStatus = params.get("trade_status");
            if (!"TRADE_SUCCESS".equals(tradeStatus) && !"TRADE_FINISHED".equals(tradeStatus)){
                log.error("支付未成功");
                return result;
            }

            //TODO 处理自身业务


            result = "success";
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }

        return result;
    }
}
