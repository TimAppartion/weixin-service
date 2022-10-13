package com.example.jiuzhou.user.service.impl;

import com.example.jiuzhou.common.Enum.ResultEnum;
import com.example.jiuzhou.common.utils.Result;
import com.example.jiuzhou.user.mapper.ExtOtherAccountMapper;
import com.example.jiuzhou.user.mapper.ExtOtherPlateNumberMapper;
import com.example.jiuzhou.user.mapper.TUserMapper;
import com.example.jiuzhou.user.model.ExtOtherAccount;
import com.example.jiuzhou.user.model.ExtOtherPlateNumber;
import com.example.jiuzhou.user.model.TUser;
import com.example.jiuzhou.user.query.BindCarQuery;
import com.example.jiuzhou.user.service.MineService;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Appartion
 * @data 2022/10/13
 * 一入代码深似海，从此生活是路人
 */

@Service
@Slf4j
public class MineServiceImpl implements MineService {

    private static final Prop prop = PropKit.use("weixin.properties");
    private static Integer TENANTID=Integer.valueOf(prop.get("tenantId"));
    @Resource
    private TUserMapper tUserMapper;
    @Resource
    private ExtOtherAccountMapper extOtherAccountMapper;
    @Resource
    private ExtOtherPlateNumberMapper extOtherPlateNumberMapper;

    @Override
    public Result<?> saveBingCarNumber(BindCarQuery query) {
        TUser tUser=tUserMapper.getByCarNumber(query.getPlateNumber());
        if(ObjectUtils.isEmpty(tUser)){
            return Result.error(ResultEnum.ERROR,"车辆已被绑定");
        }
        ExtOtherAccount account = extOtherAccountMapper.getByUid(query.getUid());
        if(updateCarNumber(query.getPlateNumber(), query.getUid())){
            ExtOtherPlateNumber extOtherPlateNumber=new ExtOtherPlateNumber();
            extOtherPlateNumber.setAuthentication(account.getId());
            extOtherPlateNumber.setPlateNumber(query.getPlateNumber());
            extOtherPlateNumber.setCarColor(1);
            extOtherPlateNumber.setCarType(1);
            extOtherPlateNumber.setIsActive(1);
            extOtherPlateNumber.setCreatorUserId(TENANTID);
            extOtherPlateNumber.setCreationTime(new Date());
            extOtherPlateNumber.setIsDeleted(0);
            extOtherPlateNumberMapper.insert(extOtherPlateNumber);
        }else{
            return Result.error(ResultEnum.ERROR,"绑定车牌失败");
        }
        return Result.success("绑定成功");
    }

    @Override
    public Result<?> relievePlatNumber(BindCarQuery query) {
        TUser tUser=tUserMapper.getByUid(query.getUid());
        if(!StringUtils.isEmpty(tUser.getCarNumber1())&&tUser.getCarNumber1().equals(query.getPlateNumber())){
            tUser.setCarNumber1(null);
        }else if(!StringUtils.isEmpty(tUser.getCarNumber2())&&tUser.getCarNumber2().equals(query.getPlateNumber())){
            tUser.setCarNumber2(null);
        }else if(!StringUtils.isEmpty(tUser.getCarNumber3())&&tUser.getCarNumber3().equals(query.getPlateNumber())) {
            tUser.setCarNumber3(null);
        }
        return Result.success();
    }

    public boolean updateCarNumber(String plateNumber,String uid){
        TUser tUser=tUserMapper.getByUid(uid);
        if(StringUtils.isEmpty(tUser.getCarNumber1())){
            tUser.setCarNumber1(plateNumber);
        }else if(StringUtils.isEmpty(tUser.getCarNumber2())){
            tUser.setCarNumber2(plateNumber);
        }else if(StringUtils.isEmpty(tUser.getCarNumber3())){
            tUser.setCarNumber3(plateNumber);
        }else {
            return false;
        }
        return true;
    }
}
