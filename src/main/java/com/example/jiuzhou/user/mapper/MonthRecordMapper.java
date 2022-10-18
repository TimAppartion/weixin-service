package com.example.jiuzhou.user.mapper;

import com.example.jiuzhou.user.model.MonthRecord;
import tk.mybatis.mapper.common.Mapper;

/**
 * @author Appartion
 * @data 2022/10/17
 * 一入代码深似海，从此生活是路人
 */
public interface MonthRecordMapper extends Mapper<MonthRecord> {
    MonthRecord getById(String id);
}
