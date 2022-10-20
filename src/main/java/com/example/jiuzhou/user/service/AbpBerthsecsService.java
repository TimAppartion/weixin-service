package com.example.jiuzhou.user.service;

import com.example.jiuzhou.user.model.AbpBerthsecs;
import com.example.jiuzhou.user.query.BerthsQuery;
import com.github.pagehelper.PageInfo;

public interface AbpBerthsecsService {
    /**
     * 附件站点
     * @param query
     * @return
     */
    PageInfo<AbpBerthsecs> nearSite(BerthsQuery query);
}
