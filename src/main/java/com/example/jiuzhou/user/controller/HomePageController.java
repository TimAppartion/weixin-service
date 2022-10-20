package com.example.jiuzhou.user.controller;


import com.example.jiuzhou.common.Enum.ResultEnum;
import com.example.jiuzhou.common.utils.Result;
import com.example.jiuzhou.user.model.AbpBerthsecs;
import com.example.jiuzhou.user.query.BerthsQuery;
import com.example.jiuzhou.user.service.AbpBerthsecsService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
/**
 * 主页
 * @author Appartion
 * @data 2022/10/12
 * 一入代码深似海，从此生活是路人
 */
@Slf4j
@RestController
@RequestMapping("/homePage")
public class HomePageController {

    @Resource
    private AbpBerthsecsService abpBerthsecsService;

    @PostMapping("/NearSite")
    public Result<PageInfo<AbpBerthsecs>> nearSite(@RequestBody BerthsQuery query){

        if(query.getPageIndex()==null){
            query.setPageIndex(1);
        }
        if(query.getPageSize()==null){
            query.setPageSize(10);
        }
        if(StringUtils.isEmpty(query.getLat())||StringUtils.isEmpty(query.getLng())){
            return Result.error(ResultEnum.MISS_DATA);
        }
        return Result.success(abpBerthsecsService.nearSite(query));
    }

    /**
     * 获取微信skd
     * @param guid
     * @return
     */
    @PostMapping("/getWxSDK")
    public Result<?> getWxSDK(@RequestParam(value = "guid", required = false) String guid){
        return Result.success();
    }


}
