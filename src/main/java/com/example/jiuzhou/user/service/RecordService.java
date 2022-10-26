package com.example.jiuzhou.user.service;

import com.example.jiuzhou.common.utils.Result;
import com.example.jiuzhou.user.query.RecordQuery;

/**
 * @author Appartion
 * @data 2022/10/26
 * 一入代码深似海，从此生活是路人
 */
public interface RecordService {
    /**
     * 停车记录
     * @param query
     * @return
     */
    Result<?> parkingRecord(RecordQuery query);
}
