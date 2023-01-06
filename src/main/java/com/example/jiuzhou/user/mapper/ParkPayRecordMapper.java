package com.example.jiuzhou.user.mapper;


import com.example.jiuzhou.user.model.ParkPayRecord;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

/**
 * @author Appartion
 * @data 2022/12/30
 * 一入代码深似海，从此生活是路人
 */
public interface ParkPayRecordMapper extends Mapper<ParkPayRecord> {
    void insertOne(@Param("query") ParkPayRecord query);

    ParkPayRecord getByTransactionId(@Param("transactionId") String transactionId);
}
