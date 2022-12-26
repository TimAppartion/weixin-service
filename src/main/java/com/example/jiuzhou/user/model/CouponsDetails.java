package com.example.jiuzhou.user.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Data
@Table(name = "CouponsDetails")
public class CouponsDetails implements Serializable {
    @javax.persistence.Id
    @Column(name="Id")
    private String Id;

    @Column(name="PlanId")
    private Integer PlanId;

    @Column(name="UId")
    private String UId;

    @Column(name="ProvideTime")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date ProvideTime;

    @Column(name="Status")
    private Integer Status;

    @Column(name="UseTime")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date UseTime;
}
