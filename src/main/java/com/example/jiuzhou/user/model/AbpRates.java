package com.example.jiuzhou.user.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author Appartion
 * @data 2022/10/21
 * 一入代码深似海，从此生活是路人
 */
@Data
@Table(name = "AbpRates")
public class AbpRates {
    @javax.persistence.Id
    @Column(name="Id")
    private Integer Id;

    @Column(name="RateName")
    private String RateName;

    @Column(name="RateJson")
    private String RateJson;

    @Column(name="RatePDA")
    private String RatePDA;

    @Column(name="IsActive")
    private Integer IsActive;

    @Column(name="Remark")
    private String Remark;

    @Column(name="TenantId")
    private Integer TenantId;

    @Column(name="CompanyId")
    private Integer CompanyId;

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
