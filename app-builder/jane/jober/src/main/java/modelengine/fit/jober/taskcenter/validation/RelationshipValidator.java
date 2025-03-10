/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.validation;

/**
 * 为TenantId、TaskId、TaskTypeId、SourceId提供从属关系的校验器
 *
 * @author 姚江
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
