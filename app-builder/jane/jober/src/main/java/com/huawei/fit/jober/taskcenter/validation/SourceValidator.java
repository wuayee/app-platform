/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.validation;

import com.huawei.fit.jober.taskcenter.domain.SourceType;

/**
 * 为任务数据源提供校验器。
 *
 * @author 陈镕希 c00572808
 * @since 2023-08-15
 */
public interface SourceValidator extends Validator {
    /**
     * 对任务数据源唯一标识进行校验。
     *
     * @param sourceId 表示任务数据源唯一标识的 {@link String}。
     * @return 表示通过校验的任务数据源唯一标识的 {@link String}。
     */
    String validateSourceId(String sourceId);

    /**
     * 对任务数据源名称进行校验。
     *
     * @param name 表示任务数据源名称的 {@link String}。
     * @return 表示通过校验的任务数据源唯一标识的 {@link String}。
     */
    String validateSourceName(String name);

    /**
     * 对任务数据源应用名称进行校验。
     *
     * @param app 表示任务数据源应用名称的 {@link String}。
     * @return 表示通过校验的任务数据源应用名称的 {@link String}。
     */
    String validateSourceApp(String app);

    /**
     * 对任务数据源类型进行校验。
     *
     * @param type 表示任务数据源类型的 {@link String}。
     * @return 表示通过校验的任务数据源类型的 {@link SourceType}。
     */
    SourceType validateSourceType(String type);

    /**
     * 对任务定义唯一标识进行校验。
     *
     * @param taskId 表示任务定义唯一标识的 {@link String}。
     * @return 表示通过校验的任务唯一标识的 {@link String}。
     */
    String validateTaskId(String taskId);

    /**
     * 对任务类型唯一标识进行校验。
     *
     * @param typeId 表示任务类型唯一标识的 {@link String}。
     * @return 表示通过校验的任务唯一标识的 {@link String}。
     */
    String validateTypeId(String typeId);
}
