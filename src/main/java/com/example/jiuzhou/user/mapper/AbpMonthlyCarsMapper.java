package com.example.jiuzhou.user.mapper;

import com.example.jiuzhou.user.model.AbpMonthlyCars;
import com.example.jiuzhou.user.view.MonthCarsView;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author Appartion
 * @data 2022/10/14
 * 一入代码深似海，从此生活是路人
 */
public interface AbpMonthlyCarsMapper extends Mapper<AbpMonthlyCars> {

    List<MonthCarsView> monthCad(@Param("uid")String uid);
}
