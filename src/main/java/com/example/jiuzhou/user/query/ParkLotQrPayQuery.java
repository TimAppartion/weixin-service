package com.example.jiuzhou.user.query;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author Appartion
 * @data 2022/12/30
 * 一入代码深似海，从此生活是路人
 * 扫码支付
 */
@Data
public class ParkLotQrPayQuery {

    /**
     * 订单号
     */
    private String guid;

    private String openId;

    private String userId;

    private Integer parkId;

    private BigDecimal money;

    /**
     * 扫码类型 1-场内码 2-车道码
     */
    @NotNull(message = "扫码类型不可为空")
    private Integer qrType;

    private Integer passageId;

    /**
     * 支付宝返回页面
     */
    private String returnUrl;

    /**
     * 支付宝中途退出返回页面
     */
    private String quitUrl;

    private String subject;
}
