package com.example.jiuzhou.user.controller;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.*;
import com.example.jiuzhou.common.utils.Result;
import com.example.jiuzhou.user.mapper.ZfbOrdersMapper;
import com.example.jiuzhou.user.model.ZfbOrders;
import com.example.jiuzhou.user.query.ZhiFuBaoPayQuery;
import com.example.jiuzhou.user.service.ZhiFuBaoService;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
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



    @Resource
    private ZfbOrdersMapper zfbOrdersMapper;

    @Resource
    private ZhiFuBaoService zhiFuBaoService;
    /**
     * 支付宝支付 返回链接
     * 生成二维码
     * @return
     */
    @RequestMapping("/pay")
    @Transactional( rollbackFor = Exception.class)
    public Result<?> pay(@RequestBody ZhiFuBaoPayQuery query)throws Exception{
        log.info("支付宝手持机生成二维码链接:{}",query);
        String out_trade_no = UUID.randomUUID().toString();
        query.setOut_trade_no(out_trade_no);
        query.getBody().setOut_trade_no(out_trade_no);
        query.getBody().setOrdersTime(new Date());
        query.getBody().setIsOver(1);
        query.getBody().setTotal_amount(query.getTotal_amount());
        query.getBody().setSubject(query.getSubject());
        query.getBody().setFee(query.getTotal_amount());
        zfbOrdersMapper.insert(query.getBody());
        return zhiFuBaoService.pay(query);
    }
    @GetMapping("/tradeQuery")
    public Result<?> tradeQuery(@RequestParam(value = "out_trade_no", required = false) String out_trade_no){
        if(StringUtils.isEmpty(out_trade_no)){
            return Result.error("订单号不可为空");
        }
        return zhiFuBaoService.tradeQuery(out_trade_no);
    }

    /**
     * 支付宝支付扫码付钱
     * @param query
     * @return
     */
    @RequestMapping("/tradePay")
    public Result<?> tradePay(@RequestBody ZhiFuBaoPayQuery query){
        log.info("支付宝手持机扫码支付收钱：{}",query);
        if(StringUtils.isEmpty(query.getAuth_code())){
            return Result.error("付款码不可为空");
        }
        String out_trade_no=UUID.randomUUID().toString();
        query.setOut_trade_no(out_trade_no);
        query.getBody().setOut_trade_no(out_trade_no);
        query.getBody().setOrdersTime(new Date());
        query.getBody().setIsOver(1);
        query.getBody().setTotal_amount(query.getTotal_amount());
        query.getBody().setSubject(query.getSubject());
        query.getBody().setFee(query.getTotal_amount());
        zfbOrdersMapper.insert(query.getBody());
        return zhiFuBaoService.tradePay(query);
    }

    @RequestMapping("/writeOrders")
    public Result<?> writeOrders(@RequestBody ZfbOrders query){
        log.info("支付宝手持机写入支付:{}",query);
        query.setIsOver(1);
        query.setOrdersTime(new Date());
        query.setFee(query.getFee());
        zfbOrdersMapper.insert(query);
        return Result.success("支付宝订单记录写入成功");
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
