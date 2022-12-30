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
 * @data 2022/12/30
 * 一入代码深似海，从此生活是路人
 */
@Data
@Table(name = "AbpParkadeAccessDetail")
public class AbpParkadeAccessDetail {
    @javax.persistence.Id
    @Column(name="Id")
    private Integer Id;

    @Column(name="ParkId")
    private Integer ParkId;

    @Column(name="BerthsecId")
    private Integer BerthsecId;

    @Column(name="InEquipmentNumber")
    private String InEquipmentNumber;

    @Column(name="OutEquipmentNumber")
    private String OutEquipmentNumber;

    @Column(name="Receivable")
    private BigDecimal Receivable;

    @Column(name="PlateNumber")
    private String PlateNumber;

    @Column(name="CarInTime")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date CarInTime;

    @Column(name="CarOutTime")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date CarOutTime;

    @Column(name="StopTime")
    private Integer StopTime;

    @Column(name="guid")
    private String guid;

    @Column(name="OssPathURL")
    private String OssPathURL;

    @Column(name="OutOssPathURL")
    private String OutOssPathURL;

    @Column(name="DetailOssPathURL")
    private String DetailOssPathURL;

    @Column(name="DetailOutOssPathURL")
    private String DetailOutOssPathURL;

    @Column(name="CreationTime")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date CreationTime;
}
