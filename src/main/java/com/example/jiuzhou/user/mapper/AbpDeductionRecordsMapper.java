package com.example.jiuzhou.user.mapper;

import com.example.jiuzhou.user.model.AbpDeductionRecords;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

/**
 * @author Appartion
 * @data 2022/10/12
 * 一入代码深似海，从此生活是路人
 */
public interface AbpDeductionRecordsMapper extends Mapper<AbpDeductionRecords> {
    void insetOne(@Param("query")AbpDeductionRecords query);
}
