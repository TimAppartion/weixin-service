package com.example.jiuzhou.user.mapper;

import com.example.jiuzhou.user.model.AbpWeixinMsg;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

/**
 * @author Appartion
 * @data 2022/11/30
 * 一入代码深似海，从此生活是路人
 */
public interface AbpWeixinMsgMapper extends Mapper<AbpWeixinMsg> {
    void insertOne(@Param("query")AbpWeixinMsg query);
}
