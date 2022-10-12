package com.example.jiuzhou.user.controller;

import com.example.jiuzhou.common.Enum.ResultEnum;
import com.example.jiuzhou.common.utils.Result;
import com.example.jiuzhou.user.mapper.ExtOtherPlateNumberMapper;
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
    @RequestMapping("/CarList")
    public Result<?> CarList(@Validated @ModelAttribute String uid){
        if(StringUtils.isEmpty(uid)){
            return Result.error(ResultEnum.MISS_DATA);
        }
        return Result.success(extOtherPlateNumberMapper.carList(uid));
    }
}
