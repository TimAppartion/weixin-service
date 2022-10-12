package com.example.jiuzhou.user.service;

import com.example.jiuzhou.common.utils.Result;
import com.example.jiuzhou.user.query.OauthQuery;

public interface ZhiFuBaoService {
    Result<?> index(OauthQuery query);
}
