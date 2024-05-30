/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service;

import com.huawei.fit.http.server.HttpClassicServerRequest;
import com.huawei.fit.jane.common.response.Rsp;
import com.huawei.fit.jober.aipp.domain.AppBuilderForm;
import com.huawei.fit.jober.aipp.dto.AppBuilderFormDto;

import java.util.List;

/**
 * 表单服务
 *
 * @author 邬涨财 w00575064
 * @since 2024-04-19
 */
public interface AppBuilderFormService {

    /**
     * 根据类型查询
     *
     * @param httpRequest http请求
     * @param type 类型
     * @param tenantId 租户id
     * @return 表单列表
     */
    Rsp<List<AppBuilderFormDto>> queryByType(HttpClassicServerRequest httpRequest, String type, String tenantId);

    /**
     * 根据id查找表单
     *
     * @param id 唯一id
     * @return 表单
     */
    AppBuilderForm selectWithId(String id);
}
