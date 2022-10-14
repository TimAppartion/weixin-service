package com.example.jiuzhou.user.mapper;

import com.example.jiuzhou.user.model.ExtOtherAccount;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

/**
 * @author Appartion
 * @data 2022/10/12
 * 一入代码深似海，从此生活是路人
 */
public interface ExtOtherAccountMapper extends Mapper<ExtOtherAccount> {
    Integer  insetOne(@Param("query")ExtOtherAccount account);

    ExtOtherAccount getByUid(@Param("uid") String uid);


    ExtOtherAccount getByCardNo(@Param("CardNo") String CardNo);
}
