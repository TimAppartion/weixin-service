package com.example.jiuzhou.user.service.impl;

import com.example.jiuzhou.common.Enum.ResultEnum;
import com.example.jiuzhou.common.utils.Result;
import com.example.jiuzhou.user.mapper.*;
import com.example.jiuzhou.user.model.*;
import com.example.jiuzhou.user.query.BindCarQuery;
import com.example.jiuzhou.user.query.OrderQuery;
import com.example.jiuzhou.user.query.RealNameQuery;
import com.example.jiuzhou.user.query.UpdateUserQuery;
import com.example.jiuzhou.user.service.MineService;
import com.example.jiuzhou.user.view.MonthCarsView;
import com.example.jiuzhou.user.view.UserInfoView;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import org.apache.commons.lang.StringUtils;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @Resource
    private AbpMonthlyCarsMapper abpMonthlyCarsMapper;
    @Resource
    private MonthRuleMapper monthRuleMapper;
    @Resource
    private AbpDictionaryValueMapper abpDictionaryValueMapper;

    @Resource
    private AbpDeductionRecordsMapper abpDeductionRecordsMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Result<?> saveBingCarNumber(BindCarQuery query) {
        TUser tUser=tUserMapper.getByCarNumber(query.getPlateNumber());
        if(tUser!=null){
            return Result.error(ResultEnum.ERROR,"车辆已被绑定");
        }
        ExtOtherAccount account = extOtherAccountMapper.getByUid(query.getUid());
        if(account==null){
            return Result.error(ResultEnum.ERROR,"没有生成账号，请重新绑定手机");
        }
        if(updateCarNumber(query.getPlateNumber(), query.getUid())){
            ExtOtherPlateNumber extOtherPlateNumber=new ExtOtherPlateNumber();
            extOtherPlateNumber.setAssignedOtherAccountId(account.getId());
            extOtherPlateNumber.setPlateNumber(query.getPlateNumber());
            extOtherPlateNumber.setCarColor(1);
            extOtherPlateNumber.setCarType(1);
            extOtherPlateNumber.setIsActive(1);
            extOtherPlateNumber.setCreatorUserId(TENANTID);
            extOtherPlateNumber.setCreationTime(new Date());
            extOtherPlateNumber.setIsDeleted(0);
            extOtherPlateNumber.setSort(1);
            extOtherPlateNumberMapper.insertOne(extOtherPlateNumber);
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
        }else{
            return Result.error(ResultEnum.ERROR,"未绑定【"+query.getPlateNumber()+"】车牌");
        }
        try {
            //先删除用户信息中车牌
            tUserMapper.updateByPrimaryKey(tUser);
            //删除管理表车牌信息
            ExtOtherAccount extOtherAccount = extOtherAccountMapper.getByUid(query.getUid());
            Example example = new Example(ExtOtherPlateNumber.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("AssignedOtherAccountId", extOtherAccount.getId()).andEqualTo("PlateNumber", query.getPlateNumber());
            extOtherPlateNumberMapper.deleteByExample(example);
        }catch (Exception e){
            log.error("解绑失败:{}",query);
            e.printStackTrace();
            return Result.error(ResultEnum.ERROR , "解绑失败");
        }
        return Result.success();
    }

    @Override
    public Result<?> userInfo(String uid) {
        UserInfoView tUser=tUserMapper.getUserInfo(uid);
        if(tUser!=null){
            List<MonthCarsView> monthlyCars=abpMonthlyCarsMapper.monthCad(uid);
            tUser.setMonthCard(monthlyCars.size());
        }else {
            return Result.error(ResultEnum.ERROR,"查询用户信息失败");
        }
        return Result.success(tUser);
    }

    @Override
    public Result<?> monthCad(String uid) {
        List<MonthCarsView> monthlyCars=abpMonthlyCarsMapper.monthCad(uid);
        return Result.success(monthlyCars);
    }

    @Override
    public Result<?> getMonthlyCardDetail(String parkType) {

        List<MonthRule> monthRuleList=monthRuleMapper.getMonthRuleByParkType(parkType);

        Example example2 = new Example(AbpDictionaryValue.class);
        Example.Criteria criteria2 = example2.createCriteria();
        criteria2.andEqualTo("TenantId", TENANTID).andEqualTo("TypeCode", "A10022").andEqualTo("IsActive", "1");
        List<AbpDictionaryValue> abpDictionaryValueList=abpDictionaryValueMapper.selectByExample(example2);
        Map<String,Object> map=new HashMap<>();
        map.put("rule",monthRuleList);
        map.put("monthType",abpDictionaryValueList);
        return Result.success(map);
    }

    @Override
    public Result<?> orderList(OrderQuery query) {
        PageHelper.startPage(query.getPageNumber(), query.getPageSize());
        List<AbpDeductionRecords> list=abpDeductionRecordsMapper.getOrderList(query);
        return Result.success(new PageInfo (list));
    }

    @Override
    public Result<?> updateUser(UpdateUserQuery query) {
        TUser tUser = tUserMapper.getByUid(query.getUid());
        if(tUser==null){
            return Result.error(ResultEnum.ERROR,"未查询到用户信息");
        }
        if(StringUtils.isNotEmpty(query.getNickName())){
            tUser.setNickName(query.getNickName());
        }
        if(query.getSex()!=null){
            tUser.setSex(query.getSex());
        }
        if(StringUtils.isNotEmpty(query.getBirthday())){
            tUser.setBirthday(query.getBirthday());
        }
        tUserMapper.updateByPrimaryKey(tUser);
        return Result.success("用户信息修改完成");
    }

    @Override
    public Result<?> realName(RealNameQuery query) {
        TUser tUser = tUserMapper.getByUid(query.getUid());
        if(tUser==null){
            return Result.error(ResultEnum.MISS_DATA,"未查询到用户信息");
        }
        tUser.setCertificateType(query.getCertificateType());
        tUser.setCertificateImg(query.getCertificateImg());
        tUser.setCertificateReversesImg(query.getCertificateReversesImg());
        tUser.setCard(query.getCard());
        tUserMapper.updateByPrimaryKey(tUser);
        return Result.success("实名验证信息登记完成");
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
        tUserMapper.updateByPrimaryKey(tUser);
        return true;
    }
}
