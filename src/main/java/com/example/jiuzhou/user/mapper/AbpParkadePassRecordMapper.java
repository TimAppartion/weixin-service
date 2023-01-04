package com.example.jiuzhou.user.mapper;

import com.example.jiuzhou.user.model.AbpParkadePassRecord;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;
/**
 * @author Appartion
 * @data 2022/12/29
 * 一入代码深似海，从此生活是路人
 */
public interface AbpParkadePassRecordMapper extends Mapper<AbpParkadePassRecord>{
    AbpParkadePassRecord getByPassageId(@Param("PassageId")Integer PassageId);
}
