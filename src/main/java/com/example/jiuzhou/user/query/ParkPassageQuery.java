package com.example.jiuzhou.user.query;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author Appartion
 * @data 2023/1/3
 * 一入代码深似海，从此生活是路人
 */
@Data
public class ParkPassageQuery {
    @NotNull(message = "通道id不可为空")
    private Integer passageId;

    @NotNull(message = "车场Id不可为空")
    private Integer parkId;


    private String plateNumber;
}
