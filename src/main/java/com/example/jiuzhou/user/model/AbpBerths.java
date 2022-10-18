package com.example.jiuzhou.user.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Appartion
 * @data 2022/10/18
 * 一入代码深似海，从此生活是路人
 */
@Data
public class AbpBerths {
    @javax.persistence.Id
    @GeneratedValue(generator = "JDBC" ,strategy = GenerationType.IDENTITY)
    @Column(name="Id")
    private Integer Id;

    @Column(name="BerthNumber")
    private String BerthNumber;

    @Column(name="BerthStatus")
    private String BerthStatus;

    @Column(name="RelateNumber")
    private String RelateNumber;

    @Column(name="CarType")
    private Integer CarType;

    @Column(name="InCarTime")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date InCarTime;

    @Column(name="OutCarTime")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date OutCarTime;

    @Column(name="RegionId")
    private Integer RegionId;

    @Column(name="ParkId")
    private Integer ParkId;

    @Column(name="SensorNumber")
    private String SensorNumber;

    @Column(name="SensorsInCarTime")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date SensorsInCarTime;

    @Column(name="SensorsOutCarTime")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date SensorsOutCarTime;

    @Column(name="ParkStatus")
    private Integer ParkStatus;

    @Column(name="StopType")
    private Integer StopType;

    @Column(name="SensorBeatTime")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date SensorBeatTime;

    @Column(name="BerthsecId")
    private Integer BerthsecId;

    @Column(name="TenantId")
    private Integer TenantId;

    @Column(name="LastModificationTime")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date LastModificationTime;

    @Column(name="LastModifierUserId")
    private Integer LastModifierUserId;

    @Column(name="CreatorUserId")
    private Integer CreatorUserId;

    @Column(name="CreationTime")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date CreationTime;

    @Column(name="IsActive")
    private Integer IsActive;

    @Column(name="CompanyId")
    private Integer CompanyId;

    @Column(name="guid")
    private String guid;

    @Column(name="SensorGuid")
    private String SensorGuid;

    @Column(name="CardNo")
    private String CardNo;

    @Column(name="Prepaid")
    private BigDecimal Prepaid;

    @Column(name="IsSourceVideo")
    private Integer IsSourceVideo;

    @Column(name="IsFaultFlag")
    private Integer IsFaultFlag;
}
