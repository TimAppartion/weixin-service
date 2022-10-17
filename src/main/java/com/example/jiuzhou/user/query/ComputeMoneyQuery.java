package com.example.jiuzhou.user.query;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Appartion
 * @data 2022/10/17
 * 一入代码深似海，从此生活是路人
 */
@Data
public class ComputeMoneyQuery {
    private String uid;
    private Integer couponId;
    private BigDecimal fee;
}
