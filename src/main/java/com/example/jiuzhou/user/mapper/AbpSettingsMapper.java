package com.example.jiuzhou.user.mapper;

import com.example.jiuzhou.user.model.AbpSettings;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface AbpSettingsMapper extends Mapper<AbpSettings> {
    AbpSettings getByName(@Param("name") String name,@Param("TenantId")Integer TenantId);
}
