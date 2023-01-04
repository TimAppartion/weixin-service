package com.example.jiuzhou.user.controller;

import com.example.jiuzhou.common.Enum.ResultEnum;
import com.example.jiuzhou.common.utils.Result;
import com.example.jiuzhou.user.query.ParkLotQrPayQuery;
import com.example.jiuzhou.user.query.ParkLotQrQuery;
import com.example.jiuzhou.user.query.ParkPassageQuery;
import com.example.jiuzhou.user.service.ParkLotService;
import com.jfinal.weixin.sdk.kit.PaymentKit;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Appartion
 * @data 2022/12/29
 * 一入代码深似海，从此生活是路人
 * 停车场功能业务
 */
@Slf4j
@RestController
@RequestMapping("/parkade")
public class ParkadeController {


    @Resource
    private ParkLotService parkLotService;
    /**
     * 场内扫码查询订单
     * @param query
     * @return
     */
    @PostMapping("/parkLotQr")
    public Result<?> parkLotQr(@RequestBody ParkLotQrQuery query){
        return parkLotService.parkLotQr(query);
    }

    /**
     * 微信扫码下单
     * @param query
     * @return
     */
    @PostMapping("/wx/parkLotQrPay")
    public Result<?> WXParkLotQrPay(@RequestBody ParkLotQrPayQuery query){

        if(StringUtils.isEmpty(query.getOpenId())  || StringUtils.isEmpty(query.getGuid())){
            return Result.error(ResultEnum.MISS_DATA);
        }
        return parkLotService.WXParkLotQrPay(query);
    }

    /**
     * 微信扫码下单回调接口
     * @param request
     * @return
     */
    @PostMapping("/wx/parkLotQrPayBack")
    public String WXParkLotQrPayBack(HttpServletRequest request) throws IOException {

        log.info("微信扫码下单回调接口返回参数：{}",request);
        ServletInputStream is = request.getInputStream();
        byte[] bs = new byte[1024];
        int len = -1;
        StringBuilder builder = new StringBuilder();
        while ((len = is.read(bs)) != -1){
            builder.append(new String(bs,0,len));
        }
        String s = builder.toString();

        Map<String,String> map = PaymentKit.xmlToMap(s);

        if(map!=null && "success".equalsIgnoreCase(map.get("result_code"))){
            //支付成功处理业务
            Result result=parkLotService.WXParkLotQrPayBack(map);
            if(result.getCode()!=200){
                return null;
            }
            Map<String,String> responseMap=new HashMap<>();
            responseMap.put("return_code","SUCCESS");
            responseMap.put("return_msg","OK");
            responseMap.put("appid",map.get("appid"));
            responseMap.put("result_code","success");
            return PaymentKit.toXml(responseMap);
        }
        return null;
    }


    /**
     * 支付宝场内支付
     * @param query
     * @return
     */
    @PostMapping("/zfb/parkLotQrPay")
    public Result<?> ZFBParkLotQrPay(@RequestBody ParkLotQrQuery query){
        return Result.success();
//        return parkLotService.ZFBParkLotQrPay(query);
    }
    /**
     * 通道码扫码查询订单
     * 根据车场id 通道id先查询当前通道是否有未支付订单 如果有直接返回
     * 没有根据传的车牌号查询订单
     * @param query
     * @return
     */
    @PostMapping("/passageQr")
    public Result<?> passageQr(@RequestBody ParkPassageQuery query){
        return parkLotService.passageQr(query);
    }

}
