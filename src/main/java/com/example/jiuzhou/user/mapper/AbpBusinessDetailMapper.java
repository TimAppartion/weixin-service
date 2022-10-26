package com.example.jiuzhou.user.mapper;

import com.example.jiuzhou.user.model.AbpBusinessDetail;
import com.example.jiuzhou.user.query.ArrearageQuery;
import com.example.jiuzhou.user.view.ArrearageListView;
import com.example.jiuzhou.user.view.ParkOrderView;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author Appartion
 * @data 2022/10/18
 * 一入代码深似海，从此生活是路人
 */
public interface AbpBusinessDetailMapper extends Mapper<AbpBusinessDetail> {
    AbpBusinessDetail getByGuid(String guid);
    List<ParkOrderView> getAllOrder(String uid);

    List<ArrearageListView> arrearageList(@Param("query")ArrearageQuery query);

    List<ArrearageListView> getDJOrder(@Param("plateNumber") String plateNumber);
}
