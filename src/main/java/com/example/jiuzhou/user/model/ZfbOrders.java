package com.example.jiuzhou.user.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Appartion
 * @data 2022/11/11
 * 一入代码深似海，从此生活是路人
 */
@Data
@Table(name = "ZfbOrders")
public class ZfbOrders {

    @Id
    @Column(name = "out_trade_no")
    private String out_trade_no;

    @Column(name = "uid")
    private String uid;

    @Column(name = "fee")
    private BigDecimal fee;

    @Column(name = "type")
    private Integer type;

    @Column(name = "guid")
    private String guid;

    @Column(name = "plateNumber")
    private String plateNumber;

    @Column(name = "parkId")
    private Integer parkId;

    @Column(name = "monthlyType")
    private String monthlyType;

    @Column(name = "month")
    private Integer month;

    @Column(name = "isMonthlyRenewal")
    private Boolean isMonthlyRenewal;

    @Column(name = "couponId")
    private String couponId;

    @Column(name = "isOver")
    private Integer isOver;

    @Column(name="ordersTime")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date ordersTime;

    @Column(name="overTime")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date overTime;

    @Column(name = "subject")
    private String subject;

    @Column(name = "total_amount")
    private BigDecimal total_amount;

    @Column(name = "monthRecordId")
    private String monthRecordId;

    @Column(name = "userId")
    private String userId;

    @NotNull(message = "支付来源不可为空")
    @Column(name = "payFrom")
    private Integer payFrom;
}
