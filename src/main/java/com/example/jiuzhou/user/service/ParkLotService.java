package com.example.jiuzhou.user.service;

import com.example.jiuzhou.common.utils.Result;
import com.example.jiuzhou.user.query.ParkLotQrPayQuery;
import com.example.jiuzhou.user.query.ParkLotQrQuery;
import com.example.jiuzhou.user.query.ParkPassageQuery;

import java.util.Map;

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

    /**
     * 场内扫码支付下单接口
     * https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_1
     * @param query
     * @return
     */
    Result<?> WXParkLotQrPay(ParkLotQrPayQuery query);

    Result<?> WXParkLotQrPayBack(Map<String,String> map);

    Result<?> passageQr(ParkPassageQuery query);

}
