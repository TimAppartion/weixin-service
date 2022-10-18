package com.example.jiuzhou.user.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.util.Date;

/**
 * @author Appartion
 * @data 2022/10/18
 * 一入代码深似海，从此生活是路人
 */
@Data
public class AbpRemoteGuids {

    @javax.persistence.Id
    @GeneratedValue(generator = "JDBC" ,strategy = GenerationType.IDENTITY)
    @Column(name="Id")
    private Integer Id;

    @Column(name="BusinessDetailGuid")
    private String BusinessDetailGuid;

    @Column(name="CreationTime")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date CreationTime;

    @Column(name="CreatorUserId")
    private Integer CreatorUserId;

    @Column(name="IsActive")
    private Integer IsActive;

    @Column(name="BerthsecId")
    private Integer BerthsecId;

    @Column(name="DeviceCode")
    private String DeviceCode;

    @Column(name="EmployeeId")
    private Integer EmployeeId;

    @Column(name="UpdateTime")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date UpdateTime;
}
