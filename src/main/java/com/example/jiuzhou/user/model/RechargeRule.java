package com.example.jiuzhou.user.model;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * @author Appartion
 * @data 2022/10/17
 * 一入代码深似海，从此生活是路人
 */
@Data
@Table(name = "RechargeRule")
public class RechargeRule {

    @javax.persistence.Id
    @Column(name="Id")
    private Integer Id;

    @Column(name="Money")
    private BigDecimal Money;

    @Column(name="Donation")
    private BigDecimal Donation;

    @Column(name="TenantId")
    private Integer TenantId;

    @Column(name="IsActive")
    private Integer IsActive;

    @Column(name="type")
    private Integer type;
}
