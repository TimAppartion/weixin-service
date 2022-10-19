package com.example.jiuzhou.user.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author Appartion
 * @data 2022/10/14
 * 一入代码深似海，从此生活是路人
 */
@Data
@Table(name = "AbpParks")
public class AbpParks {
    @javax.persistence.Id
    @Column(name="Id")
    private Integer Id;

    @Column(name="RegionId")
    private Integer RegionId;

    @Column(name="ParkName")
    private String ParkName;

    @Column(name="ParkType")
    private Integer ParkType;

    @Column(name="Mark")
    private String Mark;

    @Column(name="BerthCount")
    private Integer BerthCount;

    @Column(name="TenantId")
    private Integer TenantId;

    @Column(name="CompanyId")
    private Integer CompanyId;

    @Column(name="Address")
    private String Address;

    @Column(name="X")
    private String X;

    @Column(name="Y")
    private String Y;

    @Column(name="BeginNumber")
    private String BeginNumber;

    @Column(name="EndNumber")
    private String EndNumber;

    @Column(name="OtherNumber")
    private String OtherNumber;

    @Column(name="ZhiBoParkId")
    private String ZhiBoParkId;

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
}
