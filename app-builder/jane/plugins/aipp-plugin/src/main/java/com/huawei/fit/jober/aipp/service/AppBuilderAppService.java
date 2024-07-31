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
 * 应用创建服务接口
 *
 * @author 邬涨财 w00575064
 * @since 2024-04-17
 */
public interface AppBuilderAppService {
    /**
     * 创建应用。
     *
     * @param appId 表示应用唯一标识的 {@link String}。
     * @param dto 表示应用信息的 {@link AppBuilderAppCreateDto}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @param isUpgrade 表示是否为升级操作的 {@link boolean}。
     * @return 创建后的应用信息的 {@link AppBuilderAppDto}。
     */
    @Genericable(id = "com.huawei.fit.jober.aipp.service.app.create")
    AppBuilderAppDto create(String appId, AppBuilderAppCreateDto dto, OperationContext context, boolean isUpgrade);

    /**
     * 更新应用信息。
     *
     * @param appId 表示应用唯一标识的 {@link String}。
     * @param appDto 表示应用信息的 {@link AppBuilderAppDto}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 更新后的应用信息的 {@link Rsp}{@code <}{@link AppBuilderAppDto}{@code >}。
     */
    @Genericable(id = "com.huawei.fit.jober.aipp.service.app.update")
    Rsp<AppBuilderAppDto> updateApp(String appId, AppBuilderAppDto appDto, OperationContext context);

    /**
     * 更新应用配置信息。
     *
     * @param appId 表示应用唯一标识的 {@link String}。
     * @param configDto 表示应用配置信息的 {@link AppBuilderConfigDto}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 更新后的应用配置信息的 {@link Rsp}{@code <}{@link AppBuilderAppDto}{@code >}。
     */
    @Genericable(id = "com.huawei.fit.jober.aipp.service.config.update")
    Rsp<AppBuilderAppDto> updateConfig(String appId, AppBuilderConfigDto configDto, OperationContext context);

    /**
     * 更新应用流程图信息。
     *
     * @param appId 表示应用唯一标识的 {@link String}。
     * @param graphDto 表示应用流程图信息的 {@link AppBuilderFlowGraphDto}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 更新后的应用流程图信息的 {@link Rsp}{@code <}{@link AppBuilderAppDto}{@code >}。
     */
    @Genericable(id = "com.huawei.fit.jober.aipp.service.flow.graph.update")
    Rsp<AppBuilderAppDto> updateFlowGraph(String appId, AppBuilderFlowGraphDto graphDto, OperationContext context);

    /**
     * 发布应用。
     *
     * @param appDto 表示应用信息的 {@link AppBuilderAppDto}。
     * @param contextOf 表示操作上下文的 {@link OperationContext}。
     * @return 发布后的应用信息的 {@link Rsp}{@code <}{@link AippCreateDto}{@code >}。
     */
    @Genericable(id = "com.huawei.fit.jober.aipp.service.app.publish")
    Rsp<AippCreateDto> publish(AppBuilderAppDto appDto, OperationContext contextOf);

    /**
     * 根据名称获取应用配置信息。
     *
     * @param appId 表示应用唯一标识的 {@link String}。
     * @param name 表示应用配置信息的名称的 {@link String}。
     * @return 获取到的应用配置信息的
     *         {@link Optional}{@code <}{@link AppBuilderConfigFormPropertyDto}{@code >}。
     */
    @Genericable(id = "com.huawei.fit.jober.aipp.service.get.property.by.name")
    Optional<AppBuilderConfigFormPropertyDto> getPropertyByName(String appId, String name);

    /**
     * 获取应用的列表信息。
     *
     * @param cond 表示获取条件的 {@link AppQueryCondition}。
     * @param httpRequest 表示HTTP请求的 {@link HttpClassicServerRequest}。
     * @param tenantId 表示租户唯一标识的 {@link String}。
     * @param offset 表示偏移量的 {@code long}。
     * @param limit 表示获取数据的最大个数的 {@code int}。
     * @return 获取到的列表信息集合
     */
    @Genericable(id = "com.huawei.fit.jober.aipp.service.app.list")
    Rsp<RangedResultSet<AppBuilderAppMetadataDto>> list(AppQueryCondition cond,
            HttpClassicServerRequest httpRequest, String tenantId, long offset, int limit);

    /**
     * 删除应用。
     *
     * @param appId 表示应用唯一标识的 {@link String}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     */
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
    @Genericable(id = "com.huawei.fit.jober.aipp.service.app.recent.published")
    List<PublishedAppResDto> recentPublished(AppQueryCondition cond, long offset, int limit, String appId,
            OperationContext context);

    /**
     * 获取应用的发布详情。
     *
     * @param uniqueName 表示应用发布的唯一名称
     * @param context 表示操作上下文的 {@link OperationContext}。
     *
     * @return 获取到的发布详情的 {@link PublishedAppResDto}。
     */
    @Genericable(id = "com.huawei.fit.jober.aipp.service.app.published")
    PublishedAppResDto published(String uniqueName, OperationContext context);
}
