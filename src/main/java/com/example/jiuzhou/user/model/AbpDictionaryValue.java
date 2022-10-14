package com.example.jiuzhou.user.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

/**
 * 字典表
 *
 * @author Appartion
 * @data 2022/10/14
 * 一入代码深似海，从此生活是路人
 */
@Data
@Table(name = "AbpDictionaryValue")
public class AbpDictionaryValue {

    @Column(name="Id")
    private Integer Id;

    @Column(name="ValueCode")
    private String ValueCode;

    @Column(name="TypeCode")
    private String TypeCode;

    @Column(name="ValueData")
    private String ValueData;

    @Column(name="TenantId")
    private Integer TenantId;

    @Column(name="IsActive")
    private Integer IsActive;

    @Column(name="IsDefault")
    private Integer IsDefault;

    @Column(name="sort")
    private Integer sort;

    @Column(name="Remark")
    private String Remark;
}
