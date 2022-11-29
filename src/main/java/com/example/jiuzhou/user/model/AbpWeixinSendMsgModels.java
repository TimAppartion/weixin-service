package com.example.jiuzhou.user.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author Appartion
 * @data 2022/11/29
 * 一入代码深似海，从此生活是路人
 */
@Data
@Table(name = "AbpWeixinSendMsgModels")
public class AbpWeixinSendMsgModels {
    @javax.persistence.Id
    @Column(name = "Id")
    private Integer Id;

    @Column(name = "Key")
    private String Key;

    @Column(name = "Msg")
    private String Msg;

    @Column(name = "CreationTime")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date CreationTime;

    @Column(name = "IsActive")
    private Integer IsActive;

    @Column(name = "TenantId")
    private Integer TenantId;
}
