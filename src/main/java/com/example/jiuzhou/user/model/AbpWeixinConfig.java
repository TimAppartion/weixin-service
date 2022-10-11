package com.example.jiuzhou.user.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import java.util.Date;

@Data
public class AbpWeixinConfig {
    @Column(name="Id")
    private Integer Id;

    @Column(name="AppId")
    private String AppId;

    @Column(name="AppSecret")
    private String AppSecret;

    @Column(name="Token")
    private String Token;

    @Column(name="EncodingAESKey")
    private String EncodingAESKey;

    @Column(name="CreationTime")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date CreationTime;

    @Column(name="TenantId")
    private Integer TenantId;

    @Column(name="PayUrl")
    private String PayUrl;

    @Column(name="BackPayUrl")
    private String BackPayUrl;

    @Column(name="AppName")
    private String AppName;

    @Column(name="webAppId")
    private String webAppId;

    @Column(name="encryptMessage")
    private String encryptMessage;

    @Column(name="mch_id")
    private String mch_id;

    @Column(name="paternerKey")
    private String paternerKey;

    @Column(name="subscribe_rul")
    private String subscribe_rul;

    @Column(name="domain")
    private String domain;

    @Column(name="BerthStatus")
    private Integer BerthStatus;

    @Column(name="BerthDetail")
    private Integer BerthDetail;

    @Column(name="ZappId")
    private String ZappId;

    @Column(name="privateKey")
    private String privateKey;

    @Column(name="alipayPulicKey")
    private String alipayPulicKey;

    @Column(name="serverUrl")
    private String serverUrl;

    @Column(name="MonthlyPayment")
    private Integer MonthlyPayment;

    @Column(name="DepositCard")
    private Integer DepositCard;

    @Column(name="RecoverMoneny")
    private Integer RecoverMoneny;

    @Column(name="OnlineOrdering")
    private Integer OnlineOrdering;

}
