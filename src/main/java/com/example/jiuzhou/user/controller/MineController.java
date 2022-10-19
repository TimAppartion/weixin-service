package com.example.jiuzhou.user.controller;

import com.example.jiuzhou.common.Enum.ResultEnum;
import com.example.jiuzhou.common.utils.Result;
import com.example.jiuzhou.user.mapper.ExtOtherPlateNumberMapper;
import com.example.jiuzhou.user.query.BindCarQuery;
import com.example.jiuzhou.user.query.OrderQuery;
import com.example.jiuzhou.user.query.UpdateUserQuery;
import com.example.jiuzhou.user.service.MineService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
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
    public Result<?> CarList(@RequestParam(value = "uid", required = false) String uid){
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

    /**
     * 用户个人信息
     * @param uid
     * @return
     */
    @RequestMapping("/UserInfo")
    public Result<?> userInfo (@RequestParam(value = "uid", required = false) String uid){
        if(StringUtils.isEmpty(uid)){
            return Result.error(ResultEnum.MISS_DATA);
        }
        return mineService.userInfo(uid);
    }

    /**
     * 查询个人月卡信息
     * @param uid
     * @return
     */
    @RequestMapping("/MonthCad")
    public Result<?> monthCad(@RequestParam(value = "uid", required = false) String uid){
        return mineService.monthCad(uid);
    }

    /**
     * 月卡详情
     * @param parkType
     * @return
     */
    @RequestMapping("/getMonthlyCardDetail")
    public Result<?> getMonthlyCardDetail(@RequestParam(value = "parkType", required = false)String parkType){
        if(StringUtils.isEmpty(parkType)){
            return Result.error(ResultEnum.MISS_DATA);
        }
        return mineService.getMonthlyCardDetail(parkType);
    }

    /**
     * 账单记录
     * @param query
     * @return
     */
    @PostMapping("/OrderList")
    public Result<?> orderList (@RequestBody OrderQuery query){
        if(query.getPageNumber()==null){
            query.setPageNumber(1);
        }
        if(query.getPageSize()==null){
            query.setPageSize(10);
        }
        if(StringUtils.isEmpty(query.getUid())){
            return Result.error(ResultEnum.MISS_DATA);
        }
        return mineService.orderList(query);
    }

    /**
     * 修改用户个人信息
     * @param query
     * @return
     */
    @PostMapping("/updateUser")
    public Result<?> updateUser(@RequestBody UpdateUserQuery query){
        if(StringUtils.isEmpty(query.getUid())){
            return Result.error(ResultEnum.MISS_DATA);
        }
        return mineService.updateUser(query);
    }

}
