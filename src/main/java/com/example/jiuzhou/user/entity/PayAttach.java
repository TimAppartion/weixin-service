package com.example.jiuzhou.user.entity;

/**
 * @author Appartion
 * @data 2022/10/17
 * 一入代码深似海，从此生活是路人
 */
public class PayAttach {


    private  String couponId;
    //商户订单号
    private String orderId;
    //支付类型
    private int type;
    //购买数量
    private int count;

    private String guid;
    //付款金额
    private String fee;


    public PayAttach(String orderId, int type, int count) {
        this.orderId = orderId;
        this.type = type;
        this.count = count;
    }

    public PayAttach(String orderId, int type, int count, String guid) {
        this.orderId = orderId;
        this.type = type;
        this.count = count;
        this.guid = guid;
    }

    public PayAttach(String orderId, int type, int count, String guid,String couponId) {
        this.orderId = orderId;
        this.type = type;
        this.count = count;
        this.guid = guid;
        this.couponId = couponId;
    }
    public PayAttach(String orderId, int type, int count, String guid,String couponId,String fee) {
        this.orderId = orderId;
        this.type = type;
        this.count = count;
        this.guid = guid;
        this.couponId = couponId;
        this.fee=fee;
    }

    public String getCouponId() {
        return couponId;
    }

    public void setCouponId(String couponId) {
        this.couponId = couponId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }
}
