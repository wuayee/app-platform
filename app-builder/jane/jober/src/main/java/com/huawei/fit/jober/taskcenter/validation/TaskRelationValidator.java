/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.validation;

import com.huawei.fit.jane.task.domain.RelationType;

/**
 * 为任务关联提供校验
 *
 * @author 罗书强
 * @since 2024-01-02
 */
public interface TaskRelationValidator {
    /**
     * 关联关系的唯一标识。
     *
     * @param id 表示任务关联关系的唯一标识的 {@link String}。
     * @return 表示符合要求的任务关联关系的唯一标识的 {@link String}。
     */
    String id(String id);

    /**
     * 校验任务关联方的唯一标识。
     *
     * @param objectId1 表示任务关联方的唯一标识的 {@link String}。
     * @return 表示符合要求的任务关联方的唯一标识的 {@link String}。
     */
    String objectId1(String objectId1);

    /**
     * 校验任务关联方的类型。
     *
     * @param objectType1 表示任务关联方的类型的 {@link String}。
     * @return 表示符合要求的任务关联方的类型的 {@link String}。
     */
    String objectType1(String objectType1);

    /**
     * 校验任务被关联方的唯一标识。
     *
     * @param objectId2 表示任务被关联方的唯一标识的 {@link String}。
     * @return 表示符合要求的任务被关联方的唯一标识的 {@link String}。
     */
    String objectId2(String objectId2);

    /**
     * 校验任务被关联方的类型。
     *
     * @param objectType2 表示任务被关联方的类型的 {@link String}。
     * @return 表示符合要求的任务被关联方的类型的 {@link String}。
     */
    String objectType2(String objectType2);

    /**
     * 校验任务关联的类型。
     *
     * @param relationType 表示任务关联的类型的 {@link String}。
     * @return 表示符合要求的任务关联的类型的 {@link String}。
     */
    RelationType relationType(String relationType);
}
