package com.example.weixin.user.model;

import lombok.Data;

import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Data
@Table(name = "CouponsDetails")
public class CouponsDetails implements Serializable {
    private Integer Id;
    private Integer PlanId;
    private String UserId;
    private Date ProvideTime;
    private Integer Status;
    private Date UseTime;
}
