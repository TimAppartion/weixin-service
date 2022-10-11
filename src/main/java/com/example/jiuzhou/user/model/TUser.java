package com.example.jiuzhou.user.model;



import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.Date;

@Data
@Table(name = "TUser")
public class TUser {
    @Column(name="Id")
    private Integer Id;

    @Column(name="UId")
    private String UId;

    @Column(name="TenantId")
    private Integer TenantId;

    @Column(name="nickName")
    private String nickName;

    @Column(name="email")
    private String email;

    @Column(name="tel")
    private String tel;

    @Column(name="qq")
    private String qq;

    @Column(name="password")
    private String password;

    @Column(name="password2")
    private String password2;

    @Column(name="openId")
    private String openId;

    @Column(name="userId")
    private String userId;

    @Column(name="remember")
    private String remember;

    @Column(name="registerDate")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date registerDate;

    @Column(name="lastLoginDate")
    private String lastLoginDate;

    @Column(name="levels")
    private Integer levels;

    @Column(name="CarNumber1")
    private String CarNumber1;

    @Column(name="Model1")
    private Integer Model1;

    @Column(name="CarNumber2")
    private String CarNumber2;

    @Column(name="Model2")
    private Integer Model2;

    @Column(name="CarNumber3")
    private String CarNumber3;

    @Column(name="Model3")
    private Integer Model3;

    @Column(name="SendWeixinDatetime")
    private Date SendWeixinDatetime;

    @Column(name="SendWeixinNumber")
    private Integer SendWeixinNumber;

    @Column(name="CertificateType")
    private String CertificateType;

    @Column(name="Card")
    private String Card;

    @Column(name="CertificateImg")
    private String CertificateImg;

    @Column(name="CertificateReversesImg")
    private String CertificateReversesImg;

    @Column(name="CarPhone")
    private String CarPhone;

    @Column(name="sex")
    private Integer sex;

    @Column(name="birthday")
    private String birthday;
}
