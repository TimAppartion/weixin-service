package com.example.jiuzhou.user.controller;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.*;
import com.example.jiuzhou.common.utils.Result;
import com.example.jiuzhou.user.mapper.ZfbOrdersMapper;
import com.example.jiuzhou.user.query.ZhiFuBaoPayQuery;
import com.example.jiuzhou.user.service.ZhiFuBaoService;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;


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


    @Resource
    private ZfbOrdersMapper zfbOrdersMapper;

    @Resource
    private ZhiFuBaoService zhiFuBaoService;
    /**
     * 支付宝支付 返回链接
     *
     * @return
     */
    @RequestMapping("/pay")
    public Result<?> pay(@RequestBody ZhiFuBaoPayQuery query)throws Exception{

        return zhiFuBaoService.pay(query);

    }

    /**
     * 支付宝pc网页支付下单
     * @param query
     * @return
     */
    @RequestMapping("/payByFrom")
    @Transactional
    public Result<?> payByFrom(@RequestBody ZhiFuBaoPayQuery query){
        return zhiFuBaoService.payByFrom(query);
    }

    /**
     * 支付宝手机网页下单返回支付链接
     * @param query
     * @throws Exception
     */
    @RequestMapping("/aliPay")
    public Result<?> aliPay(@RequestBody ZhiFuBaoPayQuery query) throws Exception {

        log.info("支付宝订单支付参数：{}",query);
        query.getBody().setOut_trade_no(query.getOut_trade_no());
        query.getBody().setOrdersTime(new Date());
        query.getBody().setIsOver(1);
        query.getBody().setTotal_amount(query.getTotal_amount());
        query.getBody().setSubject(query.getSubject());
        return zhiFuBaoService.aliPay(query);
    }


    @RequestMapping("/verity")
    public String verity(@RequestParam Map<String,String> params) throws AlipayApiException {
        log.info("支付宝激活开发者模式验签参数:{}",params);
        return "true";
    }


    @RequestMapping("/callback")
    public String callback(@RequestParam Map<String, String> params){
        String var=JSONObject.toJSONString(params);
        log.info("支付宝支付异步通知返回：{}",var);
        return zhiFuBaoService.callback(params);

    }
}
