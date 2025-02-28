/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.validation.impl;

import modelengine.fit.jober.taskcenter.util.DynamicSqlExecutor;
import modelengine.fit.jober.taskcenter.validation.AbstractValidator;
import modelengine.fit.jober.taskcenter.validation.RelationshipValidator;

import lombok.AllArgsConstructor;
import modelengine.fit.jober.common.ErrorCodes;
import modelengine.fit.jober.common.exceptions.NotFoundException;
import modelengine.fitframework.annotation.Component;

import java.util.ArrayList;
import java.util.Objects;

/**
 * {@link RelationshipValidator} 的默认实现
 *
 * @author 姚江
 * @since 2023-11-13 14:28
 */
@Component
@AllArgsConstructor
public class RelationshipValidatorImpl extends AbstractValidator implements RelationshipValidator {
    private final DynamicSqlExecutor executor;

    @Override
    public void validateTaskExistInTenant(String taskId, String tenantId) {
        // 兼容tenant输入名字的情况
        String sql = "SELECT 1 " + "FROM task INNER JOIN tenant on tenant.id = task.tenant_id "
                + "WHERE task.id = ? and (task.tenant_id = ? or tenant.name = ?) " + "LIMIT 1";

        Object result = this.executor.executeScalar(sql, new ArrayList<Object>() {
            {
            add(taskId);
            add(tenantId);
            add(tenantId);
        }});

        if (!Objects.equals(1, result)) {
            throw new NotFoundException(ErrorCodes.TASK_NOT_IN_TENANT, taskId, tenantId);
        }
    }

    @Override
    public void validateTaskTypeExistInTask(String typeId, String taskId) {
        String sql = "SELECT 1 FROM task_type WHERE id = ? AND task_id = ? LIMIT 1";

        Object result = this.executor.executeScalar(sql, new ArrayList<Object>() {
            {
            add(typeId);
            add(taskId);
        }});

        if (!Objects.equals(1, result)) {
            sql = "SELECT 1 FROM task_type AS tt " + "INNER JOIN task_tree_task AS ttt ON ttt.tree_id = tt.tree_id "
                    + "WHERE tt.id = ? AND ttt.task_id = ? " + "LIMIT 1";
            result = this.executor.executeScalar(sql, new ArrayList<Object>() {
                {
                add(typeId);
                add(taskId);
            }});
            extracted(typeId, taskId, result);
        }
    }

    private void extracted(String typeId, String taskId, Object result) {
        if (!Objects.equals(1, result)) {
            throw new NotFoundException(ErrorCodes.TYPE_NOT_IN_TASK, typeId, taskId);
        }
    }

    @Override
    public void validateSourceExistInTaskType(String sourceId, String typeId) {
        String sql = "SELECT 1 FROM task_node_source WHERE source_id = ? AND node_id = ? LIMIT 1";
        Object result = this.executor.executeScalar(sql, new ArrayList<Object>() {
            {
            add(sourceId);
            add(typeId);
        }});

        if (!Objects.equals(1, result)) {
            throw new NotFoundException(ErrorCodes.SOURCE_NOT_IN_TYPE, sourceId, typeId);
        }
    }
}
