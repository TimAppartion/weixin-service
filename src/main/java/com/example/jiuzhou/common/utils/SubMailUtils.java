package com.example.jiuzhou.common.utils;

import java.io.IOException;
import java.util.Map;
import com.alibaba.fastjson.JSONObject;
import com.example.jiuzhou.user.mapper.AbpWeixinConfigMapper;
import com.example.jiuzhou.user.model.AbpSettings;
import com.example.jiuzhou.user.model.AbpWeixinConfig;
import com.example.jiuzhou.user.model.RequestEncoder;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.ehcache.CacheKit;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Random;

public class SubMailUtils {


    public static final String TIMESTAMP = "https://api.mysubmail.com/service/timestamp";
    private static final String URL = "https://api-v4.mysubmail.com/sms/send";
    private static final String APPID="65798";
    private static final String AppKEY="6d239f8e8ca2c85a351842c077417704";
    public static final String TYPE_MD5 = "md5";
    public static final String TYPE_SHA1 = "sha1";

    private static final Prop prop = PropKit.use("sms.properties");
    //您本次注册的验证码是:@，感谢您的使用！如非本人操作，请忽略(10分钟内之内有效)。
    private static final String SMS_CONTENT_REGISTER_CODE = prop.get("content_register_code");
    //您本次找回密码的验证码是:@，感谢您的使用！如非本人操作，请忽略(10分钟内之内有效)。
    private static final String SMS_CONTENT_FORGET_CODE = prop.get("content_forget_code");
    //会员注册通知：昵称:@,联系方式:@
    private static final String CONTENT_REGISTER_NOTIFY = prop.get("content_register_notify");

    @Resource
    private AbpWeixinConfigMapper abpWeixinConfigMapper;

    public static Integer sendMsg(Integer type,String to,String code,String name,String tel){
        String content="";
        switch (type){
            case 1:content= StringUtils.replace(SMS_CONTENT_REGISTER_CODE, "@", code);break;
            case 2:content=StringUtils.replace(SMS_CONTENT_FORGET_CODE, "@", code);break;
            case 3:content=StringUtils.replace(CONTENT_REGISTER_NOTIFY, "@", name);break;
        }
        Integer resCode=-1;
        try {
            Map<String, String> headers = new HashMap<String, String>();
            headers.put("Content-Type", "application/json");
            JSONObject params = new JSONObject();
            AbpSettings abpSettings=new AbpSettings();
            abpSettings.setTenantId(1);
            AbpWeixinConfig abpWeixinConfig=new AbpWeixinConfig();
            abpWeixinConfig.setTenantId(1);
            params.put("content","【九州停车】"+content);
            params.put("signature",AppKEY);
            params.put("to",to);
            params.put("appid",APPID);
            String result = HttpKit.post(URL,null,params.toJSONString(),headers);
            if(JSONObject.parseObject(result).get("status").equals("success")){
                resCode=0;
            }else{
                resCode=-1;
            }
        }catch (Exception e){
            e.printStackTrace();
            resCode=-1;
        }
        return resCode;

    }


    /**
     * 验证手机验证码是否正确
     *
     * @param tel
     * @param code
     */
    public static boolean mobileCodeEquals(String tel, String code) {
        // 判断手机验证码是否正确
        String cache_code = CacheKit.get("tenMinute", tel);
        System.out.println("cache_code:" + cache_code + "tel_code:" + code);
        if (null == cache_code || !code.equals(cache_code)) {
            return false;
        } else {
            // 如果相同则移除该手机验证码缓存
            CacheKit.remove("tenMinute", tel);
            return true;
        }
    }
//    public static void main(String[] args) {
//        sendMsg(1,"18141131931",null,null,null);
//    }

    public static void main2(String[] args) {
        Map<String, String> requestData = new HashMap<>();
        String appid = "";
        String appkey = "";
        String to = "18141131931";
        String content = "【昌宁停车】你好，你的验证码是2257";
        String sign_type = "md5";
        String sign_version = "2";

        //组合请求数据
        requestData.put("appid", appid);
        requestData.put("to", to);
        if (sign_type.equals(TYPE_MD5) || sign_type.equals(TYPE_SHA1)) {
            String timestamp = getTimestamp();
            requestData.put("timestamp", timestamp);
            requestData.put("sign_type", sign_type);
            requestData.put("sign_version", sign_version);
            String signStr = appid + appkey + RequestEncoder.formatRequest(requestData) + appid + appkey;
            System.out.println(signStr);
            requestData.put("signature", RequestEncoder.encode(sign_type, signStr));
        } else {
            requestData.put("signature", appkey);
        }
        requestData.put("content", content);
        //post请求
        HttpPost httpPost = new HttpPost(URL);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-Type", "application/json");
        StringEntity entity = new StringEntity(JSONObject.toJSONString(requestData), "UTF-8");
        httpPost.setEntity(entity);
        try {
            CloseableHttpClient closeableHttpClient = HttpClientBuilder.create().build();
            HttpResponse response = closeableHttpClient.execute(httpPost);
            HttpEntity httpEntity = response.getEntity();
            if (httpEntity != null) {
                String jsonStr = EntityUtils.toString(httpEntity, "UTF-8");
                System.out.println(jsonStr);
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //获取时间戳
    private static String getTimestamp() {
        CloseableHttpClient closeableHttpClient = HttpClientBuilder.create().build();
        HttpGet httpget = new HttpGet(TIMESTAMP);
        try {
            HttpResponse response = closeableHttpClient.execute(httpget);
            HttpEntity httpEntity = response.getEntity();
            String jsonStr = EntityUtils.toString(httpEntity, "UTF-8");
            if (jsonStr != null) {
                JSONObject json = JSONObject.parseObject(jsonStr);
                return json.getString("timestamp");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



    // 随机字符串
    private static final String _INT = "0123456789";
    private static final String _STR = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final String _ALL = _INT + _STR;

    private static final Random RANDOM = new Random();
    /**
     * 生成的随机数类型
     */
    public static enum RandomType {
        INT, STRING, ALL;
    }

    /**
     * 随机数生成
     * @param count
     * @return
     */

    public static String random(int count, RandomType randomType) {
        if (count == 0) return "";
        if (count < 0) {
            throw new IllegalArgumentException("Requested random string length " + count + " is less than 0.");
        }
        char[] buffer = new char[count];
        for (int i = 0; i < count; i++) {
            if (randomType.equals(RandomType.INT)) {
                buffer[i] = _INT.charAt(RANDOM.nextInt(_INT.length()));
            } else if (randomType.equals(RandomType.STRING)) {
                buffer[i] = _STR.charAt(RANDOM.nextInt(_STR.length()));
            }else {
                buffer[i] = _ALL.charAt(RANDOM.nextInt(_ALL.length()));
            }
        }
        return new String(buffer);
    }

}