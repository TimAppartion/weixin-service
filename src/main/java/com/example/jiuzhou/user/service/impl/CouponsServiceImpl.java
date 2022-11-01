package com.example.jiuzhou.user.service.impl;

import com.example.jiuzhou.common.utils.Result;
import com.example.jiuzhou.user.mapper.CouponsDetailsMapper;
import com.example.jiuzhou.user.model.CouponsDetails;
import com.example.jiuzhou.user.service.CouponsService;
import com.example.jiuzhou.user.view.CouponsView;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class CouponsServiceImpl implements CouponsService {

    @Resource
    private CouponsDetailsMapper couponsDetailsMapper;

    @Override
    public List<CouponsDetails> getList() {
        return couponsDetailsMapper.getList();
    }

    @Override
    public Result<?> canUseCoupon(BigDecimal fee, String uid, Integer couponId) {
        CouponsView details= couponsDetailsMapper.getDetails(couponId);
        if(details!=null) {
            if (!uid.equals(details.getUid())) {
                return Result.error("用户关联和使用优惠券不匹配");
            }
            if (details.getType() == 1) {
                //固定减免
                if (details.getCouponsMoney().compareTo(fee) < 0) {
                    fee = fee.subtract(details.getCouponsMoney());
                } else {
                    return Result.error("优惠券优惠金额大于支付金额");
                }
            } else if (details.getType() == 2) {
                //固定打折
                fee = fee.multiply(details.getCouponsMoney());
            } else if (details.getType() == 3) {
                //条件减免
                if (fee.compareTo(details.getTermMoney()) > 0 && fee.compareTo(details.getCouponsMoney()) >= 0) {
                    fee = fee.subtract(details.getCouponsMoney());
                } else {
                    return Result.error("未达到优惠券使用金额");
                }
            } else if (details.getType() == 4) {
                //条件打折
                if (fee.compareTo(details.getTermMoney()) > 0) {
                    fee = fee.multiply(details.getCouponsMoney());
                } else {
                    return Result.error("未达到优惠券使用金额");
                }
            }
        }
        //返回分单位的金额
        return Result.success(fee.setScale(2,BigDecimal.ROUND_HALF_UP));
    }

    @Override
    public void useCoupon(Integer couponId, Integer status) {
        CouponsView details=couponsDetailsMapper.getDetails(couponId);
        CouponsDetails model=new CouponsDetails();

        model.setId(details.getId());
        model.setPlanId(details.getPlanId());
        model.setUId(details.getUid());
        model.setProvideTime(details.getProvideTime());
        model.setStatus(status);
        model.setUseTime(new Date());
        couponsDetailsMapper.updateByPrimaryKey(model);
    }

}
