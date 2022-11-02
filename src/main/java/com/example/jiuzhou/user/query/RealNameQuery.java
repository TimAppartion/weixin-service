package com.example.jiuzhou.user.query;

import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.NotNull;

/**
 * @author Appartion
 * @data 2022/11/2
 * 一入代码深似海，从此生活是路人
 */
@Data
public class RealNameQuery {
    private String uid;

    @NotNull(message = "证件类型不可为空")
    private String certificateType;

    @NotNull(message = "证件号码不可为空")
    private String card;
    /**
     * 证件正面
     */
    @NotNull(message = "证件正面照片未上传")
    private String certificateImg;
    /**
     * 证件反面
     */
    @NotNull(message = "证件反面照片未上传")
    private String certificateReversesImg;

}
