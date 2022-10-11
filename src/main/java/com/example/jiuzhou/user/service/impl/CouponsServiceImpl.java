package com.example.jiuzhou.user.service.impl;

import com.example.jiuzhou.user.mapper.CouponsDetailsMapper;
import com.example.jiuzhou.user.model.CouponsDetails;
import com.example.jiuzhou.user.service.CouponsService;
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
