/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service;

import com.huawei.fit.http.server.HttpClassicServerRequest;
import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jane.common.response.Rsp;
import com.huawei.fit.jober.aipp.condition.AppQueryCondition;
import com.huawei.fit.jober.aipp.dto.AippCreateDto;
import com.huawei.fit.jober.aipp.dto.AppBuilderAppCreateDto;
import com.huawei.fit.jober.aipp.dto.AppBuilderAppDto;
import com.huawei.fit.jober.aipp.dto.AppBuilderAppMetadataDto;
import com.huawei.fit.jober.aipp.dto.AppBuilderConfigDto;
import com.huawei.fit.jober.aipp.dto.AppBuilderConfigFormPropertyDto;
import com.huawei.fit.jober.aipp.dto.AppBuilderFlowGraphDto;
import com.huawei.fit.jober.aipp.dto.PublishedAppResDto;
import com.huawei.fit.jober.common.RangedResultSet;
import com.huawei.fitframework.annotation.Genericable;

import java.util.List;
import java.util.Optional;

/**
 * @author 邬涨财 w00575064
 * @since 2024-04-17
 */
public interface AppBuilderAppService {
    @Genericable(id = "com.huawei.fit.jober.aipp.service.app.create")
    AppBuilderAppDto create(String appId, AppBuilderAppCreateDto dto, OperationContext context);

    @Genericable(id = "com.huawei.fit.jober.aipp.service.app.update")
    Rsp<AppBuilderAppDto> updateApp(String appId, AppBuilderAppDto appDto, OperationContext context);

    @Genericable(id = "com.huawei.fit.jober.aipp.service.config.update")
    Rsp<AppBuilderAppDto> updateConfig(String appId, AppBuilderConfigDto configDto, OperationContext context);

    @Genericable(id = "com.huawei.fit.jober.aipp.service.flow.graph.update")
    Rsp<AppBuilderAppDto> updateFlowGraph(String appId, AppBuilderFlowGraphDto graphDto, OperationContext context);

    @Genericable(id = "com.huawei.fit.jober.aipp.service.app.publish")
    Rsp<AippCreateDto> publish(AppBuilderAppDto appDto, OperationContext contextOf);

    @Genericable(id = "com.huawei.fit.jober.aipp.service.get.property.by.name")
    Optional<AppBuilderConfigFormPropertyDto> getPropertyByName(String appId, String name);

    @Genericable(id = "com.huawei.fit.jober.aipp.service.app.list")
    Rsp<RangedResultSet<AppBuilderAppMetadataDto>> list(AppQueryCondition cond, HttpClassicServerRequest httpRequest, String tenantId,
            long offset, int limit);

    @Genericable(id = "com.huawei.fit.jober.aipp.service.app.delete")
    void delete(String appId, OperationContext context);

    /**
     * 获取应用的历史版本信息。
     *
     * @param cond 表示获取条件的 {@link AppQueryCondition}。
     * @param offset 表示偏移量的 {@code long}。
     * @param limit 表示获取数据的最大个数的 {@code int}。
     * @param appId 表示应用唯一标识的 {@link String}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 获取到的历史版本信息集合的 {@link List}{@code <}{@link PublishedAppResDto}{@code >}。
     */
    @Genericable(id = "com.huawei.fit.jober.aipp.service.app.published")
    List<PublishedAppResDto> published(AppQueryCondition cond, long offset, int limit, String appId,
            OperationContext context);
}
