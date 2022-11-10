package com.example.jiuzhou.user.query;

import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

/**
 * @author Appartion
 * @data 2022/10/28
 * 一入代码深似海，从此生活是路人
 */
@Data
public class ZhiFuBaoPayQuery {
    private String out_trade_no;
    private String body;

    /**
     * 订单金额
     */
    private BigDecimal total_amount;
    /**
     * 订单名称
     */
    private String subject;
}
