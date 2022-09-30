package com.example.weixin.user.controller;


import com.example.weixin.user.mapper.CouponsDetailsMapper;
import com.example.weixin.user.model.CouponsDetails;
import com.example.weixin.user.service.CouponsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@Slf4j

@RestController
@RequestMapping("/controller/Coupons")
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

}