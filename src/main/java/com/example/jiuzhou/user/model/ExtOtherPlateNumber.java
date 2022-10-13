package com.example.jiuzhou.user.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author Appartion
 * @data 2022/10/13
 * 一入代码深似海，从此生活是路人
 */
@Data
@Table(name = "ExtOtherPlateNumber")
public class ExtOtherPlateNumber {
    @Column(name="Id")
    private Integer Id;

    @Column(name="AssignedOtherAccountId")
    private Integer AssignedOtherAccountId;

    @Column(name="PlateNumber")
    private String PlateNumber;

    @Column(name="CarColor")
    private Integer CarColor;

    @Column(name="CarType")
    private Integer CarType;

    @Column(name="Order")
    private Integer Order;

    @Column(name="IsActive")
    private Integer IsActive;

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

    @Column(name="Authentication")
    private Integer Authentication;

    @Column(name="AuthenticationType")
    private String AuthenticationType;

    @Column(name="CarNumberColor")
    private String CarNumberColor;

    @Column(name="CertificateImg")
    private String CertificateImg;

    @Column(name="CertificateReversesImg")
    private String CertificateReversesImg;
}
