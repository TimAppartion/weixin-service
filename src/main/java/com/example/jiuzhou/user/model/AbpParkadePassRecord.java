package com.example.jiuzhou.user.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author Appartion
 * @data 2022/12/29
 * 一入代码深似海，从此生活是路人
 */
@Data
@Table(name = "AbpParkadePassRecord")
public class AbpParkadePassRecord {
    @javax.persistence.Id
    @Column(name="Id")
    private Integer Id;

    @Column(name="PassageId")
    private Integer PassageId;

    @Column(name="PlateNumber")
    private String PlateNumber;

    @Column(name="CreationTime")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date CreationTime;
}
