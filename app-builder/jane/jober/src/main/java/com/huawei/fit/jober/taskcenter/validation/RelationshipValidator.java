/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.validation;

/**
 * 为TenantId、TaskId、TaskTypeId、SourceId提供从属关系的校验器
 *
 * @author yWX1299574
 * @since 2023-11-13 14:16
 */
public interface RelationshipValidator extends Validator {
    /**
     * 校验task是否属于指定的tenant
     *
     * @param taskId 任务Id
     * @param tenantId 租户Id
     */
    void validateTaskExistInTenant(String taskId, String tenantId);

    /**
     * 校验taskType是否属于指定的task
     *
     * @param taskId 任务Id
     * @param typeId 任务类型Id
     */
    void validateTaskTypeExistInTask(String typeId, String taskId);

    /**
     * 校验source是否属于指定的taskType
     *
     * @param sourceId 来源Id
     * @param typeId 任务类型Id
     */
    void validateSourceExistInTaskType(String sourceId, String typeId);
}
