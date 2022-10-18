package com.example.jiuzhou.user.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Appartion
 * @data 2022/10/18
 * 一入代码深似海，从此生活是路人
 */
@Data
@Table(name = "MonthCard")
public class MonthCard {
    @javax.persistence.Id
    @Column(name="Id")
    private Integer Id;

    @Column(name="ParkType")
    private Integer ParkType;

    @Column(name="TimeLimt")
    private Integer TimeLimt;

    @Column(name="Status")
    private Integer Status;

    @Column(name="WorkTime")
    private String WorkTime;

    @Column(name="WeekendTime")
    private String WeekendTime;

    @Column(name="HolidayTime")
    private String HolidayTime;
}
