package com.example.jiuzhou.user.service;

import com.example.jiuzhou.common.utils.Result;
import com.example.jiuzhou.user.query.BindCarQuery;
import com.example.jiuzhou.user.query.OrderQuery;
import com.example.jiuzhou.user.query.RealNameQuery;
import com.example.jiuzhou.user.query.UpdateUserQuery;
import tk.mybatis.mapper.annotation.Order;

/**
 * @author Appartion
 * @data 2022/10/13
 * 一入代码深似海，从此生活是路人
 */
public interface MineService {
    /**
     * 绑定车牌号
     * @param query
     * @return
     */
    Result<?> saveBingCarNumber(BindCarQuery query);

    /**
     * 解绑车牌
     * @param query
     * @return
     */
    Result<?> relievePlatNumber(BindCarQuery query);

    /**
     * 用户个人信息
     * @param uid
     * @return
     */
    Result<?> userInfo(String uid);

    /**
     * 个人月卡信息
     * @param uid
     * @return
     */
    Result<?> monthCad(String uid);

    /**
     * 获取月卡详细
     * @param parkType
     * @return
     */
    Result<?> getMonthlyCardDetail(String parkType);

    /**
     * 订单查询
     * @param query
     * @return
     */
    Result<?> orderList(OrderQuery query);

    /**
     * 修改用户个人信息
     * @param query
     * @return
     */
    Result<?>updateUser(UpdateUserQuery query);


    /**
     *
     * @param query
     * @return
     */
    Result<?>realName(RealNameQuery query);

}
