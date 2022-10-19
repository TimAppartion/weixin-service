package com.example.jiuzhou.user.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 包月历史记录
 * @author Appartion
 * @data 2022/10/18
 * 一入代码深似海，从此生活是路人
 */
@Data
@Table(name="AbpMonthlyCarHistory")
public class AbpMonthlyCarHistory {
    @javax.persistence.Id
    @Column(name="Id")
    private Integer Id;

    @Column(name="MonthlyCarId")
    private Integer MonthlyCarId;

    @Column(name="Money")
    private BigDecimal Money;

    @Column(name="ParkIds")
    private String ParkIds;

    @Column(name="BeginTime")
    private Date BeginTime;

    @Column(name="EndTime")
    private Date EndTime;

    @Column(name="Status")
    private Integer Status;

    @Column(name="CreationTime")
    private Date CreationTime;

    @Column(name="CreatorUserId")
    private Integer CreatorUserId;

    @Column(name="MonthyType")
    private String MonthyType;

    @Column(name="PayStatus")
    private Integer PayStatus;

    @Column(name="tranid")
    private String tranid;
}
