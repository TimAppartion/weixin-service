package com.example.jiuzhou.user.mapper;

import com.example.jiuzhou.user.model.CouponsDetails;
import com.example.jiuzhou.user.query.CouponsQuery;
import com.example.jiuzhou.user.view.CouponsView;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface CouponsDetailsMapper extends Mapper<CouponsDetails> {
    List<CouponsDetails> getList();

    List<CouponsView> getCouponsList(@Param("query") CouponsQuery query);

    CouponsView getDetails(@Param("id")Integer id);

}
