package com.example.jiuzhou.user.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;

/**
 * @author Appartion
 * @data 2022/12/30
 * 一入代码深似海，从此生活是路人
 */

@Data
@Table(name = "AbpParkadePassage")
public class AbpParkadePassage {
    @javax.persistence.Id
    @Column(name="Id")
    private Integer Id;

    @Column(name="Id")
    private Integer Name;
    private Integer PavilionId;
    private Integer BerthsecsId;
    private Integer parkId;
    private Integer Type;
    private Integer Remark;
    private Integer CreationTime;
    private Integer Creator;
    private Integer ModifiedBy;
    private Integer ModifiedTime;

}
