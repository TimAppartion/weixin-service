package com.example.jiuzhou.user.query;

import lombok.Data;

@Data
public class BerthsecsQuery {
    private String lng;
    private String lat;
    private String parkingName;
    private Integer ParkType;
    private Integer pageIndex;
    private Integer pageSize;
}
