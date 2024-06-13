/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service;

import com.huawei.fit.jober.aipp.dto.AppBuilderWaterFlowInfoDto;
import com.huawei.fit.jober.aipp.dto.StoreNodeConfigResDto;
import com.huawei.jade.store.entity.transfer.ModelData;

import java.util.List;

/**
 * @author 邬涨财 w00575064
 * @since 2024-05-13
 */
public interface StoreService {
    /**
     * 获取基础节点和工具。
     *
     * @param tag 表示标签的 {@link String}。
     * @param pageNum 表示分页查询的页数的 {@code int}。
     * @param pageSize 表示分页查询的大小的 {@code int}。
     * @return 表示基础节点和工具的响应的 {@link StoreNodeConfigResDto}。
     */
    StoreNodeConfigResDto getBasicNodesAndTools(String tag, int pageNum, int pageSize);

    List<AppBuilderWaterFlowInfoDto> getWaterFlowInfos(int pageNum, int pageSize);

    /**
     * 获取模型列表。
     *
     * @param taskName 表示任务名的 {@link String}。
     * @param pageNum 表示分页查询的页数的 {@code int}。
     * @param pageSize 表示分页查询的大小的 {@code int}。
     * @return 模型列表的 {@link List}{@code <}{@link String}{@code >}。
     */
    List<ModelData> getModels(String taskName, int pageNum, int pageSize);
}
