package com.example.jiuzhou.user.query;

import lombok.Data;

/**
 * @author Appartion
 * @data 2022/10/19
 * 一入代码深似海，从此生活是路人
 */
@Data
public class UpdateUserQuery {
    private String uid;
    private String nickName;
    private Integer sex;
    private String birthday;
    private String tel;
}
