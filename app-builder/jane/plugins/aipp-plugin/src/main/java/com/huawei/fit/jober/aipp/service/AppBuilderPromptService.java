/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service;

import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jane.common.response.Rsp;
import com.huawei.fit.jober.aipp.dto.AppBuilderPromptCategoryDto;
import com.huawei.fit.jober.aipp.dto.AppBuilderPromptDto;

import java.util.List;

/**
 * 灵感大全的两个查询接口service
 *
 * @author 姚江 yWX1299574
 * @since 2024-04-26
 */
public interface AppBuilderPromptService {
    Rsp<AppBuilderPromptDto> queryInspirations(String appId, String categoryId, OperationContext context);

    Rsp<List<AppBuilderPromptCategoryDto>> listPromptCategories(String appId, OperationContext context);
}
