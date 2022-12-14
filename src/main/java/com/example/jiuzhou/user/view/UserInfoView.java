package com.example.jiuzhou.user.view;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Appartion
 * @data 2022/10/17
 * 一入代码深似海，从此生活是路人
 */
@Data
@JsonInclude(JsonInclude.Include.ALWAYS)
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

    private String certificateImg;
    private String certificateReversesImg;
    private String card;
    private String certificateType;
}
