package com.example.jiuzhou.user.mapper;


//import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.jiuzhou.user.model.TUser;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
//import tk.mybatis.mapper.common.Mapper;

@Repository
public interface TUserMapper extends Mapper<TUser> {
    TUser getByTel(@Param("tel")String tel);
}
