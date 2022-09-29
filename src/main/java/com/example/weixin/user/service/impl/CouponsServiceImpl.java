package com.example.weixin.user.service.impl;

import com.example.weixin.user.mapper.CouponsDetailsMapper;
import com.example.weixin.user.model.CouponsDetails;
import com.example.weixin.user.service.CouponsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class CouponsServiceImpl implements CouponsService {

    @Resource
    private CouponsDetailsMapper couponsDetailsMapper;

    @Override
    public List<CouponsDetails> getList() {
        return couponsDetailsMapper.getList();
    }

}
