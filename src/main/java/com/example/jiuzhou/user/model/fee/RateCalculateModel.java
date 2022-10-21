package com.example.jiuzhou.user.model.fee;

import lombok.Data;

/**
 * @author Appartion
 * @data 2022/10/21
 * 一入代码深似海，从此生活是路人
 */
@Data
public class RateCalculateModel {
    /// <summary>
    /// 计算金额
    /// </summary>
    public double CalculateMoney ;
    /// <summary>
    /// 停车时长
    /// </summary>
    public double ParkTime ;
    /// <summary>
    /// 异常信息
    /// </summary>
    public String exceptionMsg;
}
