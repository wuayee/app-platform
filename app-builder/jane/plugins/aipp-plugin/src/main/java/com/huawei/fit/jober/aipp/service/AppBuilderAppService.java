/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service;

import com.huawei.fit.http.server.HttpClassicServerRequest;
import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jane.common.response.Rsp;
import com.huawei.fit.jober.aipp.dto.AippCreateDto;
import com.huawei.fit.jober.aipp.dto.AppBuilderAppCreateDto;
import com.huawei.fit.jober.aipp.dto.AppBuilderAppDto;
import com.huawei.fit.jober.aipp.dto.AppBuilderAppMetadataDto;
import com.huawei.fit.jober.aipp.dto.AppBuilderConfigDto;
import com.huawei.fit.jober.aipp.dto.AppBuilderConfigFormPropertyDto;
import com.huawei.fit.jober.aipp.dto.AppBuilderFlowGraphDto;
import com.huawei.fit.jober.common.RangedResultSet;
import com.huawei.fitframework.annotation.Genericable;

import java.util.Optional;

/**
 * @author 邬涨财 w00575064
 * @since 2024-04-17
 */
public interface AppBuilderAppService {
    @Genericable(id = "a389e19779fcc245b7a6135a46eb5864")
    Rsp<AppBuilderAppDto> query(HttpClassicServerRequest httpRequest, String appId);

    @Genericable(id = "a389e20209fcc245b7a6135a46eb5864")
    Rsp<AppBuilderAppDto> create(String appId, AppBuilderAppCreateDto dto, OperationContext context);

    @Genericable(id = "a389e20219fcc245b7a6135a46eb5864")
    Rsp<AppBuilderAppDto> updateApp(String appId, AppBuilderAppDto appDto, OperationContext context);

    @Genericable(id = "a389e20229fcc245b7a6135a46eb5864")
    Rsp<AppBuilderAppDto> updateConfig(String appId, AppBuilderConfigDto configDto, OperationContext context);

    @Genericable(id = "a389e20239fcc245b7a6135a46eb5864")
    Rsp<AppBuilderAppDto> updateFlowGraph(String appId, AppBuilderFlowGraphDto graphDto, OperationContext context);

    @Genericable(id = "a389e19779fcc245b7a6135a46eb5865")
    Rsp<AippCreateDto> publish(AppBuilderAppDto appDto, OperationContext contextOf);

    @Genericable(id = "a389e19779fcc245b7a6135a46eb5863")
    Rsp<AippCreateDto> debug(AppBuilderAppDto appDto, OperationContext contextOf);

    @Genericable(id = "a389e19779fcc245b7a6135a46eb5866")
    Optional<AppBuilderConfigFormPropertyDto> getPropertyByName(String appId, String name);

    @Genericable(id = "a389e19779fcc245b7a6135a46eb5850")
    Rsp<RangedResultSet<AppBuilderAppMetadataDto>> list(HttpClassicServerRequest httpRequest, String tenantId,
            long offset, int limit);

    @Genericable(id = "aebcb2ec94a6bb4180a1f460e6b90ccd")
    void delete(String appId, OperationContext context);
}
