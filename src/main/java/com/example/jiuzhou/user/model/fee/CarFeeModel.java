package com.example.jiuzhou.user.model.fee;

import lombok.Data;

/**
 * @author Appartion
 * @data 2022/10/21
 * 一入代码深似海，从此生活是路人
 */
@Data
public class CarFeeModel {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /// <summary>
    /// 收费时间
    /// </summary>
    public String RateTime ;
    /// <summary>
    /// 收费金额
    /// </summary>
    public String RateMoney ;
    /// <summary>
    /// 收费方式 	0  分钟  1 小时  2	车次
    /// </summary>
    public String RateMethod ;

    /// <summary>
    /// 0 前 1 每  ps：前10分钟0元 每30分钟2元
    /// </summary>
    public String TimeType ;
}
