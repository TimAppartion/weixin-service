package com.example.jiuzhou.user.mapper;

import com.example.jiuzhou.user.query.RecordQuery;
import com.example.jiuzhou.user.view.RecordView;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Appartion
 * @data 2022/10/26
 * 一入代码深似海，从此生活是路人
 */
public interface RecordMapper {
    List<RecordView> getListOnlineOrder(@Param("query")RecordQuery query);
}
