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

        //???????????????????????????
        Map<String ,Object> responseMap=new HashMap<>();
        //????????????token
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
            return Result.error(ResultEnum.ERROR,"?????????????????????????????????");
        }
    }

    @Override
    public Result<?> pay(ZhiFuBaoPayQuery query) throws AlipayApiException {
        //??????????????????AlipayClient
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
                return Result.error(ResultEnum.MISS_DATA,"????????????????????????");
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


        //??????????????????AlipayClient
        AlipayClient alipayClient = new DefaultAlipayClient(APP_URL,APP_ID, APP_PRIVATE_KEY,
                "json", "UTF-8", APP_PUBLIC_KEY,"RSA2");
        //??????????????????
        AlipayTradeWapPayRequest alipayRequest = new AlipayTradeWapPayRequest ();
        alipayRequest.setReturnUrl(query.getReturn_url());
        alipayRequest.setNotifyUrl(NOTIFY_URL);
        alipayRequest.setBizContent("{\"out_trade_no\":\"" + query.getOut_trade_no() + "\","
                + "\"total_amount\":\"" + query.getTotal_amount() + "\","
                + "\"subject\":\"" + query.getSubject() + "\","
                + "\"quit_url\":\"" + query.getQuit_url() + "\","
                + "\"product_code\":\"QUICK_WAP_WAY\"}");



        //??????
        log.info("????????????????????????????????????{}",JSONObject.toJSONString(alipayRequest));
        try {
            AlipayTradeWapPayResponse response = alipayClient.pageExecute(alipayRequest,"get");
            return Result.success(response);
        }catch (AlipayApiException e){
            e.printStackTrace();
            return Result.error(ResultEnum.ERROR,"????????????????????????");
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
            log.info("????????????????????????{},????????????id:{}" ,zfbOrders,outTradeNo);
            //??????????????????
            boolean signVerified = AlipaySignature.rsaCheckV1(params,
                    APP_PUBLIC_KEY,
                    AlipayConstants.CHARSET_UTF8,
                    AlipayConstants.SIGN_TYPE_RSA2);
            if (!signVerified) {
                zfbOrders.setIsOver(3);
                zfbOrders.setOverTime(new Date());
                zfbOrdersMapper.updateByPrimaryKey(zfbOrders);
                log.error("????????????,????????????????????????!");
                return result;
            }
            if(zfbOrders.getIsOver()==1) {
                //?????????????????? seller_id????????? out_trade_no ?????????????????????????????????
                String sellerId = params.get("seller_id");
                if (!sellerId.equals(PID)) {
                    log.error("??????PID????????????");
                    return result;
                }
                //?????? app_id ????????????????????????
                String appId = params.get("app_id");
                if (!appId.equals(APP_ID)) {
                    log.error("app_id????????????");
                    return result;
                }
                //???????????????????????????????????????????????????????????? TRADE_SUCCESS ??? TRADE_FINISHED ????????????????????????????????????????????????
                String tradeStatus = params.get("trade_status");
                if (!"TRADE_SUCCESS".equals(tradeStatus) && !"TRADE_FINISHED".equals(tradeStatus)) {
                    log.error("???????????????");
                    return result;
                }
                //???????????????
                if(zfbOrders.getCouponId()!=null){
                    couponsService.useCoupon(zfbOrders.getCouponId(),2);
                }
                //??????????????????
                BigDecimal fee = zfbOrders.getFee();
                //????????????
                if(zfbOrders.getPayFrom()==2) {
                    if (zfbOrders.getType() == 4 && StringUtils.isNotEmpty(zfbOrders.getGuid())) {
                        log.info("??????????????????");
                        publicBasisService.payment(zfbOrders.getTotal_amount().multiply(new BigDecimal(100)).intValue(), zfbOrders.getGuid(), config.getDepositCard(), zfbOrders.getUid(), zfbOrders.getFee(),6);
                    }
                    if (zfbOrders.getType() == 5) {
                        log.info("????????????");
                        publicBasisService.saveRecharge(zfbOrders.getFee(), zfbOrders.getUid(), 2);
                    }
                    if (zfbOrders.getType() == 3) {
                        log.info("????????????");
                        publicBasisService.statement(zfbOrders.getGuid(), zfbOrders.getFee(), zfbOrders.getUid(), zfbOrders.getTotal_amount().intValue(), config.getDepositCard(), 2);
                    }
                    if (zfbOrders.getType() == 2) {
                        log.info("????????????");
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
        //?????????????????????
        request.setNotifyUrl(NOTIFY_URL);
        request.setReturnUrl(query.getReturn_url());
        //??????????????????????????????
        AlipayTradePagePayModel model =new AlipayTradePagePayModel();
        model.setOutTradeNo(query.getOut_trade_no());
        model.setTotalAmount(query.getTotal_amount().toString());
        model.setSubject(query.getSubject());
        model.setProductCode("FAST_INSTANT_TRADE_PAY");
        request.setBizModel(model);
        //????????????,???????????????
        try {
            AlipayTradePagePayResponse response = alipayClient.pageExecute(request);
            if (response.isSuccess()) {
                log.info("????????????,????????????:[{}]",response);
                return Result.success(response);
            } else {
                log.info("????????????!!");
            }
        }catch (AlipayApiException e){
            e.printStackTrace();
            return Result.error(ResultEnum.ERROR,"?????????pc??????????????????");
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
                System.out.println("????????????");
            } else {
                System.out.println("????????????");
            }
            return Result.success(response);
        }catch (AlipayApiException e){
            e.printStackTrace();
            return Result.error(ResultEnum.ERROR,"????????????");
        }

    }

    @Override
    public Result<?> tradePay(ZhiFuBaoPayQuery query) {
        AlipayClient alipayClient = new DefaultAlipayClient(APP_URL,APP_ID,APP_PRIVATE_KEY,"json",CHARSEt,APP_PUBLIC_KEY,"RSA2");


        AlipayTradePayRequest request = new AlipayTradePayRequest();  //??????API?????????request???
        request.setBizContent ( "{"   +
                "\"out_trade_no\":\""+query.getOut_trade_no()+"\","   +
                "\"scene\":\"bar_code\","   +
                "\"auth_code\":\""+query.getAuth_code()+"\","   + //?????????????????????????????????????????????????????????????????????????????????????????????????????????
                "\"subject\":\""+query.getSubject()+"\","   +
                "\"timeout_express\":\"2m\","   +
                "\"total_amount\":\""+query.getTotal_amount()+"\""   +
                "}" );  //??????????????????
        try{
            AlipayTradePayResponse response = alipayClient.execute( request );  //??????alipayClient??????API??????????????????response???
            return Result.success(response);
        }catch (AlipayApiException e){
            e.printStackTrace();
            return Result.error("????????????");
        }
        // ??????response????????????????????????????????????

    }
}
