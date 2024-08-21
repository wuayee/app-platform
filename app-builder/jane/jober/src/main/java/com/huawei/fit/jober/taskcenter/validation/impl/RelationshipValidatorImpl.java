/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.validation.impl;

import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.exceptions.NotFoundException;
import com.huawei.fit.jober.taskcenter.util.DynamicSqlExecutor;
import com.huawei.fit.jober.taskcenter.validation.AbstractValidator;
import com.huawei.fit.jober.taskcenter.validation.RelationshipValidator;
import modelengine.fitframework.annotation.Component;

import lombok.AllArgsConstructor;

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
