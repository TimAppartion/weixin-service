package com.example.jiuzhou.user.model.fee;

import lombok.Data;

import java.util.List;

/**
 * @author Appartion
 * @data 2022/10/21
 * 一入代码深似海，从此生活是路人
 */
@Data
public class CarRateModel {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /// <summary>
    /// 车辆类型
    /// </summary>
    public String CarType ;

    /// <summary>
    /// 免费时间
    /// </summary>
    public String FreeTime ;

    public Boolean ContentFreeTimeFlag;
    /// <summary>
    /// 日最大收费金额 0表示无最大收费金额
    /// </summary>
    public double DayMaxMoney;
    /// <summary>
    /// 次最大收费金额 0表示无最大收费金额
    /// </summary>
    public double OnceMaxMoney;
    /// <summary>
    /// 时间段列表
    /// </summary>
    public List<CarTimeQuantumModel> CarTimeQuantumList;
    /// <summary>
    /// 标准收费列表
    /// </summary>
    public List<CarFeeScaleModel> CarFeeScaleList ;


    public List<CarFeeModel> CarFeeList ;
}
