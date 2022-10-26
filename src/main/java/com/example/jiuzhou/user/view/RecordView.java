package com.example.jiuzhou.user.view;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Appartion
 * @data 2022/10/26
 * 一入代码深似海，从此生活是路人
 */
@Data
public class RecordView {
    private Integer Id;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date CarPayTime;

    private Integer Status;
    private Integer CarType;
    private Integer BerthsecId;
    private String guid;
    private String PlateNumber;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date CarInTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date CarOutTime;

    private String BerthsecName;
    private BigDecimal Prepaid;
    private BigDecimal Money;
    private Integer BerthNumber;
    private Integer RateId;
    private String  Evaluate;
    private String Content;
    private Integer ParkType;
    private String  carStopTime;
}
