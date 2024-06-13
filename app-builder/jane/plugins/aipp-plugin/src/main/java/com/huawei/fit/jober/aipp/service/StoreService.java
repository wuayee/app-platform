/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service;

import com.huawei.fit.jober.aipp.dto.AppBuilderWaterFlowInfoDto;
import com.huawei.fit.jober.aipp.dto.StoreNodeConfigResDto;

import java.util.List;

/**
 * @author 邬涨财 w00575064
 * @since 2024-05-13
 */
public interface StoreService {
    StoreNodeConfigResDto getBasicNodesAndTools(String tag, int pageNum, int pageSize);

    List<AppBuilderWaterFlowInfoDto> getWaterFlowInfos(int pageNum, int pageSize);

    List<String> getModels(String taskName, int pageNum, int pageSize);
}
