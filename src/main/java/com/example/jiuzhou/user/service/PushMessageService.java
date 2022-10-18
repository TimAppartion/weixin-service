package com.example.jiuzhou.user.service;

import java.math.BigDecimal;

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
}
