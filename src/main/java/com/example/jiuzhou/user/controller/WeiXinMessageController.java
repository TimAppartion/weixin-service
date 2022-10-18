package com.example.jiuzhou.user.controller;

import com.example.jiuzhou.common.Enum.ResultEnum;
import com.example.jiuzhou.common.utils.Result;
import com.example.jiuzhou.user.query.WeiXinMessageQuery;
import com.example.jiuzhou.user.service.WeiXinMessageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 公众号内发通知消息
 * @author Appartion
 * @data 2022/10/14
 * 一入代码深似海，从此生活是路人
 */

@Slf4j
@RestController
@RequestMapping("/message")
public class WeiXinMessageController {


    @Resource
    private WeiXinMessageService weiXinMessageService;

    @PostMapping("/SendParkPay")
    public Result<?> sendParkPay(@RequestBody WeiXinMessageQuery query){
        if(StringUtils.isEmpty(query.getOpenId())&&
                StringUtils.isEmpty(query.getCarNumber())&&
                StringUtils.isEmpty(query.getParkName())&&
                StringUtils.isEmpty(query.getCarInTime())&&
                StringUtils.isEmpty(query.getCarOutTime())){
            return Result.error(ResultEnum.MISS_DATA);
        }
        return weiXinMessageService.SendParkPay(query);
    }

    @PostMapping("/SendInPark")
    public Result<?> sendInPark(@RequestBody WeiXinMessageQuery query){
        if(StringUtils.isEmpty(query.getCarNumber())&&
                StringUtils.isEmpty(query.getParkName())&&
                StringUtils.isEmpty(query.getCarInTime())){
            return Result.error(ResultEnum.MISS_DATA);
        }
        return weiXinMessageService.SendInPark(query);
    }

    @PostMapping("/SendOutPark")
    public Result<?> sendOutPark(@RequestBody WeiXinMessageQuery query){
        if(StringUtils.isEmpty(query.getCarNumber())&&
                StringUtils.isEmpty(query.getParkName())&&
                StringUtils.isEmpty(query.getCarInTime())&&
                StringUtils.isEmpty(query.getCarOutTime())){
            return Result.error(ResultEnum.MISS_DATA);
        }
        return weiXinMessageService.SendOutPark(query);
    }
}
