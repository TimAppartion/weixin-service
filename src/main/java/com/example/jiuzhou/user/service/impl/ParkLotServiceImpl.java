package com.example.jiuzhou.user.service.impl;

import com.example.jiuzhou.common.utils.Result;
import com.example.jiuzhou.user.mapper.AbpParkadePassRecordMapper;
import com.example.jiuzhou.user.query.ParkLotQrQuery;
import com.example.jiuzhou.user.service.ParkLotService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author Appartion
 * @data 2022/12/29
 * 一入代码深似海，从此生活是路人
 */
@Service
@Slf4j
public class ParkLotServiceImpl implements ParkLotService {

    @Resource
    private AbpParkadePassRecordMapper abpParkadePassRecordMapper;

    @Override
    public Result<?> parkLotQr(ParkLotQrQuery query) {
        return Result.success();
    }
}
