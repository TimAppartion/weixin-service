package com.example.jiuzhou.user.model;

import lombok.Data;

/**
 * @author Appartion
 * @data 2022/11/30
 * 一入代码深似海，从此生活是路人
 */
@Data
public class TextMessage {
    private String ToUserName;
    private String FromUserName;
    private long CreateTime;
    private String MsgType;
    private String Content;
}
