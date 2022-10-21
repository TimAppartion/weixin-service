package com.example.jiuzhou.user.view;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Appartion
 * @data 2022/10/21
 * 一入代码深似海，从此生活是路人
 */
@Data
public class ParkOrderView {
    private Integer Id;
    private Integer Status;
    private String guid;
    private String PlateNumber;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date CarInTime;

    private String BerthsecName;

    private BigDecimal Prepaid;

    private String BerthNumber;

    private Integer BerthsecId;

    private Integer ParkType;
}
