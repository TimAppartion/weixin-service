package com.example.jiuzhou.user.model.fee;

import lombok.Data;

import java.util.List;

/**
 * @author Appartion
 * @data 2022/10/21
 * 一入代码深似海，从此生活是路人
 */
@Data
public class RateModel {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public RateModel() {

    }

    public int Id;
    /// <summary>
    /// 费率名称
    /// </summary>
    public String RateName;
    /// <summary>
    /// 是否启用
    /// </summary>
    public Boolean IsActive;
    /// <summary>
    /// 备注
    /// </summary>
    public String Remark;


    public List<CarRateModel> CarRateList;


    public List<TimeSettingModel> TimeSettingList;
}
