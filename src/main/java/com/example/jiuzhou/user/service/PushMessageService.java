package com.example.jiuzhou.user.service;

import com.example.jiuzhou.common.utils.Result;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Appartion
 * @data 2022/10/18
 * 一入代码深似海，从此生活是路人
 */
public interface PushMessageService {
    /**
     * 发送包月推送消息
     * @param openId
     * @param Money
     * @param CarNumber
     * @param isRenewal
     */
    void sendMonthlyCarRenewalMsg(String openId, BigDecimal Money, String CarNumber, boolean isRenewal);

    /**
     * 发送缴费成功短信
     *
     * @param openId
     * @param money
     * @param plateNumber
     * @param berthNumber
     * @param stopTime
     * @return
     */
    Result<?> sendMsgOrder(String openId, BigDecimal money, String plateNumber, String berthNumber, String stopTime);

    /**
     * 微信支付后消息推送
     * @param openId
     * @param plateNumber
     * @param berthNumber
     * @param payType
     * @param money
     * @param stopTime
     * @param tel
     * @param carOutTime
     * @return
     */
    Result<?> SendMsgOrderOut(String openId, String plateNumber, String berthNumber, String payType, BigDecimal money, String stopTime, String tel, Date carOutTime);
}
