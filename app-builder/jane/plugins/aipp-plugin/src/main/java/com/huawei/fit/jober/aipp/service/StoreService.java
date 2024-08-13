/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service;

import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jober.aipp.dto.AppBuilderWaterFlowInfoDto;
import com.huawei.fit.jober.aipp.dto.ModelDto;
import com.huawei.fit.jober.aipp.dto.StoreBasicNodeInfoDto;
import com.huawei.fit.jober.aipp.dto.StoreNodeConfigResDto;
import com.huawei.fit.jober.aipp.dto.ToolDto;
import com.huawei.jade.carver.tool.model.query.ToolQuery;

import java.util.List;

/**
 * Store 相关服务
 *
 * @author 邬涨财
 * @since 2024-05-13
 */
public interface StoreService {
    /**
     * 获取基础节点和工具。
     *
     * @param tag 表示标签的 {@link String}。
     * @param mode 表示选择标签的与和或逻辑的 {@link String}。
     * @param pageNum 表示分页查询的页数的 {@code int}。
     * @param pageSize 表示分页查询的大小的 {@code int}。
     * @param version 表示工具的版本的 {@link String}。
     * @return 表示基础节点和工具的响应的 {@link StoreNodeConfigResDto}。
     */
    StoreNodeConfigResDto getBasicNodesAndTools(String tag, String mode, int pageNum, int pageSize,
            String version);

    /**
     * 获取工具流配置对象列表。
     *
     * @param mode 表示选择标签的与和或逻辑的 {@link String}。
     * @param pageNum 表示分页的页数的 {@link int}。
     * @param pageSize 表示分页的每页个数的 {@link int}。
     * @param version 表示工具的版本的 {@link String}。
     * @return 表示获取到的工具流配置对象列表的 {@link List}{@code <}{@link AppBuilderWaterFlowInfoDto}{@code >}。
     */
    List<AppBuilderWaterFlowInfoDto> getWaterFlowInfos(String mode, int pageNum, int pageSize, String version);

    /**
     * 获取指定插件列表。
     *
     * @param toolQuery 表示插件的动态查询条件的 {@link ToolQuery}。
     * @param operationContext 表示操作上下文的 {@link OperationContext}。
     * @return 表示插件列表和总数的 {@link ToolDto}。
     */
    ToolDto getPlugins(ToolQuery toolQuery, OperationContext operationContext);

    /**
     * 获取基础节点列表。
     *
     * @return 表示基础列表的 {@link List}{@code <}{@link StoreBasicNodeInfoDto}{@code >}。
     */
    List<StoreBasicNodeInfoDto> getBasic();

    /**
     * 获取模型列表。
     *
     * @param taskName 表示任务名的 {@link String}。
     * @param pageNum 表示分页查询的页数的 {@code int}。
     * @param pageSize 表示分页查询的大小的 {@code int}。
     * @return 模型列表的 {@link ModelDto}。
     */
    ModelDto getModels(String taskName, int pageNum, int pageSize);
}
