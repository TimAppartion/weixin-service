package com.example.jiuzhou.user.mapper;

import com.example.jiuzhou.user.model.AbpBerthsecs;
import com.example.jiuzhou.user.query.BerthsQuery;
import com.example.jiuzhou.user.view.AbpBerthsecsView;
import com.example.jiuzhou.user.view.QueryOrderView;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface AbpBerthsecsMapper extends Mapper<AbpBerthsecs> {

    AbpBerthsecs getById(@Param("id")Integer id);

    List<AbpBerthsecsView> nearSite(@Param("query") BerthsQuery query);

    QueryOrderView queryOrder(Integer id);
}
