package com.example.jiuzhou.user.controller;

import com.ctc.wstx.util.StringUtil;
import com.example.jiuzhou.common.Enum.ResultEnum;
import com.example.jiuzhou.common.utils.Result;
import com.example.jiuzhou.common.utils.SubMailUtils;
import com.example.jiuzhou.user.query.OauthQuery;
import com.example.jiuzhou.user.service.WeiXinOauthService;
import com.jfinal.i18n.Res;
import com.jfinal.plugin.ehcache.CacheKit;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Slf4j
@RestController
@ResponseBody
@RequestMapping("/weixin_guide/oauth")
public class OauthController {
    @Resource
    private WeiXinOauthService weiXinOauthService;

    @PostMapping("/WeiXinIndex")
    public Result<?> WeiXinIndex(@RequestBody OauthQuery query){
        log.info("微信授权信息:{}",query);
        if(StringUtils.isEmpty(query.getCode())){
            return Result.error(ResultEnum.MISS_DATA);
        }
        if(StringUtils.isEmpty(query.getMobile())){
            return Result.error(ResultEnum.MISS_DATA);
        }
        return weiXinOauthService.index(query);
    }

    @GetMapping("/sendRegSMS")
    public Result<?> sendRegSMS(@Validated @ModelAttribute OauthQuery query){
        String code = SubMailUtils.random(6, SubMailUtils.RandomType.INT);
        // String code = "123456";
        int res_code = -9;
        if (query.getSmsType()== 0) {
            // 注册
            res_code = SubMailUtils.sendMsg(1,query.getMobile(),code,null,null);
            // res_code = 0;
        } else if (query.getSmsType() == 1) {
            // 找回密码
            res_code = SubMailUtils.sendMsg(2,query.getMobile(),code,null,null);
        }
        if (res_code != 0) {
            return Result.error(ResultEnum.ERROR,"短信发送失败");
        }
        // 验证码放入缓存
        CacheKit.put("tenMinute", query.getMobile(), code);
        return Result.success("短信发送成功");
    }

    @PostMapping("/bingdphone")
    public Result<?> bingdphone(@RequestBody OauthQuery query){
        if(StringUtils.isEmpty(query.getMobile())){
            return Result.error(ResultEnum.MISS_DATA);
        }
        if(StringUtils.isEmpty(query.getCode())){
            return Result.error(ResultEnum.ERROR,"验证码不可为空");
        }
        if(!SubMailUtils.mobileCodeEquals(query.getMobile(),query.getCode())){
            return Result.error(ResultEnum.ERROR,"验证码输入错误,请重新输入！");
        }
        return weiXinOauthService.bindPhone(query);
    }


}
