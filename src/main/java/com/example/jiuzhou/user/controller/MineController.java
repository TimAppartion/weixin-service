package com.example.jiuzhou.user.controller;

import com.example.jiuzhou.common.Enum.ResultEnum;
import com.example.jiuzhou.common.utils.Result;
import com.example.jiuzhou.user.mapper.ExtOtherPlateNumberMapper;
import com.example.jiuzhou.user.query.BindCarQuery;
import com.example.jiuzhou.user.service.MineService;
import com.jfinal.i18n.Res;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;


/**
 * 我的页面
 * @author Appartion
 * @data 2022/10/12
 * 一入代码深似海，从此生活是路人
 */
@Slf4j
@RestController
@RequestMapping("/mine")
public class MineController {
    @Resource
    private ExtOtherPlateNumberMapper extOtherPlateNumberMapper;

    @Resource
    private MineService mineService;

    /**
     * 我的车辆列表
     * @param uid
     * @return
     */
    @RequestMapping("/CarList")
    public Result<?> CarList(@Validated @ModelAttribute String uid){
        if(StringUtils.isEmpty(uid)){
            return Result.error(ResultEnum.MISS_DATA);
        }
        return Result.success(extOtherPlateNumberMapper.carList(uid));
    }

    /**
     * 绑定车牌
     * @param query
     * @return
     */
    @PostMapping("/SaveBingCarNumber")
    public Result<?> saveBingCarNumber(@RequestBody BindCarQuery query){
        if(StringUtils.isEmpty(query.getPlateNumber())||StringUtils.isEmpty(query.getUid())){
            return Result.error(ResultEnum.MISS_DATA);
        }
        return mineService.saveBingCarNumber(query);
    }

    /**
     * 车牌解绑
     * @param query
     * @return
     */
    @PostMapping("/RelievePlatNumber")
    public Result<?> relievePlatNumber(@RequestBody BindCarQuery query){
        if(StringUtils.isEmpty(query.getPlateNumber())||StringUtils.isEmpty(query.getUid())){
            return Result.error(ResultEnum.MISS_DATA);
        }
        return mineService.relievePlatNumber(query);
    }
    @RequestMapping("/UserInfo")
    public Result<?> userInfo (@Validated @ModelAttribute String uid){
        if(StringUtils.isEmpty(uid)){
            return Result.error(ResultEnum.MISS_DATA);
        }
        return Result.success();
    }
}
