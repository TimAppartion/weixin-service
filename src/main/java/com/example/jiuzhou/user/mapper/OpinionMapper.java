package com.example.jiuzhou.user.mapper;

import com.example.jiuzhou.user.model.Opinion;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

/**
 * @author Appartion
 * @data 2022/10/19
 * 一入代码深似海，从此生活是路人
 */
public interface OpinionMapper extends Mapper<Opinion> {
    void  insertOne(@Param("query")Opinion query);
}
