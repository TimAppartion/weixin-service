package com.example.jiuzhou.user.mapper;

import com.example.jiuzhou.user.model.AbpWeixinConfig;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

@Repository
public interface AbpWeixinConfigMapper extends Mapper<AbpWeixinConfig> {
    Integer getIdByTenantId(@Param("Id")Integer id);
}
