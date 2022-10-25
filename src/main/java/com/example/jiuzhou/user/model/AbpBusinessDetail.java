package com.example.jiuzhou.user.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Appartion
 * @data 2022/10/18
 * 一入代码深似海，从此生活是路人
 */
@Data
@Table(name = "AbpBusinessDetail")
public class AbpBusinessDetail {
    @javax.persistence.Id
    @GeneratedValue(generator = "JDBC" ,strategy = GenerationType.IDENTITY)
    @Column(name="Id")
    private Integer Id;

    @Column(name="BerthNumber")
    private String BerthNumber;

    @Column(name="PlateNumber")
    private String PlateNumber;

    @Column(name="OcrPlateNumber")
    private String OcrPlateNumber;

    @Column(name="CarType")
    private Integer CarType;

    @Column(name="Prepaid")
    private BigDecimal Prepaid;

    @Column(name="PrepaidPayStatus")
    private Integer PrepaidPayStatus;

    @Column(name="PrepaidCarNo")
    private String PrepaidCarNo;

    @Column(name="Receivable")
    private BigDecimal Receivable;

    @Column(name="Money")
    private BigDecimal Money;

    @Column(name="FactReceive")
    private BigDecimal FactReceive;

    @Column(name="Arrearage")
    private BigDecimal Arrearage;

    @Column(name="BeforeDiscount")
    private BigDecimal BeforeDiscount;

    @Column(name="DiscountMoney")
    private BigDecimal DiscountMoney;

    @Column(name="DiscountRate")
    private double DiscountRate;

    @Column(name="CarInTime")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date CarInTime;

    @Column(name="CarOutTime")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date CarOutTime;

    @Column(name="CarPayTime")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date CarPayTime;

    @Column(name="InOperaId")
    private Integer InOperaId;

    @Column(name="InDeviceCode")
    private String InDeviceCode;

    @Column(name="OutOperaId")
    private Integer OutOperaId;

    @Column(name="OutDeviceCode")
    private String OutDeviceCode;

    @Column(name="ChargeOperaId")
    private Integer ChargeOperaId;

    @Column(name="ChargeDeviceCode")
    private String ChargeDeviceCode;

    @Column(name="guid")
    private String guid;

    @Column(name="SensorsInCarTime")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date SensorsInCarTime;

    @Column(name="SensorsOutCarTime")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date SensorsOutCarTime;

    @Column(name="SensorsStopTime")
    private Integer SensorsStopTime;

    @Column(name="SensorsReceivable")
    private BigDecimal SensorsReceivable;

    @Column(name="Repayment")
    private BigDecimal Repayment;

    @Column(name="CarRepaymentTime")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date CarRepaymentTime;

    @Column(name="PaymentType")
    private Integer PaymentType;

    @Column(name="EscapeTime")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date EscapeTime;

    @Column(name="EscapePayStatus")
    private Integer EscapePayStatus;

    @Column(name="IsEscapePay")
    private Integer IsEscapePay;

    @Column(name="EscapeOperaId")
    private Integer EscapeOperaId;

    @Column(name="EscapeUserId")
    private Integer EscapeUserId;

    @Column(name="EscapeDeviceCode")
    private String EscapeDeviceCode;

    @Column(name="EscapeTenantId")
    private Integer EscapeTenantId;

    @Column(name="EscapeCompanyId")
    private Integer EscapeCompanyId;

    @Column(name="PayStatus")
    private Integer PayStatus;

    @Column(name="ReceivableCarNo")
    private String ReceivableCarNo;

    @Column(name="OtherAccountId")
    private Integer OtherAccountId;

    @Column(name="IsPay")
    private Integer IsPay;

    @Column(name="StopType")
    private Integer StopType;

    @Column(name="FeeType")
    private Integer FeeType;

    @Column(name="StopTime")
    private Integer StopTime;

    @Column(name="TenantId")
    private Integer TenantId;

    @Column(name="CompanyId")
    private Integer CompanyId;

    @Column(name="RegionId")
    private Integer RegionId;

    @Column(name="ParkId")
    private Integer ParkId;

    @Column(name="BerthsecId")
    private Integer BerthsecId;

    @Column(name="Status")
    private Integer Status;

    @Column(name="AppAccountId")
    private Integer AppAccountId;

    @Column(name="IsLock")
    private Integer IsLock;

    @Column(name="EmployeeId")
    private Integer EmployeeId;

    @Column(name="ReceivableOtherAccountId")
    private Integer ReceivableOtherAccountId;

    @Column(name="EscapeCardNo")
    private String EscapeCardNo;

    @Column(name="EscapeOtherAccountId")
    private Integer EscapeOtherAccountId;

    @Column(name="ElectronicOrderid")
    private Integer ElectronicOrderid;

    @Column(name="IsDeleted")
    private Integer IsDeleted;

    @Column(name="DeleterUserId")
    private Integer DeleterUserId;

    @Column(name="DeletionTime")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date DeletionTime;

    @Column(name="LastModificationTime")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date LastModificationTime;

    @Column(name="LastModifierUserId")
    private Integer LastModifierUserId;

    @Column(name="CreationTime")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date CreationTime;

    @Column(name="CreatorUserId")
    private Integer CreatorUserId;

    @Column(name="InBatchNo")
    private String InBatchNo;

    @Column(name="OutBatchNo")
    private String OutBatchNo;

    @Column(name="PaymentBatchNo")
    private String PaymentBatchNo;

    @Column(name="Evaluate")
    private String Evaluate;

    @Column(name="isChecked")
    private String isChecked;

    @Column(name="Price")
    private String Price;

}
