package com.example.jiuzhou.user.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.jiuzhou.common.Enum.ResultEnum;
import com.example.jiuzhou.common.utils.Result;
import com.example.jiuzhou.common.utils.SubMailUtils;
import com.example.jiuzhou.user.mapper.AbpWeixinConfigMapper;
import com.example.jiuzhou.user.mapper.TUserMapper;
import com.example.jiuzhou.user.model.AbpWeixinConfig;

import com.example.jiuzhou.user.query.OauthQuery;
import com.example.jiuzhou.user.service.WeiXinOauthService;
import com.example.jiuzhou.user.service.ZhiFuBaoService;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/oauth")
public class OauthController {
    @Resource
    private WeiXinOauthService weiXinOauthService;

    @Resource
    private ZhiFuBaoService zhiFuBaoService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Resource
    private TUserMapper tUserMapper;
    @Resource
    private AbpWeixinConfigMapper abpWeixinConfigMapper;

    private static final Prop prop = PropKit.use("weixin.properties");
    private static Integer TENANTID=Integer.valueOf(prop.get("tenantId"));

    @PostConstruct
    public void startInit(){
        log.info("————————————系统初始化添加信息进缓存开始——————————————");
        Example example = new Example(AbpWeixinConfig.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("TenantId", 1);
        AbpWeixinConfig config = abpWeixinConfigMapper.selectByExample(example).get(0);
        redisTemplate.opsForValue().set("config",JSONObject.toJSONString(config));
        log.info("————————————系统初始化添加信息进缓存结束——————————————");
//        AbpWeixinConfig config1=JSONObject.parseObject(redisTemplate.opsForValue().get("config").toString(),AbpWeixinConfig.class);
    }
    @PostMapping("/WeiXinIndex")
    public Result<?> WeiXinIndex(@RequestBody OauthQuery query){
        log.info("微信授权信息:{}",query);
        if(StringUtils.isEmpty(query.getCode())){
            return Result.error(ResultEnum.MISS_DATA);
        }
        return weiXinOauthService.index(query);
    }

    @PostMapping("/zfbIndex")
    public Result<?> zfbIndex(@RequestBody OauthQuery query){
        log.info("支付宝授权信息:{}",query);
        if(StringUtils.isEmpty(query.getCode())){
            return Result.error(ResultEnum.MISS_DATA);
        }
        return zhiFuBaoService.index(query);
    }

    @GetMapping("/sendRegSMS")
    public Result<?> sendRegSMS(@Validated @ModelAttribute OauthQuery query){
        log.info("短信发送：{}",query);
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
        // redis因为用的@Autowired注入的导致需要对录入的参数做序列化处理
        redisTemplate.opsForValue().set(query.getMobile().toString(), code,5,TimeUnit.MINUTES);
        return Result.success("短信发送成功");
    }

    @PostMapping("/bingdphone")
    public Result<?> bingdphone(@RequestBody OauthQuery query){
        if(StringUtils.isEmpty(query.getMobile())){
            return Result.error(ResultEnum.MISS_DATA);
        }
        if(StringUtils.isEmpty(query.getUid())){
            return Result.error(ResultEnum.MISS_DATA);
        }
        if(StringUtils.isEmpty(query.getCode())){
            return Result.error(ResultEnum.ERROR,"验证码错误");
        }
        if(!mobileCodeEquals(query.getMobile(),query.getCode())){
            return Result.error(ResultEnum.ERROR,"验证码输入错误,请重新输入！");
        }
        return weiXinOauthService.bindPhone(query);
    }
    /**
     * 验证手机验证码是否正确
     *
     * @param tel
     * @param code
     */
    public boolean mobileCodeEquals(String tel, String code) {
        // 判断手机验证码是否正确
        Object cache_code =redisTemplate.opsForValue().get(tel);
        if(cache_code==null){
            return false;
        }
        log.info("cache_code:" + cache_code + "tel_code:" + code);
        if (!code.equals(cache_code.toString())) {
            return false;
        } else {
            // 如果相同则移除该手机验证码缓存
            redisTemplate.delete(tel);
            return true;
        }
    }

}
