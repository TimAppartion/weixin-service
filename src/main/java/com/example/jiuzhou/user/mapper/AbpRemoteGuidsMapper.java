package com.example.jiuzhou.user.mapper;

import com.example.jiuzhou.user.model.AbpRemoteGuids;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

/**
 * @author Appartion
 * @data 2022/10/18
 * 一入代码深似海，从此生活是路人
 */
public interface AbpRemoteGuidsMapper extends Mapper<AbpRemoteGuids> {
    void insertOne(@Param("query")AbpRemoteGuids guids);
}
