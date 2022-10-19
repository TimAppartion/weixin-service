package com.example.jiuzhou.user.view;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Appartion
 * @data 2022/10/17
 * 一入代码深似海，从此生活是路人
 */
@Data
public class UserInfoView {
    private String birthday;
    private Integer Coupon;
    private Integer Id;
    private Integer monthCard;
    private String NickName;
    private Integer sex;
    private String tel;
    private BigDecimal Wallet;

    private String headImgUrl;
    private String openId;
    private String userId;
    private String uid;
}
