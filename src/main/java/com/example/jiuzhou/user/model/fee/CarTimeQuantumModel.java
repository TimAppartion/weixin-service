package com.example.jiuzhou.user.model.fee;

import lombok.Data;

/**
 * @author Appartion
 * @data 2022/10/21
 * 一入代码深似海，从此生活是路人
 */
@Data
public class CarTimeQuantumModel {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /// <summary>
    /// 开始时间
    /// </summary>
    public double beginTime ;
    /// <summary>
    ///
    /// 结束时间
    /// </summary>
    public double endTime ;
    /// <summary>
    /// 收费方式
    /// </summary>
    public String RateMethod ;
    /// <summary>
    /// 收费金额
    /// </summary>
    public String TimeQuantumMoney ;
}
