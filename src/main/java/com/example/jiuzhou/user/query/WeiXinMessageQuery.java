package com.example.jiuzhou.user.query;

import lombok.Data;

/**
 * @author Appartion
 * @data 2022/10/14
 * 一入代码深似海，从此生活是路人
 */
@Data
public class WeiXinMessageQuery {
    private String openId;
    private String carNumber;
    private String parkName;
    private String carInTime;
    private String carOutTime;
    private String remake;
}
