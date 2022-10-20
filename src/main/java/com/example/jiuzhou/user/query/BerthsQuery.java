package com.example.jiuzhou.user.query;

import lombok.Data;

@Data
public class BerthsQuery {
    private String lng;
    private String lat;
    private String parkingName;
    private Integer parkType;
    private Integer pageIndex;
    private Integer pageSize;
}
