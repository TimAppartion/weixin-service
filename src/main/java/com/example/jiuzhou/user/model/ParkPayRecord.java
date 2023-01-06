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
@Table(name = "ParkPayRecord")
public class ParkPayRecord {

    @javax.persistence.Id
    @Column(name="Id")
    private Integer Id;

    @Column(name="TenantId")
    private Integer TenantId;

    @Column(name="Money")
    private BigDecimal Money;

    @Column(name="ParkadeAccessId")
    private Integer ParkadeAccessId;

    @Column(name="OpenId")
    private String OpenId;

    @Column(name="UserId")
    private String UserId;

    @Column(name="PlateNumber")
    private String PlateNumber;

    @Column(name="CreationTime")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date CreationTime;


    @Column(name = "transaction_id")
    private String transactionId;
}
