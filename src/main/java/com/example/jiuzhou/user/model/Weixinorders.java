package com.example.jiuzhou.user.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import javax.persistence.Id;

/**
 * @author Appartion
 * @data 2022/10/17
 * 一入代码深似海，从此生活是路人
 */
@Data
@Table(name = "Weixinorders")
public class Weixinorders {
    @Id
    @Column(name="Id")
    private Integer Id;

    @Column(name="appid")
    private String appid;

    @Column(name="out_trade_no")
    private String out_trade_no;

    @Column(name="openId")
    private String openId;

    @Column(name="mch_id")
    private String mch_id;

    @Column(name="cash_fee")
    private Integer cash_fee;

    @Column(name="total_fee")
    private Integer total_fee;

    @Column(name="fee_type")
    private String fee_type;

    @Column(name="result_code")
    private String result_code;

    @Column(name="err_code")
    private String err_code;

    @Column(name="is_subscribe")
    private String is_subscribe;

    @Column(name="trade_type")
    private String trade_type;

    @Column(name="bank_type")
    private String bank_type;

    @Column(name="transaction_id")
    private String transaction_id;

    @Column(name="coupon_id")
    private String coupon_id;

    @Column(name="coupon_fee")
    private Integer coupon_fee;

    @Column(name="coupon_count")
    private Integer coupon_count;

    @Column(name="attach")
    private String attach;

    @Column(name="time_end")
    private String time_end;

    @Column(name="couresCount")
    private Integer couresCount;

    @Column(name="couresId")
    private Integer couresId;

    @Column(name="url")
    private String url;

    @Column(name="TenantId")
    private Integer TenantId;

    @Column(name="CompanyId")
    private Integer CompanyId;

    @Column(name="uid")
    private String uid;
}
