package com.example.jiuzhou.user.query;

import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Appartion
 * @data 2022/10/28
 * 一入代码深似海，从此生活是路人
 */
@Data
public class ZhiFuBaoPayQuery {
    private String out_trade_no;
    private String body;

    private String total_amount;
    private String subject;
}
