package com.example.jiuzhou.user.query;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Appartion
 * @data 2022/12/30
 * 一入代码深似海，从此生活是路人
 * 扫码支付
 */
@Data
public class ParkLotQrPayQuery {
    /**
     * 车牌号
     */
    private String plateNumber;

    /**
     * 订单号
     */
    private String guid;

    private String openId;

    private String userId;

    private Integer parkId;

    private BigDecimal money;
}
