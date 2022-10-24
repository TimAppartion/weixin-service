package com.example.jiuzhou.user.model;

import lombok.Data;

import javax.persistence.Table;

/**
 * @author Appartion
 * @data 2022/10/24
 * 一入代码深似海，从此生活是路人
 */
@Data
@Table(name = "AbpOrderEvaluate")
public class AbpOrderEvaluate {
    private String Id;
    private String Evaluate;
    private String OrderId;
    private String Content;
}
