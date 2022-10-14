package com.example.jiuzhou.user.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * @author Appartion
 * @data 2022/10/14
 * 一入代码深似海，从此生活是路人
 */

@Data
@Table(name = "MonthRule")
public class MonthRule {
    @Column(name="Id")
    private Integer Id;

    @Column(name="TenantId")
    private Integer TenantId;

    @Column(name="ParkId")
    private Integer ParkId;

    @Column(name="ParkName")
    private String ParkName;

    @Column(name="MonthMoney")
    private BigDecimal MonthMoney;

    @Column(name="YearMoney")
    private BigDecimal YearMoney;

    @Column(name="IsActive")
    private Integer IsActive;
}
