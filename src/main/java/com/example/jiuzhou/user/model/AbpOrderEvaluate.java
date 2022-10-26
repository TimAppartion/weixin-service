package com.example.jiuzhou.user.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Appartion
 * @data 2022/10/24
 * 一入代码深似海，从此生活是路人
 */
@Data
@Table(name = "AbpOrderEvaluate")
public class AbpOrderEvaluate {
    @javax.persistence.Id
    @Column(name = "Id")
    private String Id;

    @Column(name = "Evaluate")
    private String Evaluate;

    @Column(name = "OrderId")
    private String OrderId;

    @Column(name = "Content")
    private String Content;
}
