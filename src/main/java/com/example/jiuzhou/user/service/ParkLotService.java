package com.example.jiuzhou.user.service;

import com.example.jiuzhou.common.utils.Result;
import com.example.jiuzhou.user.query.ParkLotQrQuery;

/**
 * @author Appartion
 * @data 2022/12/29
 * 一入代码深似海，从此生活是路人
 */
public interface ParkLotService {
    /**
     * 场内扫码查询订单
     * @param query
     * @return
     */
    Result<?> parkLotQr (ParkLotQrQuery query);
}
