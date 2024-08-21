/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.service.impl;

import static com.huawei.fit.jober.taskcenter.util.Sqls.longValue;
import static modelengine.fitframework.util.ObjectUtils.nullIf;

import com.huawei.fit.jane.task.util.Dates;
import com.huawei.fit.jane.task.util.Entities;
import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.ServerInternalException;
import com.huawei.fit.jober.common.exceptions.BadRequestException;
import com.huawei.fit.jober.common.exceptions.ConflictException;
import com.huawei.fit.jober.common.exceptions.NotFoundException;
import com.huawei.fit.jober.taskcenter.declaration.TreeDeclaration;
import com.huawei.fit.jober.taskcenter.domain.TreeEntity;
import com.huawei.fit.jober.taskcenter.filter.TreeFilter;
import com.huawei.fit.jober.taskcenter.service.TreeService;
import com.huawei.fit.jober.taskcenter.util.DynamicSqlExecutor;
import com.huawei.fit.jober.taskcenter.util.ExecutableSql;
import com.huawei.fit.jober.taskcenter.util.Sqls;
import com.huawei.fit.jober.taskcenter.validation.TreeValidator;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.model.RangedResultSet;
import modelengine.fitframework.transaction.Transactional;
import modelengine.fitframework.util.ObjectUtils;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 为 {@link TreeService} 提供实现。
 *
 * @author 梁济时
 * @since 2023-08-17
 */
@Component
@RequiredArgsConstructor
public class TreeServiceImpl implements TreeService {
    private static final String SQL_MODULE = "tree";

    private final DynamicSqlExecutor executor;

    private final TreeValidator validator;

    private static TreeEntity toEntity(Map<String, Object> row) {
        TreeEntity entity = new TreeEntity();
        entity.setId(ObjectUtils.cast(row.get("id")));
        entity.setName(ObjectUtils.cast(row.get("name")));
        entity.setChildCount(0);
        entity.setTaskId(Entities.ignoreEmpty(ObjectUtils.cast(row.get("task_id"))));
        Entities.fillTraceInfo(entity, row);
        return entity;
    }

    @Override
    @Transactional
    public TreeEntity create(TreeDeclaration declaration, OperationContext context) {
        String name = this.validator.name(
                declaration.getName().required(() -> new BadRequestException(ErrorCodes.TREE_NAME_REQUIRED)), context);
        String taskId = this.validator.taskId(declaration.getTaskId().get(), context);
        String treeId = Entities.generateId();
        String operator = context.operator();
        LocalDateTime operationTime = LocalDateTime.now();
        LocalDateTime operationTimeUtc = Dates.toUtc(operationTime);
        List<Object> args = Arrays.asList(treeId, name, operator, operationTimeUtc, operator, operationTimeUtc);
        String sql = Sqls.script(SQL_MODULE, "insert-prefix") + Sqls.script(SQL_MODULE, "insert-values");
        if (this.executor.executeUpdate(sql, args) < 1) {
            throw new ServerInternalException("Failed to save task tree into database.");
        }
        if (!Entities.emptyId().equals(taskId)) {
            args = Arrays.asList(Entities.generateId(), treeId, taskId);
            sql = Sqls.script(SQL_MODULE, "insert-task-prefix") + Sqls.script(SQL_MODULE, "insert-task-values");
            if (this.executor.executeUpdate(sql, args) < 1) {
                throw new ServerInternalException("Failed to save task tree task into database.");
            }
        }
        TreeEntity entity = new TreeEntity();
        entity.setId(treeId);
        entity.setName(name);
        entity.setTaskId(Entities.ignoreEmpty(taskId));
        entity.setCreator(operator);
        entity.setCreationTime(operationTime);
        entity.setLastModifier(entity.getCreator());
        entity.setLastModificationTime(entity.getCreationTime());
        return entity;
    }

