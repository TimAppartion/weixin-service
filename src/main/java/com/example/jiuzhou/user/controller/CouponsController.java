package com.example.jiuzhou.user.controller;


import com.example.jiuzhou.common.Enum.ResultEnum;
import com.example.jiuzhou.user.mapper.CouponsDetailsMapper;
import com.example.jiuzhou.user.model.CouponsDetails;
import com.example.jiuzhou.common.utils.Result;
import com.example.jiuzhou.user.query.CouponsQuery;
import com.example.jiuzhou.user.service.CouponsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/Coupons")
@ResponseBody
public class CouponsController {
    @Autowired
    private CouponsService couponsService;

    @Resource
    private CouponsDetailsMapper couponsDetailsMapper;


    @RequestMapping("/getList")
    public List<CouponsDetails> getList(){
        log.info("我直接一个哒唛");
        return couponsService.getList();
    }

    @PostMapping("/getCouponsList")
    public Result<?> getCouponsList(@RequestBody CouponsQuery query){
        if(StringUtils.isEmpty(query.getUid())){
            return  Result.error(ResultEnum.MISS_DATA,"用户id不可为空");
        }
        if(StringUtils.isNotEmpty(query.getFee())){
            query.setOther("and (" +
                    "( cp.Type IN ( 2, 4 ) AND cp.CouponsMoney * "+query.getFee()+"  >= 0.01 and (cp.TermMoney<"+query.getFee()+" or cp.TermMoney is null) )" +
                    " OR " +
                    "(" +
                    " cp.Type IN ( 1, 3 ) AND ( "+query.getFee()+" - cp.CouponsMoney ) >= 0.01 and  (cp.TermMoney<= "+query.getFee()+"  OR cp.TermMoney IS NULL )" +
                    ") " +
                    ") ");
        }
        return Result.success(couponsDetailsMapper.getCouponsList(query));
    }

}