package com.example.jiuzhou.user.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConstants;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.example.jiuzhou.user.mapper.MonthRecordMapper;
import com.example.jiuzhou.user.model.AbpWeixinConfig;
import com.example.jiuzhou.user.model.MonthRecord;
import com.example.jiuzhou.user.service.PublicBasisService;
import org.apache.commons.lang.StringUtils;
import com.alipay.api.request.*;
import com.alipay.api.response.*;
import com.example.jiuzhou.common.Enum.ResultEnum;
import com.example.jiuzhou.common.utils.Result;
import com.example.jiuzhou.user.mapper.TUserMapper;
import com.example.jiuzhou.user.mapper.ZfbOrdersMapper;
import com.example.jiuzhou.user.model.TUser;
import com.example.jiuzhou.user.model.ZfbOrders;
import com.example.jiuzhou.user.query.OauthQuery;
import com.example.jiuzhou.user.query.ZhiFuBaoPayQuery;
import com.example.jiuzhou.user.service.CouponsService;
import com.example.jiuzhou.user.service.ZhiFuBaoService;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class ZhiFuBaoServiceImpl implements ZhiFuBaoService {

    private static final Prop prop = PropKit.use("zfb.properties");
    private static String APP_ID=prop.get("app_id");
    private static String APP_PRIVATE_KEY=prop.get("app_private_key");
    private static String APP_PUBLIC_KEY=prop.get("app_public_key");
    private static String CHARSEt=prop.get("charset");
    private static String APP_URL=prop.get("app_url");
    private static String NOTIFY_URL=prop.get("notify_url");
    private static String RETURN_URL=prop.get("return_url");

    private static String PID=prop.get("pid");


    @Resource
    private TUserMapper tUserMapper;

    @Resource
    private ZfbOrdersMapper zfbOrdersMapper;

    @Resource
    private CouponsService couponsService;

    @Resource
    private PublicBasisService publicBasisService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Resource
    private MonthRecordMapper monthRecordMapper;


    @Override
    public Result<?> index(OauthQuery query) {
        AlipayClient alipayClient = new DefaultAlipayClient(APP_URL,APP_ID,APP_PRIVATE_KEY,"json",CHARSEt,APP_PUBLIC_KEY,"RSA2");
        AlipaySystemOauthTokenRequest request = new AlipaySystemOauthTokenRequest();
        request.setCode(query.getCode());
        request.setGrantType("authorization_code");

        //返回前端的结果参数
        Map<String ,Object> responseMap=new HashMap<>();
        //授权获取token
        try {
            AlipaySystemOauthTokenResponse response = alipayClient.execute(request);
            log.info("zfbOauthToken"+response);
            if(response.isSuccess()){
                AlipayUserInfoShareResponse userInfoShareResponse = alipayClient.execute(new AlipayUserInfoShareRequest(),response.getAccessToken());
                log.info("zfbShare:"+userInfoShareResponse);
                if(userInfoShareResponse.isSuccess()){
                    JSONObject result = JSONObject.parseObject(userInfoShareResponse.getBody());
                    JSONObject userInfo=result.getJSONObject("alipay_user_info_share_response");
//                    if(!userInfo.isEmpty() && userInfo.getInteger("code").intValue() ==1000){
                    responseMap.put("user_id",userInfo.get("user_id"));
                    responseMap.put("nick_name",userInfo.get("nick_name"));
                    responseMap.put("header_img",userInfo.get("avatar"));
                    return Result.success(responseMap);
//                    }
                }
                return Result.success(userInfoShareResponse);
            }
            return Result.success(response);
        }catch (Exception e){
            e.printStackTrace();
            return Result.error(ResultEnum.ERROR,"获取支付宝授权信息失败");
        }
    }

    @Override
    public Result<?> pay(ZhiFuBaoPayQuery query) throws AlipayApiException {
        //获得初始化的AlipayClient
        AlipayClient alipayClient = new DefaultAlipayClient(APP_URL,APP_ID, APP_PRIVATE_KEY,
                "json", "UTF-8", APP_PUBLIC_KEY,"RSA2");

        AlipayTradePrecreateRequest alipayRequest = new AlipayTradePrecreateRequest();
        alipayRequest.setReturnUrl(query.getReturn_url());
        alipayRequest.setNotifyUrl(NOTIFY_URL);

        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", query.getOut_trade_no());
        bizContent.put("total_amount", query.getTotal_amount());
        bizContent.put("subject", query.getSubject());

        alipayRequest.setBizContent(bizContent.toString());
        AlipayTradePrecreateResponse response = alipayClient.execute(alipayRequest);

        if(response.isSuccess()){
            return Result.success(response);
        } else {
            return Result.error(ResultEnum.ERROR, response);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<?> aliPay(ZhiFuBaoPayQuery query)  {


        if(query.getBody().getType()==2){
            query.getBody().setMonthRecordId(UUID.randomUUID().toString().replace("-",""));
            if(query.getBody().getIsMonthlyRenewal()==null){
                return Result.error(ResultEnum.MISS_DATA,"是否包月不可为空");
            }
            Result result=publicBasisService.checkMonthlyCar(query.getBody().getIsMonthlyRenewal(),query.getBody().getPlateNumber(),query.getBody().getParkId());
            if(result.getCode()!=200){
                return result;
            }
            MonthRecord monthRecord = new MonthRecord();
            monthRecord.setId(query.getBody().getMonthRecordId());
            monthRecord.setPlateNumber(query.getBody().getPlateNumber());
            monthRecord.setMonthly_total_fee(query.getBody().getTotal_amount());
            monthRecord.setParkId(query.getBody().getParkId());
            monthRecord.setMonth(query.getBody().getMonth());
            monthRecord.setMonthlyType(query.getBody().getMonthlyType());
            monthRecord.setUid(query.getBody().getUid());
            monthRecordMapper.insertOne(monthRecord);
        }


        zfbOrdersMapper.insert(query.getBody());


        //获得初始化的AlipayClient
        AlipayClient alipayClient = new DefaultAlipayClient(APP_URL,APP_ID, APP_PRIVATE_KEY,
                "json", "UTF-8", APP_PUBLIC_KEY,"RSA2");
        //设置请求参数
        AlipayTradeWapPayRequest alipayRequest = new AlipayTradeWapPayRequest ();
        alipayRequest.setReturnUrl(query.getReturn_url());
        alipayRequest.setNotifyUrl(NOTIFY_URL);
        alipayRequest.setBizContent("{\"out_trade_no\":\"" + query.getOut_trade_no() + "\","
                + "\"total_amount\":\"" + query.getTotal_amount() + "\","
                + "\"subject\":\"" + query.getSubject() + "\","
                + "\"quit_url\":\"" + query.getQuit_url() + "\","
                + "\"product_code\":\"QUICK_WAP_WAY\"}");



        //请求
        log.info("支付宝订单支付请求参数：{}",JSONObject.toJSONString(alipayRequest));
        try {
            AlipayTradeWapPayResponse response = alipayClient.pageExecute(alipayRequest,"get");
            return Result.success(response);
        }catch (AlipayApiException e){
            e.printStackTrace();
            return Result.error(ResultEnum.ERROR,"支付订单下单失败");
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String  callback(Map<String, String> params) {
        String result = "failure";
        String outTradeNo = params.get("out_trade_no");
        ZfbOrders zfbOrders = zfbOrdersMapper.getByOutTradeNo(outTradeNo);

        AbpWeixinConfig config= JSONObject.parseObject(redisTemplate.opsForValue().get("config").toString(),AbpWeixinConfig.class);

        try {

            zfbOrders.setUserId(params.get("buyer_id"));
            log.info("支付宝业务数据：{},交易号的id:{}" ,zfbOrders,outTradeNo);
            //异步通知验签
            boolean signVerified = AlipaySignature.rsaCheckV1(params,
                    APP_PUBLIC_KEY,
                    AlipayConstants.CHARSET_UTF8,
                    AlipayConstants.SIGN_TYPE_RSA2);
            if (!signVerified) {
                zfbOrders.setIsOver(3);
                zfbOrders.setOverTime(new Date());
                zfbOrdersMapper.updateByPrimaryKey(zfbOrders);
                log.error("支付成功,异步通知验签失败!");
                return result;
            }
            if(zfbOrders.getIsOver()==1) {
                //校验通知中的 seller_id是否为 out_trade_no 这笔单据的对应的操作方
                String sellerId = params.get("seller_id");
                if (!sellerId.equals(PID)) {
                    log.error("商家PID校验失败");
                    return result;
                }
                //验证 app_id 是否为该商家本身
                String appId = params.get("app_id");
                if (!appId.equals(APP_ID)) {
                    log.error("app_id校验失败");
                    return result;
                }
                //在支付宝的业务通知中，只有交易通知状态为 TRADE_SUCCESS 或 TRADE_FINISHED 时，支付宝才会认定为买家付款成功
                String tradeStatus = params.get("trade_status");
                if (!"TRADE_SUCCESS".equals(tradeStatus) && !"TRADE_FINISHED".equals(tradeStatus)) {
                    log.error("支付未成功");
                    return result;
                }
                //使用优惠券
                if(zfbOrders.getCouponId()!=null){
                    couponsService.useCoupon(zfbOrders.getCouponId(),2);
                }
                //优惠前的金额
                BigDecimal fee = zfbOrders.getFee();
                //在线补缴
                if(zfbOrders.getPayFrom()==2) {
                    if (zfbOrders.getType() == 4 && StringUtils.isNotEmpty(zfbOrders.getGuid())) {
                        log.info("处理在线补缴");
                        publicBasisService.payment(zfbOrders.getTotal_amount().multiply(new BigDecimal(100)).intValue(), zfbOrders.getGuid(), config.getDepositCard(), zfbOrders.getUid(), zfbOrders.getFee(),6);
                    }
                    if (zfbOrders.getType() == 5) {
                        log.info("账号充值");
                        publicBasisService.saveRecharge(zfbOrders.getFee(), zfbOrders.getUid(), 2);
                    }
                    if (zfbOrders.getType() == 3) {
                        log.info("自主结单");
                        publicBasisService.statement(zfbOrders.getGuid(), zfbOrders.getFee(), zfbOrders.getUid(), zfbOrders.getTotal_amount().intValue(), config.getDepositCard(), 2);
                    }
                    if (zfbOrders.getType() == 2) {
                        log.info("包月缴费");
                        publicBasisService.monthPay(zfbOrders.getMonthRecordId(), zfbOrders.getUid(), zfbOrders.getTotal_amount().multiply(new BigDecimal(100)).intValue(), config.getDepositCard(), 2);
                    }
                }
                zfbOrders.setIsOver(2);
                zfbOrders.setOverTime(new Date());
                zfbOrdersMapper.updateByPrimaryKey(zfbOrders);
                result = "success";
            }
        } catch (Exception e) {
            zfbOrders.setIsOver(3);
            zfbOrders.setOverTime(new Date());
            zfbOrdersMapper.updateByPrimaryKey(zfbOrders);
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public Result<?> payByFrom(ZhiFuBaoPayQuery query)   {
        AlipayClient alipayClient = new DefaultAlipayClient(APP_URL,APP_ID,APP_PRIVATE_KEY,"json",CHARSEt,APP_PUBLIC_KEY,"RSA2");


        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        //支付宝公共参数
        request.setNotifyUrl(NOTIFY_URL);
        request.setReturnUrl(query.getReturn_url());
        //面向对象封装业务参数
        AlipayTradePagePayModel model =new AlipayTradePagePayModel();
        model.setOutTradeNo(query.getOut_trade_no());
        model.setTotalAmount(query.getTotal_amount().toString());
        model.setSubject(query.getSubject());
        model.setProductCode("FAST_INSTANT_TRADE_PAY");
        request.setBizModel(model);
        //执行请求,调用支付宝
        try {
            AlipayTradePagePayResponse response = alipayClient.pageExecute(request);
            if (response.isSuccess()) {
                log.info("调用成功,返回结果:[{}]",response);
                return Result.success(response);
            } else {
                log.info("调用失败!!");
            }
        }catch (AlipayApiException e){
            e.printStackTrace();
            return Result.error(ResultEnum.ERROR,"支付宝pc网页支付下单");
        }
        return null;
    }

    @Override
    public Result<?> tradeQuery(String out_trade_no) {
        AlipayClient alipayClient = new DefaultAlipayClient(APP_URL,APP_ID,APP_PRIVATE_KEY,"json",CHARSEt,APP_PUBLIC_KEY,"RSA2");


        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", out_trade_no);
        //bizContent.put("trade_no", "2014112611001004680073956707");
        request.setBizContent(bizContent.toString());
        try {
            AlipayTradeQueryResponse response = alipayClient.execute(request);
            if(response.isSuccess()){
                System.out.println("调用成功");
            } else {
                System.out.println("调用失败");
            }
            return Result.success(response);
        }catch (AlipayApiException e){
            e.printStackTrace();
            return Result.error(ResultEnum.ERROR,"查询失败");
        }

    }

    @Override
    public Result<?> tradePay(ZhiFuBaoPayQuery query) {
        AlipayClient alipayClient = new DefaultAlipayClient(APP_URL,APP_ID,APP_PRIVATE_KEY,"json",CHARSEt,APP_PUBLIC_KEY,"RSA2");


        AlipayTradePayRequest request = new AlipayTradePayRequest();  //创建API对应的request类
        request.setBizContent ( "{"   +
                "\"out_trade_no\":\""+query.getOut_trade_no()+"\","   +
                "\"scene\":\"bar_code\","   +
                "\"auth_code\":\""+query.getAuth_code()+"\","   + //即用户在支付宝客户端内出示的付款码，使用一次即失效，需要刷新后再去付款
                "\"subject\":\""+query.getSubject()+"\","   +
                "\"timeout_express\":\"2m\","   +
                "\"total_amount\":\""+query.getTotal_amount()+"\""   +
                "}" );  //设置业务参数
        try{
            AlipayTradePayResponse response = alipayClient.execute( request );  //通过alipayClient调用API，获得对应的response类
            return Result.success(response);
        }catch (AlipayApiException e){
            e.printStackTrace();
            return Result.error("支付失败");
        }
        // 根据response中的结果继续业务逻辑处理

    }
}
