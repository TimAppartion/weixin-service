package com.example.jiuzhou.user.query;

import lombok.Data;

import java.util.Date;

/**
 * @author Appartion
 * @data 2022/10/17
 * 一入代码深似海，从此生活是路人
 */
@Data
public class OrderQuery {
    private String uid;
    private Date startTime;
    private Date endTime;
    private Integer pageNumber;
    private Integer pageSize;
}
