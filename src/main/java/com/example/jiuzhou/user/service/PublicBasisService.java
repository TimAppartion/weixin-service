package com.example.jiuzhou.user.service;

import com.example.jiuzhou.common.utils.Result;
import com.example.jiuzhou.user.query.*;

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

    /**
     * 停车记录发送短信
     * @param query
     * @return
     */
    Result<?> saveOnlineCar(SaveOnlineCarQuery query);

    /**
     * 在停订单 微信支付后消息推送
     * @param query
     * @return
     */
    Result<?> onlineCarSendMsg(OnlineCarSendMsgQuery query);

    /**
     * 处理在线补缴
     * @param totalFee
     * @param guid
     * @param depositCard
     * @param uid
     * @param fee
     */
    void payment(Integer totalFee,String guid,Integer depositCard,String uid,BigDecimal fee);

    /**
     * 自主结单
     * @param guid
     * @param fee
     * @param uid
     * @param total_fee
     * @param depositCard
     */
    void statement(String guid,BigDecimal fee,String uid,Integer total_fee,Integer depositCard,Integer payFrom);

    /**
     * 包月缴费
     * @param device_info
     * @param uid
     * @param total_fee
     * @param depositCard
     * @throws ParseException
     */
    void monthPay(String device_info,String uid,Integer total_fee,Integer depositCard,Integer payFrom) throws ParseException;

    /**
     * 账号充值
     * @param money
     * @param uid
     */
    void saveRecharge(BigDecimal money , String uid,Integer payFrom);

    /**
     * 包月校验
     * @param isMonthlyRenewal
     * @param plateNumber
     * @param parkId
     * @return
     */
    Result<?> checkMonthlyCar(boolean isMonthlyRenewal,String plateNumber,Integer parkId);

    /**
     * 手持机扫码支付
     * @param query
     * @return
     */
    Result<?> scanCode(WeiXinScanCodeQuery query);

    /**
     * 订单写入系统
     * @param query
     * @return
     */
    Result<?> weiXinWriteOrder(WeiXinWriteOrderQuery query);
}
