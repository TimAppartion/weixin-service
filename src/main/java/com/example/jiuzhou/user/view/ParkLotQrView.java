package com.example.jiuzhou.user.view;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Appartion
 * @data 2022/12/30
 * 一入代码深似海，从此生活是路人
 */
@Data
public class ParkLotQrView {
    private Integer Id;
    private Integer BusinessDetailId;
    private Date CarInTime;
    private Date CarOutTime;
    /**
     * 支付时间
     */
    private Date CarPayTime;
    private Integer ParkId;
    private String PlateNumber;
    private String guid;
    private Integer BerthsecId;

    private String CarType;
    /**
     * 预付
     */
    private BigDecimal Prepaid;

    /**
     * 已缴
     */
    private BigDecimal Receivable;
    /**
     * 实收
     */
    private BigDecimal Money;
    /**
     * 应收
     */
    private BigDecimal FactReceive;

    /**
     * 本次应缴 计算当前时间减去已缴金额
     */
    private BigDecimal thisPay;
}
