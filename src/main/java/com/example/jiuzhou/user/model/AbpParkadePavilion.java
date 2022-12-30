package com.example.jiuzhou.user.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author Appartion
 * @data 2022/12/30
 * 一入代码深似海，从此生活是路人
 */
@Data
@Table(name = "AbpParkadePavilion")
public class AbpParkadePavilion {
    @javax.persistence.Id
    @Column(name="Id")
    private Integer Id;

    @Column(name="Name")
    private String Name;

    @Column(name="Ip")
    private String Ip;

    @Column(name="BerthsecsId")
    private Integer BerthsecsId;

    @Column(name="parkId")
    private Integer parkId;

    @Column(name="Remark")
    private String Remark;

    @Column(name="CreationTime")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date CreationTime;

    @Column(name="Creator")
    private String Creator;

    @Column(name="ModifiedBy")
    private String ModifiedBy;

    @Column(name="ModifiedTime")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date ModifiedTime;
}
