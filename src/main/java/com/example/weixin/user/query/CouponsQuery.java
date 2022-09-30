package com.example.weixin.user.query;

import lombok.Data;

@Data
public class CouponsQuery {
    private String userId;
    private String status;
    private String type;
    private String CouponsType;
    private String fee;
    private String other;
}