    @Override
    @Transactional
    public void patch(String treeId, TreeDeclaration declaration, OperationContext context) {
        StringBuilder sql = new StringBuilder();
        sql.append(Sqls.script(SQL_MODULE, "update-prefix"));
        List<Object> args = new LinkedList<>();
        args.add(context.operator());
        args.add(Dates.toUtc(LocalDateTime.now()));
        String actualName = null;
        if (declaration.getName().defined()) {
            actualName = this.validator.name(declaration.getName().get(), context);
            args.add(actualName);
            sql.append(", name = ?");
        }
        sql.append(" WHERE id = ?");
        String actualTreeId = Entities.validateId(treeId, () -> new NotFoundException(ErrorCodes.TREE_NOT_FOUND));
        args.add(actualTreeId);
        if (this.executor.executeUpdate(sql.toString(), args) < 1) {
            throw new NotFoundException(ErrorCodes.TREE_NOT_FOUND);
        }
        if (declaration.getTaskId().defined()) {
            String taskId = validator.taskId(declaration.getTaskId().get(), context);
            args = Collections.singletonList(treeId);
            String sqlString = Sqls.script(SQL_MODULE, "select-task-by-tree");
            List<Map<String, Object>> rows = this.executor.executeQuery(sqlString, args);
            if (rows.isEmpty()) {
                // 当前不存在关联关系，此时创建关联关系
                args = Arrays.asList(Entities.generateId(), treeId, taskId);
                sqlString = Sqls.script(SQL_MODULE, "insert-task-prefix") + Sqls.script(SQL_MODULE,
                        "insert-task-values");
                if (this.executor.executeUpdate(sqlString, args) < 1) {
                    throw new ServerInternalException("Failed to save task tree task into database.");
                }
            } else {
                // 当前存在关联关系，使用更新
                args = Arrays.asList(taskId, treeId);
                sqlString = Sqls.script(SQL_MODULE, "update-task-of-tree");
                if (this.executor.executeUpdate(sqlString, args) < 1) {
                    throw new ServerInternalException("Failed to update task tree task into database.");
                }
            }
        }
        // 兼容性，当修改任务树的名称时，若存在对应的任务定义，则同时修改任务定义的名称。
        if (actualName != null) {
            String compatibleSql = "SELECT task_id FROM task_tree_task WHERE tree_id = ?";
            List<Object> compatibleArgs = Collections.singletonList(actualTreeId);
            String relatedTaskId = ObjectUtils.cast(this.executor.executeScalar(compatibleSql, compatibleArgs));
            if (relatedTaskId != null) {
                compatibleSql = "UPDATE task SET name = ? WHERE id = ?";
                compatibleArgs = Arrays.asList(actualName, relatedTaskId);
                this.executor.executeUpdate(compatibleSql, compatibleArgs);
            }
        }
    }

    @Override
    @Transactional
    public void delete(String treeId, OperationContext context) {
        if (this.countNodes(treeId) > 0) {
            throw new ConflictException(ErrorCodes.TREE_DELETING_HAS_NODES);
        }
        List<Object> selectTaskArgs = Collections.singletonList(treeId);
        String sqlString = Sqls.script(SQL_MODULE, "select-task-by-tree");
        List<Map<String, Object>> rows = this.executor.executeQuery(sqlString, selectTaskArgs);
        String sql;
        if (!rows.isEmpty()) {
            List<String> taskIdList = rows.stream()
                    .map(columnMap -> ObjectUtils.<String>cast(columnMap.get("task_id")))
                    .collect(Collectors.toList());
            sql = Sqls.script(SQL_MODULE, "delete-tasks-by-ids");
            ExecutableSql.resolve(sql, Collections.singletonMap("taskIds", taskIdList)).executeUpdate(this.executor);
        }
        List<Object> args = Collections.singletonList(
                Entities.validateId(treeId, () -> new NotFoundException(ErrorCodes.TREE_NOT_FOUND)));
        sql = Sqls.script(SQL_MODULE, "delete-by-id");
        if (this.executor.executeUpdate(sql, args) < 1) {
            throw new NotFoundException(ErrorCodes.TREE_NOT_FOUND);
        }
        sql = Sqls.script(SQL_MODULE, "delete-task-by-tree");
        this.executor.executeUpdate(sql, args);
    }

    private long countNodes(String treeId) {
        String sql = "SELECT COUNT(1) FROM task_type WHERE tree_id = ?";
        List<Object> args = Collections.singletonList(treeId);
        return longValue(this.executor.executeScalar(sql, args));
    }

