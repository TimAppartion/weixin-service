package com.example.jiuzhou.user.mapper;

import com.example.jiuzhou.user.model.ExtOtherPlateNumber;
import com.example.jiuzhou.user.view.MineCarView;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author Appartion
 * @data 2022/10/12
 * 一入代码深似海，从此生活是路人
 */
public interface ExtOtherPlateNumberMapper extends Mapper<ExtOtherPlateNumber> {
    void  insertOne(@Param("query")ExtOtherPlateNumber query);
    List<MineCarView> carList(@Param("uid") String uid);
}
