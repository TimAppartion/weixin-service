package com.example.jiuzhou.user.service;

import com.example.jiuzhou.common.utils.Result;
import com.example.jiuzhou.user.query.ArrearageQuery;

/**
 * @author Appartion
 * @data 2022/10/21
 * 一入代码深似海，从此生活是路人
 */
public interface HomePageService {
    /**
     * 再停订单
     * @param uid
     * @return
     */
    Result<?> onlineCar(String uid);

    /**
     * 获取微信sdk
     * @param url
     * @return
     */
    Result<?> getWexSDK(String url);

    /**
     * 欠费订单
     * @param query
     * @return
     */
    Result<?> arrearage(ArrearageQuery query);

    /**
     * 代缴车费页面
     * @param plateNumber
     * @return
     */
    Result<?>getDJOrder(String plateNumber);
}
