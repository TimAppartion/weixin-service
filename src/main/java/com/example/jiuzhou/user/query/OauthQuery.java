package com.example.jiuzhou.user.query;

import lombok.Data;

@Data
public class OauthQuery {
    private String code;
    private String mobile;
    private String openId;
    private String userId;
    private String nickName;
    private String headImgUrl;
    private Integer sex;

    private Integer smsType;
}
