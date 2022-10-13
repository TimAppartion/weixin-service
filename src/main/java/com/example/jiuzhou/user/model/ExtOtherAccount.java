package com.example.jiuzhou.user.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Appartion
 * @data 2022/10/12
 * 一入代码深似海，从此生活是路人
 */
@Data
@Table(name = "ExtOtherAccount")
public class ExtOtherAccount {
    @Column(name="Id")
    private Integer Id;

    @Column(name="IsEnabled")
    private Integer IsEnabled;

    @Column(name="EnabledTime")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date EnabledTime;

    @Column(name="EnabledUserId")
    private String EnabledUserId;

    @Column(name="CompanyId")
    private Integer CompanyId;

    @Column(name="AuthenticationSource")
    private String AuthenticationSource;

    @Column(name="CardNo")
    private String CardNo;

    @Column(name="ProductNo")
    private String ProductNo;

    @Column(name="Wallet")
    private BigDecimal Wallet;

    @Column(name="Client")
    private String Client;

    @Column(name="UserName")
    private String UserName;

    @Column(name="Name")
    private String Name;

    @Column(name="Password")
    private String Password;

    @Column(name="TelePhone")
    private String TelePhone;

    @Column(name="IsPhoneConfirmed")
    private Integer IsPhoneConfirmed;

    @Column(name="AccountLoginDatetime")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date AccountLoginDatetime;

    @Column(name="IsActive")
    private Integer IsActive;

    @Column(name="IsLock")
    private Integer IsLock;

    @Column(name="PhoneCode")
    private String PhoneCode;

    @Column(name="CodeTime")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date CodeTime;

    @Column(name="CreatorUserId")
    private Integer CreatorUserId;

    @Column(name="CreationTime")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date CreationTime;

    @Column(name="LastModificationTime")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date LastModificationTime;

    @Column(name="LastModifierUserId")
    private Integer LastModifierUserId;

    @Column(name="DeleterUserId")
    private Integer DeleterUserId;

    @Column(name="DeletionTime")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date DeletionTime;

    @Column(name="IsDeleted")
    private Integer IsDeleted;

    @Column(name="TenantId")
    private Integer TenantId;

    @Column(name="SendSmsDatetime")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date SendSmsDatetime;

    @Column(name="SendSmsNumber")
    private Integer SendSmsNumber;

    @Column(name="AutoDeduction")
    private Integer AutoDeduction;

    @Column(name="u_id")
    private String uId;
}
