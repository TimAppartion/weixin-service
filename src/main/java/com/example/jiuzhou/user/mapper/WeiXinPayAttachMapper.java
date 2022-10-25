package com.example.jiuzhou.user.mapper;

import com.example.jiuzhou.user.model.WeiXinPayAttach;
import tk.mybatis.mapper.common.Mapper;

/**
 * @author Appartion
 * @data 2022/10/25
 * 一入代码深似海，从此生活是路人
 */
public interface WeiXinPayAttachMapper extends Mapper<WeiXinPayAttach> {
    WeiXinPayAttach getById(String id);
}
