package com.example.jiuzhou.user.mapper;

import com.example.jiuzhou.user.model.AbpBerthsecs;
import com.example.jiuzhou.user.query.BerthsecsQuery;
import com.example.jiuzhou.user.view.AbpBerthsecsView;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface AbpBerthsecsMapper extends Mapper<AbpBerthsecs> {
    List<AbpBerthsecsView> nearSite(@Param("query") BerthsecsQuery query);
}
