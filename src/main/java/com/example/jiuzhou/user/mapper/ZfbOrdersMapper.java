package com.example.jiuzhou.user.mapper;

import com.example.jiuzhou.user.model.ZfbOrders;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

/**
 * @author Appartion
 * @data 2022/11/11
 * 一入代码深似海，从此生活是路人
 */
public interface ZfbOrdersMapper extends Mapper<ZfbOrders> {
    ZfbOrders getByOutTradeNo(@Param("outTradeNo")String outTradeNo );
}
