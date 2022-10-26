package com.example.jiuzhou.user.controller;

import com.example.jiuzhou.common.Enum.ResultEnum;
import com.example.jiuzhou.common.utils.Result;
import com.example.jiuzhou.user.query.RecordQuery;
import com.example.jiuzhou.user.service.RecordService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author Appartion
 * @data 2022/10/26
 * 一入代码深似海，从此生活是路人
 */
@Slf4j
@RestController
@RequestMapping("/record")
public class RecordController {
    @Resource
    private RecordService recordService;

    /**
     * 停车记录
     * @param query
     * @return
     */
    @PostMapping("/ParkingRecord")
    public Result<?> parkingRecord(@RequestBody RecordQuery query){
        if(StringUtils.isEmpty(query.getUid())){
            return Result.error(ResultEnum.MISS_DATA);
        }
        if(query.getPageNumber()==null){
            query.setPageNumber(1);
        }
        if(query.getPageSize()==null){
            query.setPageSize(10);
        }
        return recordService.parkingRecord(query);
    }
}
