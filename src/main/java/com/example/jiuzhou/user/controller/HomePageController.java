package com.example.jiuzhou.user.controller;

import com.example.jiuzhou.common.Enum.ResultEnum;
import com.example.jiuzhou.common.utils.Result;
import com.example.jiuzhou.user.mapper.AbpBerthsMapper;
import com.example.jiuzhou.user.model.AbpBerthsecs;
import com.example.jiuzhou.user.query.ArrearageQuery;
import com.example.jiuzhou.user.query.BerthsQuery;
import com.example.jiuzhou.user.service.AbpBerthsecsService;
import com.example.jiuzhou.user.service.HomePageService;
import com.github.pagehelper.PageInfo;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
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

    private static final Prop prop = PropKit.use("weixin.properties");
    private static Integer TENANTID=Integer.valueOf(prop.get("tenantId"));

    @Autowired
    private RedisTemplate redisTemplate;

    @Resource
    private AbpBerthsecsService abpBerthsecsService;

    @Resource
    private AbpBerthsMapper abpBerthsMapper;

    @Resource
    private HomePageService homePageService;

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
     * 附件站点地图使用
     * @param query
     * @return
     */
    @PostMapping("/NearSiteMap")
    public Result<?> nearSiteMap(@RequestBody BerthsQuery query){
        if(StringUtils.isEmpty(query.getLat())||StringUtils.isEmpty(query.getLng())){
            return Result.error(ResultEnum.MISS_DATA);
        }
        return abpBerthsecsService.nearSiteMap(query);
    }

    /**
     * 获取微信skd
     * @param url
     * @return
     */
    @GetMapping("/getWxSDK")
    public Result<?> getWxSDK(@RequestParam(value = "url", required = false) String url){
        return homePageService.getWexSDK(url);
    }

    /**
     * 获取泊位段
     * @param id
     * @return
     */
    @RequestMapping("/BerthList")
    public Result<?> berthList(@RequestParam(value = "id", required = false) String id){
        if(StringUtils.isEmpty(id)){
            return Result.error(ResultEnum.ERROR);
        }
        return Result.success(abpBerthsMapper.getByBerthsecId(id,TENANTID));
    }

    /**
     * 再停订单
     * @param uid
     * @return
     */
    @RequestMapping("/OnlineCar")
    public Result<?> onlineCar(@RequestParam(value = "uid", required = false) String uid){

        if(StringUtils.isEmpty(uid)){
            return Result.error(ResultEnum.MISS_DATA);
        }
        return homePageService.onlineCar(uid);
    }

    /**
     * 欠费订单
     * @param query
     * @return
     */
    @PostMapping("/ArrearageList")
    public Result<?> arrearage(@RequestBody ArrearageQuery query){
        if(StringUtils.isEmpty(query.getUid())){
            return Result.error(ResultEnum.MISS_DATA);
        }
        return homePageService.arrearage(query);
    }

    /**
     * 代缴订单查询
     * @param plateNumber
     * @return
     */
    @RequestMapping("/getDJOrder")
    public Result<?> getDJOrder(@RequestParam(value = "plateNumber", required = false) String plateNumber){
        if(StringUtils.isEmpty(plateNumber)){
            return Result.error(ResultEnum.MISS_DATA);
        }
        return homePageService.getDJOrder(plateNumber);
    }


}
