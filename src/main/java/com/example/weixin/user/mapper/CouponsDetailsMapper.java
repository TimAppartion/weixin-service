package com.example.weixin.user.mapper;

import com.example.weixin.user.model.CouponsDetails;
import com.example.weixin.user.query.CouponsQuery;
import com.example.weixin.user.view.CouponsView;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CouponsDetailsMapper {
    List<CouponsDetails> getList();

    List<CouponsView> getCouponsList(@Param("query") CouponsQuery query);
}
