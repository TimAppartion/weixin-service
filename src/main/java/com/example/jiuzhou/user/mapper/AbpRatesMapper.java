package com.example.jiuzhou.user.mapper;

import com.example.jiuzhou.user.model.AbpRates;
import tk.mybatis.mapper.common.Mapper;

/**
 * @author Appartion
 * @data 2022/10/24
 * 一入代码深似海，从此生活是路人
 */
public interface AbpRatesMapper extends Mapper<AbpRates> {
    AbpRates getById(Integer id);
}
