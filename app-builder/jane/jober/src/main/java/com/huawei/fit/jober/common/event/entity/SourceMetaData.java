/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.common.event.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 数据源信息实体。
 *
 * @author 陈镕希
 * @since 2023-08-21
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SourceMetaData {
    /**
     * 任务定义唯一标识的 {@link String}。
     */
    private String taskId;

    /**
     * 任务定义类型唯一标识的 {@link String}。
     */
    private String typeId;

    /**
     * 任务数据源定义唯一标识的 {@link String}。
     */
    private String taskSourceId;

    /**
     * 任务数据源类型的 {@link String}。
     */
    private String type;
}
