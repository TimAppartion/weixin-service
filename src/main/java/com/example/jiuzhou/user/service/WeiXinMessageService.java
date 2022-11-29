package com.example.jiuzhou.user.service;

import com.example.jiuzhou.common.utils.Result;
import com.example.jiuzhou.user.query.WeiXinMessageQuery;

/**
 * @author Appartion
 * @data 2022/10/14
 * 一入代码深似海，从此生活是路人
 */
public interface WeiXinMessageService {
    /**
     * 停车缴费成功通知
     * @param query
     * @return
     */
    Result<?> SendParkPay(WeiXinMessageQuery query);

    /**
     * 车辆入场通知
     * @param query
     * @return
     */
    Result<?> SendInPark(WeiXinMessageQuery query);


    /**
     * 车辆出场通知
     * @param query
     * @return
     */
    Result<?> SendOutPark(WeiXinMessageQuery query);

    /**
     * 获取微信token 过期自动替换为新的
     * @return
     */
    Result<?> getWeiXinToken();
}
