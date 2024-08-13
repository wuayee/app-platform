/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.validation;

import java.util.Map;

/**
 * 为任务实例提供校验器。
 *
 * @author 梁济时
 * @since 2023-08-15
 */
public interface InstanceValidator extends Validator {
    /**
     * typeId
     *
     * @param sourceId sourceId
     * @return String
     */
    String typeId(String sourceId);

    /**
     * sourceId
     *
     * @param sourceId sourceId
     * @return String
     */
    String sourceId(String sourceId);

    /**
     * info
     *
     * @param info info
     * @return Map<String, Object>
     */
    Map<String, Object> info(Map<String, Object> info);

    /**
     * 对任务唯一标识进行校验。
     *
     * @param taskId 表示任务唯一标识的 {@link String}。
     * @return 表示通过校验的任务唯一标识的 {@link String}。
     */
    String validateTaskId(String taskId);
}
