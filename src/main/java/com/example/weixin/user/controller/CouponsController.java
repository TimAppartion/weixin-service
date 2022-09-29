package com.example.weixin.user.controller;


import com.example.weixin.user.mapper.CouponsDetailsMapper;
import com.example.weixin.user.model.CouponsDetails;
import com.example.weixin.user.service.CouponsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

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
        return couponsService.getList();
    }

}