package com.example.jiuzhou.user.service;

import com.example.jiuzhou.common.utils.Result;
import com.example.jiuzhou.user.query.BindCarQuery;

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

}
