package com.example.jiuzhou.user.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.jiuzhou.common.Enum.ResultEnum;
import com.example.jiuzhou.common.utils.Result;
import com.example.jiuzhou.common.utils.SubMailUtils;
import com.example.jiuzhou.user.mapper.AbpWeixinConfigMapper;
import com.example.jiuzhou.user.mapper.AbpWeixinMsgMapper;
import com.example.jiuzhou.user.mapper.AbpWeixinSendMsgModelsMapper;
import com.example.jiuzhou.user.mapper.TUserMapper;
import com.example.jiuzhou.user.model.AbpWeixinConfig;

import com.example.jiuzhou.user.model.AbpWeixinMsg;
import com.example.jiuzhou.user.model.AbpWeixinSendMsgModels;
import com.example.jiuzhou.user.query.OauthQuery;
import com.example.jiuzhou.user.service.WeiXinMessageService;
import com.example.jiuzhou.user.service.WeiXinOauthService;
import com.example.jiuzhou.user.service.ZhiFuBaoService;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.weixin.sdk.kit.PaymentKit;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
    @Resource
    private WeiXinMessageService weiXinMessageService;

    @Resource
    private AbpWeixinSendMsgModelsMapper abpWeixinSendMsgModelsMapper;

    @Resource
    private AbpWeixinMsgMapper abpWeixinMsgMapper;

    private static final Prop prop = PropKit.use("weixin.properties");
    private static Integer TENANTID=Integer.valueOf(prop.get("tenantId"));
    private static String TOKEN=prop.get("token");
    private static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5','6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};


    @PostConstruct
    public void startInit(){
        log.info("————————————系统初始化添加信息进缓存开始——————————————");
        Example example = new Example(AbpWeixinConfig.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("TenantId", 1);
        AbpWeixinConfig config = abpWeixinConfigMapper.selectByExample(example).get(0);
        redisTemplate.opsForValue().set("config",JSONObject.toJSONString(config));
        redisTemplate.opsForValue().set("companyId",abpWeixinConfigMapper.getIdByTenantId(config.getTenantId()));
        redisTemplate.delete("WeiXinToken");
        weiXinMessageService.getWeiXinToken();
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
        int res_code = -9;
        if (query.getSmsType()== 0) {
            // 注册
            res_code = SubMailUtils.sendMsg(1,query.getMobile(),code,null,null);
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
        if(StringUtils.isEmpty(query.getCode())){
            return Result.error(ResultEnum.ERROR,"验证码错误");
        }
//        if(!mobileCodeEquals(query.getMobile(),query.getCode())){
//            return Result.error(ResultEnum.ERROR,"验证码输入错误,请重新输入！");
//        }
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


    /**
     * 微信公众号服务消息地址
     * @param response
     * @param request
     * @param signature
     * @param timestamp
     * @param nonce
     * @param echostr
     * @return
     * @throws IOException
     */
//    @RequestMapping("/msg")
//    public String callBack(HttpServletResponse response, HttpServletRequest request, String signature, String timestamp, String nonce, String echostr) throws IOException {
//        try {
//            String[] arr = new String[]{TOKEN, timestamp, nonce};
//            Arrays.sort(arr);
//            StringBuilder sb = new StringBuilder();
//            for (int i = 0; i < arr.length; i++) {
//                sb.append(arr[i]);
//            }
//            MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
//            messageDigest.update(sb.toString().getBytes());
//            String sign = getFormattedText(messageDigest.digest());
//            if (sign.equals(signature)) {
//                return echostr;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }



    @RequestMapping("/msg")
    public void msg(HttpServletResponse response, HttpServletRequest request, String signature, String timestamp, String nonce, String echostr) throws IOException {

            log.info("接收参数：request:{},requestMap:{}",request.toString(),request.getInputStream().toString());
            request.setCharacterEncoding("utf8");
            response.setCharacterEncoding("utf8");
            // 处理消息和事件推送
            Map<String, String> requestMap = parseRequest(request.getInputStream());
            log.info("转换xml为map：{}",requestMap);
            Example example = new Example(AbpWeixinSendMsgModels.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("TenantId", TENANTID);
            criteria.andEqualTo("IsActive",1);
            List<AbpWeixinSendMsgModels> list=abpWeixinSendMsgModelsMapper.selectByExample(example);
            Map<String ,String > msgMap=list.stream().collect(Collectors.toMap(AbpWeixinSendMsgModels::getCrux,AbpWeixinSendMsgModels::getMsg));
            String toUser=requestMap.get("ToUserName");
            String FromUserName =requestMap.get("FromUserName");
            requestMap.put("ToUserName",FromUserName);
            requestMap.put("FromUserName",toUser);
            if(msgMap.get(requestMap.get("Content"))!=null){
                requestMap.put("Content",msgMap.get(requestMap.get("Content")));
            }else{
                AbpWeixinMsg msg= new AbpWeixinMsg();
                msg.setMsg(requestMap.get("Content"));
                msg.setCreationTime(new Date());
                msg.setTenantId(TENANTID);
                abpWeixinMsgMapper.insertOne(msg);
                requestMap.put("Content",msgMap.get("-1"));
            }

            // 准备回复的数据包
            String respXml =PaymentKit.toXml(requestMap);
            log.info("准备返回的消息:{}",respXml);
            PrintWriter out = response.getWriter();
            out.print(respXml);
            out.flush();
            out.close();
    }
    /**
     * 解析xml数据包
     * @param is
     * @return
     */
    public static Map<String, String> parseRequest(InputStream is) {
        Map<String,String> map=new HashMap<>();
        SAXReader reader = new SAXReader();
        try {
            //读取输入流，获取文档对象
            Document document=reader.read(is);
            //提交文档对象获取根节点
            Element root=document.getRootElement();
            //获取根节点的所有的子节点
            List<Element>element=root.elements();
            for(Element e:element){
                map.put(e.getName(), e.getStringValue());
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return map;
    }


    private static String getFormattedText(byte[] bytes) {
        int len = bytes.length;
        StringBuilder buf = new StringBuilder(len * 2);
        // 把密文转换成十六进制的字符串形式
        for (int j = 0; j < len; j++) {
            buf.append(HEX_DIGITS[(bytes[j] >> 4) & 0x0f]);
            buf.append(HEX_DIGITS[bytes[j] & 0x0f]);
        }

        return buf.toString();

    }
}
