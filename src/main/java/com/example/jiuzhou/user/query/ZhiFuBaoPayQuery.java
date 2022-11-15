package com.example.jiuzhou.user.query;

import com.example.jiuzhou.user.model.ZfbOrders;
import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author Appartion
 * @data 2022/10/28
 * 一入代码深似海，从此生活是路人
 */
@Data
public class ZhiFuBaoPayQuery {
    @NotNull(message = "交易订单不可为空")
    private String out_trade_no;
    private ZfbOrders body;

    /**
     * 订单金额
     */
    @NotNull(message = "订单金额不可为空")
    private BigDecimal total_amount;
    /**
     * 订单名称
     */
    private String subject;

    /**
     * 评价的订单id
     */
    private String orderId;
    private String quit_url;
}
