package com.example.jiuzhou.user.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import java.util.Date;

/**
 * @author Appartion
 * @data 2022/11/30
 * 一入代码深似海，从此生活是路人
 */
@Data
public class AbpWeixinMsg {
    @Id
    @Column(name = "Id")
    private Integer Id;

    @Column(name = "Msg")
    private String Msg;

    @Column(name = "CreationTime")
    private Date CreationTime;

    @Column(name = "TenantId")
    private Integer TenantId;
}
