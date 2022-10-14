package com.example.jiuzhou.user.query;

import lombok.Data;

@Data
public class OauthQuery {
    private String code;
    private String mobile;
    private String openId;
    private String userId;
    private String uid;
    private Integer smsType;
}
