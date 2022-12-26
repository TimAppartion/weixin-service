package com.example.jiuzhou.user.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * @author Appartion
 * @data 2022/10/25
 * 一入代码深似海，从此生活是路人
 */
@Data
@Table(name = "WeiXinPayAttach")
public class WeiXinPayAttach {
    @Id
    @Column(name = "Id")
    private String Id;

    @Column(name = "couponId")
    private String couponId;

    @Column(name = "orderId")
    private String orderId;

    @Column(name = "type")
    private Integer type;

    @Column(name = "count")
    private Integer count;

    @Column(name = "guid")
    private String guid;

    @Column(name = "fee")
    private BigDecimal fee;

}
