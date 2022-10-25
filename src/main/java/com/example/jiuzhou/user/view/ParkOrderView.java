package com.example.jiuzhou.user.view;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Appartion
 * @data 2022/10/21
 * 一入代码深似海，从此生活是路人
 */
public class ParkOrderView implements Serializable {
    private static final long serialVersionUID = -4294461808433313537L;
    private Integer Id;
    private Integer Status;
    private String guid;
    private String PlateNumber;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date CarInTime;

    private String BerthsecName;

    private BigDecimal Prepaid;

    private String BerthNumber;

    private Integer BerthsecId;

    private Integer ParkType;
    private String carStopTime;

    public Integer getId() {
        return Id;
    }

    public void setId(Integer id) {
        Id = id;
    }

    public Integer getStatus() {
        return Status;
    }

    public void setStatus(Integer status) {
        Status = status;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getPlateNumber() {
        return PlateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        PlateNumber = plateNumber;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getCarInTime() {
        return CarInTime;
    }

    public void setCarInTime(Date carInTime) {
        CarInTime = carInTime;
    }

    public String getBerthsecName() {
        return BerthsecName;
    }

    public void setBerthsecName(String berthsecName) {
        BerthsecName = berthsecName;
    }

    public BigDecimal getPrepaid() {
        return Prepaid;
    }

    public void setPrepaid(BigDecimal prepaid) {
        Prepaid = prepaid;
    }

    public String getBerthNumber() {
        return BerthNumber;
    }

    public void setBerthNumber(String berthNumber) {
        BerthNumber = berthNumber;
    }

    public Integer getBerthsecId() {
        return BerthsecId;
    }

    public void setBerthsecId(Integer berthsecId) {
        BerthsecId = berthsecId;
    }

    public Integer getParkType() {
        return ParkType;
    }

    public void setParkType(Integer parkType) {
        ParkType = parkType;
    }

    public String getCarStopTime() {
        return carStopTime;
    }

    public void setCarStopTime(String carStopTime) {
        this.carStopTime = carStopTime;
    }
}
