package com.example.jiuzhou.user.mapper;

import com.example.jiuzhou.user.model.AbpDeductionRecords;
import com.example.jiuzhou.user.query.OrderQuery;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author Appartion
 * @data 2022/10/12
 * 一入代码深似海，从此生活是路人
 */
public interface AbpDeductionRecordsMapper extends Mapper<AbpDeductionRecords> {

    void insetOne(@Param("query")AbpDeductionRecords query);

    List<AbpDeductionRecords> getOrderList(@Param("query")OrderQuery query);
}
