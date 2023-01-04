package com.example.jiuzhou.user.mapper;

import com.example.jiuzhou.user.model.AbpParkadeAccessDetail;
import com.example.jiuzhou.user.view.ParkLotQrView;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

/**
 * @author Appartion
 * @data 2022/12/30
 * 一入代码深似海，从此生活是路人
 */
public interface AbpParkadeAccessDetailMapper extends Mapper<AbpParkadeAccessDetail> {

    ParkLotQrView getLotQrByPlateNumber(@Param("plateNumber")String plateNumber, @Param("parkId")Integer parkId);
    AbpParkadeAccessDetail getByGuid(@Param("guid")String guid);
}
