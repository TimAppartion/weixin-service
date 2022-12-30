package com.example.jiuzhou.user.query;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author Appartion
 * @data 2022/12/29
 * 一入代码深似海，从此生活是路人
 * 场内扫码query
 */
@Data
public class ParkLotQrQuery {

    @NotNull(message = "通道id不可为空")
    private int passageId;

    @NotNull(message = "车场Id不可为空")
    private int parkId;

    @NotNull(message = "车牌号不可为空")
    private String PlateNumber;

}