    @Override
    @Transactional
    public TreeEntity retrieve(String treeId, OperationContext context) {
        List<Object> args = Collections.singletonList(
                Entities.validateId(treeId, () -> new NotFoundException(ErrorCodes.TREE_NOT_FOUND)));
        String sql = Sqls.script(SQL_MODULE, "select") + "WHERE \"tr\".\"id\" = ?";
        List<Map<String, Object>> rows = this.executor.executeQuery(sql, args);
        if (rows.isEmpty()) {
            throw new NotFoundException(ErrorCodes.TREE_NOT_FOUND);
        } else {
            return toEntity(rows.get(0));
        }
    }

    @Override
    @Transactional
    public RangedResultSet<TreeEntity> list(TreeFilter filter, long offset, int limit, OperationContext context) {
        StringBuilder whereSql = new StringBuilder();
        whereSql.append(" WHERE 1 = 1 AND \"t\".\"tenant_id\" = ?");
        List<Object> whereArgs = new LinkedList<>();
        whereArgs.add(context.tenantId());
        Optional<RangedResultSet<TreeEntity>> treeEntityRanged =
                getTreeEntityRangedResultSet(filter, offset, limit, whereSql, whereArgs);
        if (treeEntityRanged.isPresent()) {
            return treeEntityRanged.get();
        }

        long total = (ObjectUtils.<Number>cast(this.executor.executeScalar(Sqls.script(SQL_MODULE, "count") + whereSql,
                whereArgs))).longValue();

        String sql = Sqls.script(SQL_MODULE, "select") + whereSql + " ORDER BY created_at OFFSET ? LIMIT ?";
        List<Object> args = new ArrayList<>(whereArgs.size() + 2);
        args.addAll(whereArgs);
        args.add(offset);
        args.add(limit);
        List<Map<String, Object>> rows = this.executor.executeQuery(sql, args);
        List<TreeEntity> entities = rows.stream().map(TreeServiceImpl::toEntity).collect(Collectors.toList());
        List<String> treeIds = entities.stream().map(TreeEntity::getId).collect(Collectors.toList());
        if (treeIds.size() == 0) {
            return RangedResultSet.create(entities, (int) offset, limit, (int) total);
        }
        List<Map<String, Object>> childCountRows = ExecutableSql.resolve(
                "SELECT parent_id, count(1) AS num FROM task_type WHERE parent_id IN (${treeIds}) GROUP BY parent_id",
                Collections.singletonMap("treeIds", treeIds)).executeQuery(executor);
        for (TreeEntity entity : entities) {
            for (Map<String, Object> row : childCountRows) {
                if (row.get("parent_id").toString().equals(entity.getId())) {
                    entity.setChildCount(ObjectUtils.<Number>cast(row.get("num")).longValue());
                    break;
                }
            }
        }
        return RangedResultSet.create(entities, (int) offset, limit, (int) total);
    }

    private static Optional<RangedResultSet<TreeEntity>> getTreeEntityRangedResultSet(TreeFilter filter, long offset,
            int limit, StringBuilder whereSql, List<Object> whereArgs) {
        if (filter.getIds().defined()) {
            List<String> ids = nullIf(filter.getIds().get(), Collections.emptyList());
            ids = ids.stream().filter(Entities::isId).collect(Collectors.toList());
            if (ids.isEmpty()) {
                return Optional.of(Entities.emptyRangedResultSet(offset, limit));
            }
            Sqls.andIn(whereSql, "tr.id", ids.size());
            whereArgs.addAll(ids);
        }
        if (filter.getTaskIds().defined()) {
            List<String> taskIds = nullIf(filter.getTaskIds().get(), Collections.emptyList());
            taskIds = taskIds.stream().filter(Entities::isId).collect(Collectors.toList());
            if (taskIds.isEmpty()) {
                return Optional.of(Entities.emptyRangedResultSet(offset, limit));
            }
            Sqls.andIn(whereSql, "trt.task_id", taskIds.size());
            whereArgs.addAll(taskIds);
        }
        if (filter.getNames().defined()) {
            List<String> names = filter.getNames().get();
            if (names.isEmpty()) {
                return Optional.of(Entities.emptyRangedResultSet(offset, limit));
            }
            Sqls.andLikeAny(whereSql, "tr.name", names.size());
            whereArgs.addAll(names.stream().map(Sqls::escapeLikeValue).collect(Collectors.toList()));
        }
        return Optional.empty();
    }
}
