package com.example.jiuzhou.user.view;

import lombok.Data;

import java.util.Date;

/**
 * 用户包月卡车辆信息
 *
 * @author Appartion
 * @data 2022/10/14
 * 一入代码深似海，从此生活是路人
 */
@Data
public class MonthCarsView {
    private Date BeginTime;

    private Integer CarType;

    private Integer CompanyId;

    private Date CreationTime;

    private Integer CreatorUserId;

    private String DeleterUserId;

    private String DeletionTime;

    private Date EndTime;

    private Integer Id;

    private boolean IsDeleted;

    private Integer IsSms;

    private Date LastModificationTime;

    private Integer LastModifierUserId;

    private double Money;

    private String MonthyType;

    private String ParkIds;

    private String ParkName;

    private Integer ParkType;

    private String PlateNumber;

    private String Telphone;

    private Integer TenantId;

    private String VehicleOwner;

    private Integer Version;
}
