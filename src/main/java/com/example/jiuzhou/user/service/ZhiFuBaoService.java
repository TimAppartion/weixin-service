package com.example.jiuzhou.user.service;

import com.alipay.api.AlipayApiException;
import com.example.jiuzhou.common.utils.Result;
import com.example.jiuzhou.user.query.OauthQuery;
import com.example.jiuzhou.user.query.ZhiFuBaoPayQuery;

import java.util.Map;

public interface ZhiFuBaoService {
    Result<?> index(OauthQuery query);

    Result<?> pay(ZhiFuBaoPayQuery query) throws AlipayApiException;

    /**
     * 支付宝手机网页下单返回支付链接
     * 手机下单后根据前端生成的交易号记录业务数据进表
     * 后支付宝付完钱后根据交易号查询出业务数据处理业务流程
     * https://opendocs.alipay.com/open/02ivbs?scene=21
     * @param query
     * @return
     */
    Result<?> aliPay(ZhiFuBaoPayQuery query);

    String callback( Map<String, String> params);

    Result<?>payByFrom(ZhiFuBaoPayQuery query);

    /**
     * 支付宝订单查询接口
     * https://opendocs.alipay.com/open/02ivbt
     * @param out_trade_no
     * @return
     */
    Result<?> tradeQuery(String out_trade_no);

    /**
     * 扫码支付收款
     * @param query
     * @return
     */
    Result<?> tradePay(ZhiFuBaoPayQuery query);
}
