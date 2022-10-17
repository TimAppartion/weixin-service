package com.example.jiuzhou.user.mapper;

import com.example.jiuzhou.user.model.Weixinorders;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

/**
 * @author Appartion
 * @data 2022/10/17
 * 一入代码深似海，从此生活是路人
 */
public interface WeixinordersMapper extends Mapper<Weixinorders> {

    Weixinorders getByTransactionId(@Param("transactionId")String transactionId);
}
