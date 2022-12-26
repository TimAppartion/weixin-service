package com.example.jiuzhou.user.service;

import com.example.jiuzhou.common.utils.Result;
import com.example.jiuzhou.user.model.CouponsDetails;
import com.example.jiuzhou.user.query.ComputeMoneyQuery;

import java.math.BigDecimal;
import java.util.List;

public interface CouponsService {
    List<CouponsDetails> getList();

    /**
     * 计算扣除优惠券后的金额
     * @param fee
     * @param uid
     * @param couponId
     * @return
     */
    Result<?> canUseCoupon(BigDecimal fee, String uid, String couponId);

    /**
     * 使用优惠券
     * @param couponId
     * @param status
     */
    void useCoupon(String couponId,Integer status);
}
