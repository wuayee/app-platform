/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service;

import com.huawei.fit.http.server.HttpClassicServerRequest;
import com.huawei.fit.jane.common.response.Rsp;
import com.huawei.fit.jober.aipp.dto.AppBuilderFormDto;

import java.util.List;

/**
 * @author 邬涨财 w00575064
 * @since 2024-04-19
 */
public interface AppBuilderFormService {
    Rsp<List<AppBuilderFormDto>> queryByType(HttpClassicServerRequest httpRequest, String type);
}
