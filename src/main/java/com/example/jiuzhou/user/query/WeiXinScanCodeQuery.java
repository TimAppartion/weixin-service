package com.example.jiuzhou.user.query;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Appartion
 * @data 2022/11/16
 * 一入代码深似海，从此生活是路人
 */
@Data
public class WeiXinScanCodeQuery {
    private String auth_code;
    private BigDecimal total_fee;
    private String guid;
    private Integer PayType;
    private String out_trade_no;

}
