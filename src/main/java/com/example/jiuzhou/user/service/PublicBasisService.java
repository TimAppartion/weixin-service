package com.example.jiuzhou.user.service;

import com.example.jiuzhou.common.utils.Result;
import com.example.jiuzhou.user.query.BalancePayQuery;
import com.example.jiuzhou.user.query.OpinionQuery;
import com.example.jiuzhou.user.query.WeiXinPayQuery;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Map;

/**
 * @author Appartion
 * @data 2022/10/17
 * 一入代码深似海，从此生活是路人
 */
public interface PublicBasisService {
    /**
     * 微信支付统一下单接口
     * @param query
     * @return
     */
    Result<?> WeiXinPay(WeiXinPayQuery query);

    /**
     * 微信支付回调接口
     * @param map
     * @return
     */
    Result<?> weiXinCallBack(Map<String,String> map) throws ParseException;

    /**
     * 车辆出场
     * @param guid
     * @param money
     */
    void carOut(String guid, BigDecimal money);

    /**
     * 车辆连续包月
     * @param uid
     * @param plateNumber
     * @param fee
     * @param parkId
     * @param month
     * @param monthlyType
     */
    void monthlyCar(String uid,String plateNumber,BigDecimal fee,Integer parkId,Integer month,String monthlyType) throws ParseException;

    /**
     * 余额支付接口
     * @param query
     * @return
     */
    Result<?> balancePay(BalancePayQuery query) throws ParseException;

    /**
     * 新增意见接口
     * @param query
     * @return
     */
    Result<?> insertOpinion(OpinionQuery query);
}
