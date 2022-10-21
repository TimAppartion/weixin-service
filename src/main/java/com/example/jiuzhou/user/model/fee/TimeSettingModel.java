package com.example.jiuzhou.user.model.fee;

import lombok.Data;

/**
 * @author Appartion
 * @data 2022/10/21
 * 一入代码深似海，从此生活是路人
 */
@Data
public class TimeSettingModel {
    /// <summary>
    /// 车辆类型  	0 所有   1	 大型车   2 小型车  3 	摩托车
    /// </summary>
    public String CarType;
    /// <summary>
    ///收费方式（0 按时  1 按次）
    /// </summary>
    public String RateMethod;

    public String beginTime;

    public String endTime;
    /// <summary>
    /// 停车多天计费方式 0  按停车总和  1  分天计算
    /// </summary>
    public String ManyDayMethod;
}
