package com.example.jiuzhou.user.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author Appartion
 * @data 2022/10/19
 * 一入代码深似海，从此生活是路人
 */
@Data
@Table(name = "Opinion")
public class Opinion {
    @javax.persistence.Id
    @Column(name="Id")
    private Integer  Id;

    @Column(name="uid")
    private String  uid;

    @Column(name="type")
    private Integer  type;

    @Column(name="context")
    private String  context;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name="createTime")
    private Date  createTime;

    @Column(name="status")
    private Integer  status;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name="opinionTime")
    private Date opinionTime;

    @Column(name="handleResult")
    private String  handleResult;

}
