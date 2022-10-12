package com.example.jiuzhou.user.mapper;

import com.example.jiuzhou.user.view.MineCarView;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Appartion
 * @data 2022/10/12
 * 一入代码深似海，从此生活是路人
 */
public interface ExtOtherPlateNumberMapper {
    List<MineCarView> carList(@Param("uid") String uid);
}
