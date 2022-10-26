package com.example.jiuzhou.user.service.impl;

import com.example.jiuzhou.common.utils.DateTimeUtils;
import com.example.jiuzhou.common.utils.Result;
import com.example.jiuzhou.user.mapper.RecordMapper;
import com.example.jiuzhou.user.query.RecordQuery;
import com.example.jiuzhou.user.service.RecordService;
import com.example.jiuzhou.user.view.RecordView;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Appartion
 * @data 2022/10/26
 * 一入代码深似海，从此生活是路人
 */
@Slf4j
@Service
public class RecordServiceImpl implements RecordService {
    @Resource
    private RecordMapper recordMapper;

    @Override
    public Result<?> parkingRecord(RecordQuery query) {
        PageHelper.startPage(query.getPageNumber(),query.getPageSize());
        List<RecordView> list=recordMapper.getListOnlineOrder(query);
        for(RecordView view:list){
            view.setCarStopTime(DateTimeUtils.getTimeDifference2(view.getCarInTime().toString(),view.getCarOutTime().toString()));
        }
        PageInfo pageInfo=new PageInfo(list);
        return Result.success(pageInfo);
    }
}
