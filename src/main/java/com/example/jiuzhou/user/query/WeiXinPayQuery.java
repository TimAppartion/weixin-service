package com.example.jiuzhou.user.query;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Appartion
 * @data 2022/10/17
 * 一入代码深似海，从此生活是路人
 */
@Data
public class WeiXinPayQuery {
    private String uid;
    private String openId;
    private BigDecimal fee;
    private Integer type;
    private String guid;
    private String plateNumber;
    private Integer parkId;
    private String monthlyType;
    private Integer month;
    private Boolean isMonthlyRenewal;
    private String couponId;
}
