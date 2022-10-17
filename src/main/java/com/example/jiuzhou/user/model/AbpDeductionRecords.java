package com.example.jiuzhou.user.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 缴费记录表
 * @author Appartion
 * @data 2022/10/12
 * 一入代码深似海，从此生活是路人
 */
@Data
@Table(name = "AbpDeductionRecords")
public class AbpDeductionRecords {
    @Column(name="Id")
    private Integer Id;

    @Column(name="OtherAccountId")
    private Integer OtherAccountId;

    @Column(name="OperType")
    private Integer OperType;

    @Column(name="Money")
    private BigDecimal Money;

    @Column(name="PayStatus")
    private Integer PayStatus;

    @Column(name="InTime")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date InTime;

    @Column(name="Remark")
    private String Remark;

    @Column(name="CardNo")
    private String CardNo;

    @Column(name="EmployeeId")
    private Integer EmployeeId;

    @Column(name="UserId")
    private Integer UserId;

    @Column(name="PlateNumber")
    private String PlateNumber;

    @Column(name="TenantId")
    private Integer TenantId;

    @Column(name="CompanyId")
    private Integer CompanyId;

    @Column(name="BeginMoney")
    private BigDecimal BeginMoney;

    @Column(name="EndMoney")
    private BigDecimal EndMoney;
}
