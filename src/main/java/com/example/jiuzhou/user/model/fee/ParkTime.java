package com.example.jiuzhou.user.model.fee;

import lombok.Data;

import java.util.Date;

/**
 * @author Appartion
 * @data 2022/10/21
 * 一入代码深似海，从此生活是路人
 */
@Data
public class ParkTime {
    /// <summary>
    /// 当天停车开始时间
    /// </summary>
    public Date beginTime ;
    /// <summary>
    /// 当天停车结束时间
    /// </summary>
    public Date endTime ;
    /// <summary>
    /// 当天开始时间段
    /// </summary>
    public double quantumBegin ;
    /// <summary>
    /// 当天结束时间段
    /// </summary>
    public double quantumEnd ;
    /// <summary>
    ///当天停车总时长
    /// </summary>
    public double timeTotal ;
    //停车费用
    public double parkMoney ;
    //当天已收金额
    public double alreadyCharge ;
}
