package com.example.jiuzhou.user.job;

import com.example.jiuzhou.user.service.MineService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author Appartion
 * @data 2022/12/27
 * 一入代码深似海，从此生活是路人
 */
@Component
public class getMonthlyCardDetailJob {
    @Resource
    private MineService mineService;
    @XxlJob("getMonthlyCardDetail")
    public ReturnT<String> execute(String param) {
        mineService.userInfo("8a87258931424937abef3cf5d8174e3f");
        return ReturnT.SUCCESS;
    }
}
