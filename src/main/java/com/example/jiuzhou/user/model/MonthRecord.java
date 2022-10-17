package com.example.jiuzhou.user.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Id;
import java.math.BigDecimal;

/**
 * @author Appartion
 * @data 2022/10/17
 * 一入代码深似海，从此生活是路人
 */
@Data
@Table(name = "MonthRecord")
public class MonthRecord {

    @Id
    @Column(name="Id")
    private String Id;

    @Column(name="openId")
    private String openId;

    @Column(name="plateNumber")
    private String plateNumber;

    @Column(name="monthly_total_fee")
    private BigDecimal monthly_total_fee;

    @Column(name="parkId")
    private Integer parkId;

    @Column(name="month")
    private Integer month;

    @Column(name="monthlyType")
    private String monthlyType;

    @Column(name="uid")
    private String uid;
}
