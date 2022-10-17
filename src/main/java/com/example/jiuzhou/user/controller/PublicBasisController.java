package com.example.jiuzhou.user.controller;

import com.example.jiuzhou.common.Enum.ResultEnum;
import com.example.jiuzhou.common.utils.Result;
import com.example.jiuzhou.user.mapper.RechargeRuleMapper;
import com.example.jiuzhou.user.model.RechargeRule;
import com.example.jiuzhou.user.query.WeiXinPayQuery;
import com.example.jiuzhou.user.service.PublicBasisService;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.weixin.sdk.kit.PaymentKit;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Appartion
 * @data 2022/10/17
 * 一入代码深似海，从此生活是路人
 */
@Slf4j
@RestController
@RequestMapping("/publicBasis")
public class PublicBasisController {

    private static final Prop prop = PropKit.use("weixin.properties");
    private static Integer TENANTID=Integer.valueOf(prop.get("tenantId"));

    @Resource
    private RechargeRuleMapper rechargeRuleMapper;

    @Resource
    private PublicBasisService publicBasisService;
    /**
     * 账号充值规则
     * @return
     */
    @GetMapping("/Recharge")
    public Result<?> Recharge (){

        Example example = new Example(RechargeRule.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("TenantId", TENANTID);
        criteria.andEqualTo("IsActive", 1);
        return Result.success(rechargeRuleMapper.selectByExample(example));
    }

    /**
     * 微信支付统一下单接口
     * @param query
     * @return
     */
    @PostMapping("/WeiXinPay")
    public Result<?> weiXinPay(@RequestBody WeiXinPayQuery query){
        if(query.getFee()==null){
            return Result.error(ResultEnum.MISS_DATA,"金额不可为空");
        }
        if(StringUtils.isEmpty(query.getUid())){
            return Result.error(ResultEnum.MISS_DATA,"用户id不可为空");
        }
        if(query.getType()==null){
            return Result.error(ResultEnum.MISS_DATA,"缴费类型不可为空");
        }
        return publicBasisService.WeiXinPay(query);
    }

    @RequestMapping("/WeiXinCallBack")
    public String weiXinCallBack(HttpServletRequest request) throws IOException {
        log.info("微信回调接口返回参数:{}",request);
        ServletInputStream is = request.getInputStream();
        byte[] bs = new byte[1024];
        int len = -1;
        StringBuilder builder = new StringBuilder();
        while ((len = is.read(bs)) != -1){
            builder.append(new String(bs,0,len));
        }
        String s = builder.toString();

        Map<String,String> map = PaymentKit.xmlToMap(s);
        log.info("微信回调转换为map：{}",map);
        if(map!=null && "success".equalsIgnoreCase(map.get("result_code"))){
            //支付成功处理业务
            Result result=publicBasisService.weiXinCallBack(map);

            Map<String,String> responseMap=new HashMap<>();
            responseMap.put("return_code","success");
            responseMap.put("return_msg","OK");
            responseMap.put("appid",map.get("appid"));
            responseMap.put("result_code","success");
            return PaymentKit.toXml(responseMap);
        }
        return null;
    }
}
