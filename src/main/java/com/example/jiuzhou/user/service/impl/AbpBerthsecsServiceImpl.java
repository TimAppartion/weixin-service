package com.example.jiuzhou.user.service.impl;

import com.example.jiuzhou.common.utils.MathUtils;
import com.example.jiuzhou.user.mapper.AbpBerthsecsMapper;
import com.example.jiuzhou.user.model.AbpBerthsecs;
import com.example.jiuzhou.user.query.BerthsecsQuery;
import com.example.jiuzhou.user.service.AbpBerthsecsService;
import com.example.jiuzhou.user.view.AbpBerthsecsView;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
public class AbpBerthsecsServiceImpl implements AbpBerthsecsService {

    @Resource
    private AbpBerthsecsMapper abpBerthsecsMapper;

    @Override
    public PageInfo<AbpBerthsecs> nearSite(BerthsecsQuery query) {
        double lng = Double.parseDouble(query.getLng());
        double lat = Double.parseDouble(query.getLat());

        PageHelper.startPage(query.getPageIndex(),query.getPageSize());
        List<AbpBerthsecsView> list=abpBerthsecsMapper.nearSite(query);
        for(AbpBerthsecsView view:list){
            if(StringUtils.isNotEmpty(view.getLatitude())&&StringUtils.isNotEmpty(view.getLongitude()) ){
                double lat_ = Double.parseDouble(view.getLatitude());
                double lng_ = Double.parseDouble(view.getLongitude());
                double dis = MathUtils.LantitudeLongitudeDist(lng,lat,lng_,lat_);
                double dis_ = dis/1000;
                if(dis_*1000>1000){
                    view.setDistance_(String.format("%.1f",dis_));
                }else{
                    view.setDistance_(String.format("%.2f",dis_));
                }
            }
        }
        PageInfo pageInfo=new PageInfo(list);
        return pageInfo;
    }
}
