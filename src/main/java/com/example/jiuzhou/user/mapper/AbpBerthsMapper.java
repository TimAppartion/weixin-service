package com.example.jiuzhou.user.mapper;

import com.example.jiuzhou.user.model.AbpBerths;
import tk.mybatis.mapper.common.Mapper;

/**
 * @author Appartion
 * @data 2022/10/18
 * 一入代码深似海，从此生活是路人
 */
public interface AbpBerthsMapper extends Mapper<AbpBerths> {
    AbpBerths getByGuid(String guid);
}
