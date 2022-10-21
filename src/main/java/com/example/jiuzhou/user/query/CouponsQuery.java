package com.example.jiuzhou.user.query;

import lombok.Data;

@Data
public class CouponsQuery {
    private String uid;
    private Integer status;
    private Integer type;
    private Integer couponsType;
    private String fee;
    private String other;
}
