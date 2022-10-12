package com.example.jiuzhou.user.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "AbpBerthsecs")
public class AbpBerthsecs {
    @Column(name="Id")
    private Integer Id;

    @Column(name="BerthsecName")
    private String BerthsecName;


    @Column(name="BeginNumeber")
    private Integer BeginNumeber;

    @Column(name="EndNumeber")
    private Integer EndNumeber;

    @Column(name="CustomNumeber")
    private String CustomNumeber;

    @Column(name="CheckInStatus")
    private Integer CheckInStatus;

    @Column(name="CheckStatus")
    private Integer CheckStatus;

    @Column(name="CheckOutStatus")
    private Integer CheckOutStatus;

    @Column(name="CheckInteger")
    private Integer CheckInteger;

    @Column(name="CheckInEmployeeId")
    private Integer CheckInEmployeeId;


    @Column(name="CheckOutTime")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date CheckOutTime;

    @Column(name="CheckOutEmployeeId")
    private Integer CheckOutEmployeeId;

    @Column(name="CheckInDeviceCode")
    private String CheckInDeviceCode;

    @Column(name="CheckOutDeviceCode")
    private String CheckOutDeviceCode;

    @Column(name="XPoInteger")
    private String XPoInteger;

    @Column(name="YPoInteger")
    private String YPoInteger;

    @Column(name="RegionId")
    private Integer RegionId;

    @Column(name="ParkId")
    private Integer ParkId;

    @Column(name="TenantId")
    private Integer TenantId;

    @Column(name="IsActive")
    private Integer IsActive;

    @Column(name="UseStatus")
    private Integer UseStatus;

    @Column(name="CompanyId")
    private Integer CompanyId;

    @Column(name="RateId")
    private Integer RateId;

    @Column(name="RateId1")
    private Integer RateId1;

    @Column(name="RateId2")
    private Integer RateId2;

    @Column(name="BerthCount")
    private String BerthCount;

    @Column(name="PushStatus")
    private Integer PushStatus;

    @Column(name="Lat")
    private String Lat;

    @Column(name="Lng")
    private String Lng;

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

    @Column(name="SignoCommunationTime")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date SignoCommunationTime;

    @Column(name="SignoInSize")
    private Integer SignoInSize;

    @Column(name="SignoOutSize")
    private Integer SignoOutSize;

    @Column(name="Distance_")
    private float Distance_;
}
