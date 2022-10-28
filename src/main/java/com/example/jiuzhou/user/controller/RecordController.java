package com.example.jiuzhou.user.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.jiuzhou.common.Enum.ResultEnum;
import com.example.jiuzhou.common.utils.Result;
import com.example.jiuzhou.user.model.AbpWeixinConfig;
import com.example.jiuzhou.user.query.RecordQuery;
import com.example.jiuzhou.user.service.RecordService;
import com.github.wxpay.sdk.WXPayConstants;
import com.github.wxpay.sdk.WXPayUtil;
import com.jfinal.weixin.sdk.kit.PaymentKit;
import com.jfinal.weixin.sdk.utils.Base64Utils;
import com.jfinal.weixin.sdk.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * @author Appartion
 * @data 2022/10/26
 * 一入代码深似海，从此生活是路人
 */
@Slf4j
@RestController
@RequestMapping("/record")
public class RecordController {
    @Resource
    private RecordService recordService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 停车记录
     * @param query
     * @return
     */
    @PostMapping("/ParkingRecord")
    public Result<?> parkingRecord(@RequestBody RecordQuery query){
        if(StringUtils.isEmpty(query.getUid())){
            return Result.error(ResultEnum.MISS_DATA);
        }
        if(query.getPageNumber()==null){
            query.setPageNumber(1);
        }
        if(query.getPageSize()==null){
            query.setPageSize(10);
        }
        return recordService.parkingRecord(query);
    }

    @GetMapping("/orderQuery")
    public Result<?> orderQuery(@RequestParam(value = "out_trade_no", required = false) String out_trade_no){
        AbpWeixinConfig config= JSONObject.parseObject(redisTemplate.opsForValue().get("config").toString(),AbpWeixinConfig.class);

        Map<String,String> params=new HashMap<>();
        params.put("appid",config.getAppId());
        params.put("mch_id",config.getMch_id());
        params.put("out_trade_no",out_trade_no);
        params.put("nonce_str",System.currentTimeMillis() / 1000 + "");
        String sign = PaymentKit.createSign(params, config.getMch_id());
        params.put("sign", sign);

        String xmlResult = HttpUtils.post("https://api.mch.weixin.qq.com/pay/orderquery",PaymentKit.toXml(params));
        return Result.success(xmlResult);
    }

    @GetMapping("/WeiXinQrCode")
    public Result<?> WeiXinQrCode(@RequestParam(value = "money", required = false) BigDecimal money) throws Exception {
        AbpWeixinConfig config= JSONObject.parseObject(redisTemplate.opsForValue().get("config").toString(),AbpWeixinConfig.class);

        String orderId=UUID.randomUUID().toString().replace("-","");

        SortedMap<String, String> map = new TreeMap<>();
        map.put("appid",config.getAppId());
        map.put("mch_id",config.getMch_id());
        map.put("nonce_str", UUID.randomUUID().toString().replaceAll("-",""));
        map.put("body","停车管理收费");
        map.put("out_trade_no",orderId);
        map.put("total_fee",money.toString());
        map.put("spbill_create_ip","120.26.37.170");
        map.put("notify_url","http://120.26.37.170:8000/api/InterfacePDA/WinxinPayBackMessage");
        map.put("trade_type","NATIVE");
        String sign = WXPayUtil.generateSignature(map,"fxintelfxintelfxintelfxintel0913", WXPayConstants.SignType.MD5);
        map.put("sign",sign);
        String xmlResult = HttpUtils.post("https://api.mch.weixin.qq.com/pay/unifiedorder",PaymentKit.toXml(map));
        Map<String , String > resultMap=new HashMap<>();
        resultMap = PaymentKit.xmlToMap(xmlResult);
        return Result.success(resultMap);

    }
    public static String createSign(String characterEncoding, SortedMap<String, String> packageParams, String API_KEY) {
        StringBuffer sb = new StringBuffer();
        Set es = packageParams.entrySet();
        Iterator it = es.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String k = (String) entry.getKey();
            String v = (String) entry.getValue();
            if (null != v && !"".equals(v) && !"sign".equals(k) && !"key".equals(k)) {
                sb.append(k + "=" + v + "&");
            }
        }
        sb.append("key=" + API_KEY);
        String sign = md5AndBase64Encode(sb.toString(), characterEncoding).toUpperCase();
        return sign;
    }

    /**

     * md5和base64混合加密

     * @author mao

     */

    private static String md5AndBase64Encode(String userName, String pwd) {
        if(userName == null || pwd == null){
            return null;
        }
        String str = "";
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");//md5
            md5.update((userName + "+" + pwd).getBytes());
            str = Base64Utils.encode(md5.digest());//base64
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
        return str;

    }
}
