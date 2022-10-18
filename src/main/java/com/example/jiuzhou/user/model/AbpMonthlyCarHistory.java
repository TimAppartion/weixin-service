package com.example.jiuzhou.user.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 包月历史记录
 * @author Appartion
 * @data 2022/10/18
 * 一入代码深似海，从此生活是路人
 */
@Data
public class AbpMonthlyCarHistory {
    private Integer Id;
    private Integer MonthlyCarId;
    private BigDecimal Money;
    private String ParkIds;
    private Date BeginTime;
    private Date EndTime;
    private Integer Status;
    private Date CreationTime;
    private Integer CreatorUserId;
    private String MonthyType;
    private Integer PayStatus;
    private String tranid;
}
