package com.example.jiuzhou.user.query;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Appartion
 * @data 2022/10/25
 * 一入代码深似海，从此生活是路人
 */
@Data
public class OnlineCarSendMsgQuery {
    private String openId;
    private BigDecimal money;
    private String guid;
    private Integer day;
    private Integer hours;
    private Integer minute;
}
