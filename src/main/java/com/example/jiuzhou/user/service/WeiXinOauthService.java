package com.example.jiuzhou.user.service;

import com.example.jiuzhou.common.utils.Result;
import com.example.jiuzhou.user.query.OauthQuery;

public interface WeiXinOauthService {
    /**
     * 微信授权
     * @param query
     * @return
     */
    Result<?> index(OauthQuery query);

    /**
     * 验证通过绑定手机号
     * @param query
     * @return
     */
    Result<?> bindPhone(OauthQuery query);
}
