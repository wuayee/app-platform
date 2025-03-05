/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.domain.postgresql;

import static modelengine.fitframework.inspection.Validation.notNull;
import static modelengine.fitframework.util.ObjectUtils.nullIf;

import lombok.RequiredArgsConstructor;
import modelengine.fit.jane.task.util.Entities;
import modelengine.fit.jane.task.util.OperationContext;
import modelengine.fit.jober.common.ErrorCodes;
import modelengine.fit.jober.common.exceptions.BadRequestException;
import modelengine.fit.jober.common.exceptions.ConflictException;
import modelengine.fit.jober.common.exceptions.NotFoundException;
import modelengine.fit.jober.taskcenter.domain.SourceEntity;
import modelengine.fit.jober.taskcenter.domain.TaskType;
import modelengine.fit.jober.taskcenter.service.SourceService;
import modelengine.fit.jober.taskcenter.util.DynamicSqlExecutor;
import modelengine.fit.jober.taskcenter.util.Sqls;
import modelengine.fit.jober.taskcenter.util.sql.InsertSql;
import modelengine.fit.jober.taskcenter.util.sql.SqlBuilder;
import modelengine.fit.jober.taskcenter.validation.RelationshipValidator;
import modelengine.fit.jober.taskcenter.validation.TaskTypeValidator;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.transaction.Transactional;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * 为 {@link TaskType.Repo} 提供基于 Postgresql 的实现。
 *
 * @author 梁济时
 * @since 2023-09-13
 */
@Component
@RequiredArgsConstructor
public class PostgresqlTaskTypeRepo implements TaskType.Repo {
    private static final Logger log = Logger.get(PostgresqlTaskTypeRepo.class);

    private static final String TABLE_NAME = "task_type";

    private static final String TABLE_NAME_SOURCE = "task_source";

    private final DynamicSqlExecutor executor;

    private final TaskTypeValidator validator;

    private final SourceService sourceService;

    private final RelationshipValidator relationshipValidator;

    private static List<TaskType> buildTree(List<TaskTypeRow> rows) {
        Map<String, List<TaskTypeRow>> grouped = rows.stream().collect(Collectors.groupingBy(TaskTypeRow::parentId));
        List<TaskTypeRow> roots = nullIf(grouped.get(Entities.emptyId()), Collections.emptyList());
        Queue<TaskTypeRow> queue = new LinkedList<>(roots);
        while (!queue.isEmpty()) {
            TaskTypeRow current = queue.poll();
            List<TaskTypeRow> children = nullIf(grouped.get(current.id()), Collections.emptyList());
            current.children(children);
            queue.addAll(children);
        }
        return roots.stream().map(TaskTypeRow::toTaskType).collect(Collectors.toList());
    }

    private static LocalDateTime toLocalDateTime(Object value) {
        if (value == null) {
            return null;
        }
        return ObjectUtils.<Timestamp>cast(value).toLocalDateTime();
    }

    private static String toString(Object value) {
        return ObjectUtils.cast(value);
    }

    /**
     * obtainTreeId
     *
     * @param taskId taskId
     * @return treeId
     * @deprecated 兼容性，回填 tree_id 字段
     */
    @Deprecated
    private String obtainTreeId(String taskId) {
        String sql = "SELECT tree_id FROM task_tree_task WHERE task_id = ?";
        List<Object> args = Collections.singletonList(taskId);
        String treeId = ObjectUtils.cast(this.executor.executeScalar(sql, args));
        return nullIf(treeId, taskId);
    }

    @Override
    @Transactional
    // @TenantAuthentication
    // @OperationRecord(objectId = -1, objectIdGetMethodName = "id", objectType = ObjectTypeEnum.TASK_TYPE,
    //         operate = OperateEnum.CREATED, declaration = 1)
    public TaskType create(String taskId, TaskType.Declaration declaration, OperationContext context) {
        notNull(taskId, "The owning task id of type to create cannot be null.");
        notNull(declaration, "The declaration of task type to create cannot be null.");
        notNull(context, "The operation context to create task type cannot be null.");

        log.debug("Create task type. [taskId={}, declaration={}, operator={}]", taskId, declaration,
                context.operator());

        String operator = context.operator();
        LocalDateTime operationTime = LocalDateTime.now();

        String actualTaskId = this.validator.taskId(taskId);
        relationshipValidator.validateTaskExistInTenant(actualTaskId, context.tenantId());

        TaskType type = TaskType.custom()
                .id(Entities.generateId())
                .name(declaration.name()
                        .map(this.validator::name)
                        .required(() -> new BadRequestException(ErrorCodes.TYPE_NAME_REQUIRED)))
                .parentId(declaration.parentId()
                        .map(this::checkParentIdIsExists)
                        .map(this.validator::parentId)
                        .withDefault(null))
                .creator(operator)
                .creationTime(operationTime)
                .lastModifier(operator)
                .lastModificationTime(operationTime)
                .build();

        InsertSql sql = InsertSql.custom().into(TABLE_NAME);
        sql.value("id", type.id());
        sql.value("task_id", actualTaskId);
        sql.value("name", type.name());
        sql.value("parent_id", nullIf(type.parentId(), Entities.emptyId()));
        sql.value("created_by", type.creator());
        sql.value("created_at", type.creationTime());
        sql.value("updated_by", type.lastModifier());
        sql.value("updated_at", type.lastModificationTime());
        sql.conflict("task_id", "name");

        // 待删除，兼容逻辑
        sql.value("tree_id", this.obtainTreeId(actualTaskId));

        List<Map<String, Object>> rows = sql.executeAndReturn(this.executor, "id");
        String actualId = ObjectUtils.cast(rows.get(0).get("id"));
        if (!StringUtils.equals(type.id(), actualId)) {
            log.error("A type with the same name already exists in the task. [taskId={}, name={}, typeId={}]", taskId,
                    type.name(), actualId);
            throw new ConflictException(ErrorCodes.TYPE_NAME_ALREADY_EXISTS);
        }
        return type;
    }

    private String checkParentIdIsExists(String parentId) {
        if (StringUtils.isEmpty(parentId)) {
            return parentId;
        }
        String sql = "SELECT 1 FROM task_type WHERE id = ? ";
        List<Object> args = Collections.singletonList(parentId);
        if (Objects.isNull(this.executor.executeScalar(sql, args))) {
            throw new BadRequestException(ErrorCodes.TYPE_PARENT_ID_NOT_EXISTS);
        }
        return parentId;
    }

    @Override
    @Transactional
    // @TenantAuthentication
    // @OperationRecord(objectId = 1, objectType = ObjectTypeEnum.TASK_TYPE, operate = OperateEnum.UPDATED,
    //         declaration = 2)
    public void patch(String taskId, String id, TaskType.Declaration declaration, OperationContext context) {
        notNull(taskId, "The owning task id of type to patch cannot be null.");
        notNull(id, "The id of task type to patch cannot be null.");
        notNull(declaration, "The declaration of task type to patch cannot be null.");
        notNull(context, "The operation context to patch task type cannot be null.");

        String actualTaskId = Entities.validateId(taskId, () -> new BadRequestException(ErrorCodes.TASK_ID_INVALID));
        String actualTypeId = Entities.validateId(id, () -> new BadRequestException(ErrorCodes.TYPE_ID_INVALID));
        relationshipValidator.validateTaskExistInTenant(actualTaskId, context.tenantId());
        relationshipValidator.validateTaskTypeExistInTask(actualTypeId, actualTaskId);

        String operator = context.operator();
        LocalDateTime operationTime = LocalDateTime.now();

        SqlBuilder sql = SqlBuilder.custom();
        List<Object> args = new ArrayList<>(6);
        sql.append("UPDATE ").appendIdentifier(TABLE_NAME).append(" SET updated_by = ?, updated_at = ?");
        args.addAll(Arrays.asList(operator, operationTime));
        if (declaration.name().defined()) {
            String name = this.validator.name(declaration.name().get());
            sql.append(", name = ?");
            args.add(name);
            if (declaration.sourceIds().defined()) {
                SqlBuilder sourceSql = SqlBuilder.custom();
                List<Object> sourceArgs = new ArrayList<>();
                List<String> sourceIds = declaration.sourceIds().get();
                sourceSql.append("UPDATE ").appendIdentifier(TABLE_NAME_SOURCE).append(" SET name = ? WHERE id IN (");
                sourceArgs.add(name);
                for (String sourceId : sourceIds) {
                    sourceSql.append("?, ");
                    sourceArgs.add(sourceId);
                }
                sourceSql.backspace(2);
                sourceSql.append(")");
                this.executor.executeUpdate(sourceSql.toString(), sourceArgs);
            }
        }
        if (declaration.parentId().defined()) {
            String parentId = this.validator.parentId(declaration.parentId().get());
            parentId = nullIf(parentId, Entities.emptyId());
            sql.append(", parent_id = ?");
            args.add(parentId);
        }
        sql.append(" WHERE id = ? AND task_id = ?");
        args.addAll(Arrays.asList(actualTypeId, actualTaskId));
        if (this.executor.executeUpdate(sql.toString(), args) < 1) {
            log.error("No type with the specific id in the task. [taskId={}, typeId={}]", actualTaskId, actualTypeId);
            throw new NotFoundException(ErrorCodes.TYPE_NOT_FOUND);
        }
    }

    @Override
    @Transactional
    // @TenantAuthentication
    // @OperationRecord(objectId = 1, objectType = ObjectTypeEnum.TASK_TYPE, operate = OperateEnum.DELETED)
    public void delete(String taskId, String id, OperationContext context) {
        notNull(taskId, "The owning task id of type to delete cannot be null.");
        notNull(id, "The id of task type to delete cannot be null.");
        notNull(context, "The operation context to delete task type cannot be null.");

        String actualTaskId = Entities.validateId(taskId, () -> new BadRequestException(ErrorCodes.TASK_ID_INVALID));
        String actualTypeId = Entities.validateId(id, () -> new BadRequestException(ErrorCodes.TYPE_ID_INVALID));
        relationshipValidator.validateTaskExistInTenant(actualTaskId, context.tenantId());
        relationshipValidator.validateTaskTypeExistInTask(actualTypeId, actualTaskId);

        if (this.hasChildren(id)) {
            throw new ConflictException(ErrorCodes.NODE_DELETING_HAS_NODES);
        }
        if (this.hasSources(id)) {
            throw new ConflictException(ErrorCodes.NODE_DELETING_HAS_SOURCES);
        }

        SqlBuilder sql = SqlBuilder.custom();
        sql.append("DELETE FROM ").appendIdentifier(TABLE_NAME).append(" WHERE id = ? AND task_id = ?");
        List<Object> args = Arrays.asList(actualTypeId, actualTaskId);
        if (this.executor.executeUpdate(sql.toString(), args) < 1) {
            log.error("The task type to delete does not exist. [taskId={}, typeId={}]", actualTaskId, actualTypeId);
            throw new NotFoundException(ErrorCodes.TYPE_NOT_FOUND);
        }
        this.deleteChildren(Collections.singletonList(actualTypeId));
    }

    private boolean hasChildren(String id) {
        String sql = "SELECT COUNT(1) FROM task_type WHERE parent_id = ?";
        List<Object> args = Collections.singletonList(id);
        long count = Sqls.longValue(this.executor.executeScalar(sql, args));
        return count > 0;
    }

    private boolean hasSources(String id) {
        String sql = "SELECT COUNT(1) FROM task_node_source WHERE node_id = ?";
        List<Object> args = Collections.singletonList(id);
        long count = Sqls.longValue(this.executor.executeScalar(sql, args));
        return count > 0;
    }

    @Override
    // @Transactional
    public void deleteByTasks(String taskId, OperationContext context) {
        notNull(taskId, "The owning task id of type to delete cannot be null.");
        notNull(context, "The operation context to delete task type cannot be null.");

        String actualTaskId = Entities.validateId(taskId, () -> new BadRequestException(ErrorCodes.TASK_ID_INVALID));

        String sql = "DELETE FROM \"task_type\" WHERE \"task_id\" = ? RETURNING \"id\"";
        List<Object> args = Collections.singletonList(actualTaskId);
        List<Map<String, Object>> rows = this.executor.executeQuery(sql, args);
        List<String> deletedTypeIds = rows.stream().map(
                row -> ObjectUtils.<String>cast(row.get("id"))).collect(Collectors.toList());
        if (deletedTypeIds.isEmpty()) {
            return;
        }
        deletedTypeIds.addAll(this.deleteChildren(deletedTypeIds));

        sql = "DELETE FROM \"task_node_source\" WHERE \"node_id\" IN (" + IntStream.range(0, deletedTypeIds.size())
                .mapToObj(i -> "?")
                .collect(Collectors.joining(", ")) + ") RETURNING \"source_id\", \"node_id\"";
        args = new ArrayList<>(deletedTypeIds);
        rows = this.executor.executeQuery(sql, args);
        for (Map<String, Object> row : rows) {
            String sourceId = ObjectUtils.cast(row.get("source_id"));
            String typeId = ObjectUtils.cast(row.get("node_id"));
            this.sourceService.delete(taskId, typeId, sourceId, context);
        }
    }

    @Override
    @Transactional
    public boolean exists(String taskId) {
        String sql = "SELECT COUNT(1) FROM task_type WHERE task_id = ?";
        List<Object> args = Collections.singletonList(taskId);
        long count = Sqls.longValue(this.executor.executeScalar(sql, args));
        return count > 0;
    }

    @Override
    @Transactional
    public TaskType retrieve(String taskId, String id, OperationContext context) {
        notNull(taskId, "The owning task id of type to retrieve cannot be null.");
        notNull(id, "The id of task type to retrieve cannot be null.");
        notNull(context, "The operation context to retrieve task type cannot be null.");

        String actualTaskId = Entities.validateId(taskId, () -> new BadRequestException(ErrorCodes.TASK_ID_INVALID));
        String actualTypeId = Entities.validateId(id, () -> new BadRequestException(ErrorCodes.TYPE_ID_INVALID));

        SqlBuilder sql = SqlBuilder.custom();
        buildSelectFromWhere(sql)
                .appendIdentifier("id")
                .append(" = ? AND ")
                .appendIdentifier("task_id")
                .append(" = ?");
        List<Object> args = Arrays.asList(actualTypeId, actualTaskId);
        List<Map<String, Object>> rows = this.executor.executeQuery(sql.toString(), args);
        List<TaskTypeRow> roots = rows.stream().map(TaskTypeRow::new).collect(Collectors.toList());
        if (roots.isEmpty() || Objects.equals(roots.get(0).taskId(), actualTypeId)) {
            log.error("The task type to retrieve does not exist. [taskId={}, typeId={}]", actualTaskId, actualTypeId);
            throw new NotFoundException(ErrorCodes.TYPE_NOT_FOUND);
        }

        List<TaskTypeRow> types = roots;
        List<TaskTypeRow> all = new LinkedList<>(roots);
        while (!types.isEmpty()) {
            List<String> ids = types.stream().map(TaskTypeRow::id).collect(Collectors.toList());
            List<TaskTypeRow> children = this.obtainChildren(ids);
            this.fillChildren(types, children);
            all.addAll(children);
            types = children;
        }
        this.fillSources(all, context);
        return roots.get(0).toTaskType();
    }

    @Override
    @Transactional
    public List<TaskType> list(String taskId, OperationContext context) {
        Map<String, List<TaskType>> types = this.list(Collections.singletonList(taskId), context);
        return nullIf(types.get(taskId), Collections.emptyList());
    }

    @Override
    @Transactional
    public Map<String, List<TaskType>> list(Collection<String> taskIds, OperationContext context) {
        notNull(context, "The operation context to list task types cannot be null.");

        List<String> actualTaskIds = Optional.ofNullable(taskIds)
                .map(Collection::stream)
                .orElseGet(Stream::empty)
                .map(StringUtils::trim)
                .filter(Entities::isId)
                .map(Entities::canonicalizeId)
                .collect(Collectors.toList());
        if (actualTaskIds.isEmpty()) {
            return Collections.emptyMap();
        }
        SqlBuilder sql = SqlBuilder.custom();
        buildSelectFromWhere(sql)
                .appendIdentifier("task_id")
                .append(" IN (?")
                .appendRepeatedly(", ?", actualTaskIds.size() - 1)
                .append(") ORDER BY created_at");
        List<Object> args = new ArrayList<>(actualTaskIds);
        List<Map<String, Object>> rows = this.executor.executeQuery(sql.toString(), args);
        List<TaskTypeRow> types = rows.stream().map(TaskTypeRow::new).collect(Collectors.toList());
        clearUnknownParentIds(types);
        this.fillSources(types, context);
        Map<String, List<TaskTypeRow>> grouped = types.stream().collect(Collectors.groupingBy(TaskTypeRow::taskId));
        Map<String, List<TaskType>> roots = new HashMap<>(grouped.size());
        for (Map.Entry<String, List<TaskTypeRow>> entry : grouped.entrySet()) {
            roots.put(entry.getKey(), buildTree(entry.getValue()));
        }
        return roots;
    }

    private List<TaskTypeRow> obtainChildren(List<String> parentIds) {
        SqlBuilder sql = SqlBuilder.custom();
        buildSelectFromWhere(sql)
                .appendIdentifier("parent_id")
                .append(" IN (?")
                .appendRepeatedly(", ?", parentIds.size() - 1)
                .append(")");
        List<Object> args = new ArrayList<>(parentIds);
        List<Map<String, Object>> rows = this.executor.executeQuery(sql.toString(), args);
        return rows.stream().map(TaskTypeRow::new).collect(Collectors.toList());
    }

    private void fillChildren(List<TaskTypeRow> parents, List<TaskTypeRow> children) {
        Map<String, List<TaskTypeRow>> group = children.stream().collect(Collectors.groupingBy(TaskTypeRow::parentId));
        parents.forEach(parent -> parent.children(group.get(parent.id())));
    }

    private List<String> deleteChildren(List<String> ids) {
        SqlBuilder sql = SqlBuilder.custom();
        sql.append("DELETE FROM ")
                .appendIdentifier(TABLE_NAME)
                .append(" WHERE ")
                .appendIdentifier("parent_id")
                .append(" IN (")
                .appendRepeatedly("?, ", ids.size())
                .backspace(2)
                .append(") RETURNING ")
                .appendIdentifier("id");
        List<String> allDeletedIds = new LinkedList<>();
        List<String> parentIds = ids;
        while (!CollectionUtils.isEmpty(parentIds)) {
            List<Object> args = new ArrayList<>(ids);
            List<Map<String, Object>> rows = this.executor.executeQuery(sql.toString(), args);
            List<String> deletedIds = rows.stream().map(row -> toString(row.get("id"))).collect(Collectors.toList());
            allDeletedIds.addAll(deletedIds);
            parentIds = deletedIds;
        }
        return allDeletedIds;
    }

    private void fillSources(List<TaskTypeRow> types, OperationContext context) {
        if (types.isEmpty()) {
            return;
        }
        SqlBuilder sql = SqlBuilder.custom()
                .append("SELECT ")
                .appendIdentifier("node_id")
                .append(", ")
                .appendIdentifier("source_id")
                .append(" FROM ")
                .appendIdentifier("task_node_source")
                .append(" WHERE ")
                .appendIdentifier("node_id")
                .append(" IN (?")
                .appendRepeatedly(", ?", types.size() - 1)
                .append(")");
        List<Object> args = types.stream().map(TaskTypeRow::id).collect(Collectors.toList());
        List<Map<String, Object>> rows = this.executor.executeQuery(sql.toString(), args);
        Map<String, List<String>> typeSourceIds = rows.stream()
                .collect(Collectors.groupingBy(row -> ObjectUtils.cast(row.get("node_id")),
                        Collectors.mapping(row -> ObjectUtils.cast(row.get("source_id")), Collectors.toList())));
        List<String> taskIds = types.stream().map(TaskTypeRow::taskId).distinct().collect(Collectors.toList());
        Map<String, SourceEntity> sources = this.sourceService.list(taskIds, context)
                .values()
                .stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toMap(SourceEntity::getId, Function.identity()));
        for (TaskTypeRow type : types) {
            List<String> sourceIds = typeSourceIds.get(type.id());
            if (CollectionUtils.isEmpty(sourceIds)) {
                continue;
            }
            List<SourceEntity> typeSources = sourceIds.stream()
                    .map(sources::get)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            type.sources(typeSources);
        }
    }

    private void clearUnknownParentIds(List<TaskTypeRow> rows) {
        Set<String> parentIds = rows.stream().map(TaskTypeRow::parentId).collect(Collectors.toSet());
        Set<String> ids = rows.stream().map(TaskTypeRow::id).collect(Collectors.toSet());
        Set<String> unknownParentIds = CollectionUtils.difference(parentIds, ids);
        rows.stream()
                .filter(row -> unknownParentIds.contains(row.parentId()))
                .forEach(row -> row.parentId(Entities.emptyId()));
    }

    private SqlBuilder buildSelectFromWhere(SqlBuilder sql) {
        return sql.append("SELECT ").appendIdentifier("id").append(", ").appendIdentifier("task_id").append(", ")
                .appendIdentifier("name").append(", ").appendIdentifier("parent_id").append(", ")
                .appendIdentifier("created_by").append(", ").appendIdentifier("created_at").append(", ")
                .appendIdentifier("updated_by").append(", ").appendIdentifier("updated_at").append(" FROM ")
                .appendIdentifier(TABLE_NAME).append(" WHERE ");
    }

    private static class TaskTypeRow {
        private final Map<String, Object> row;

        TaskTypeRow(Map<String, Object> row) {
            this.row = row;
        }

        String id() {
            return PostgresqlTaskTypeRepo.toString(this.row.get("id"));
        }

        String taskId() {
            return PostgresqlTaskTypeRepo.toString(this.row.get("task_id"));
        }

        String parentId() {
            return PostgresqlTaskTypeRepo.toString(this.row.get("parent_id"));
        }

        void parentId(String parentId) {
            this.row.put("parent_id", parentId);
        }

        String name() {
            return PostgresqlTaskTypeRepo.toString(this.row.get("name"));
        }

        String creator() {
            return PostgresqlTaskTypeRepo.toString(this.row.get("created_by"));
        }

        LocalDateTime creationTime() {
            return PostgresqlTaskTypeRepo.toLocalDateTime(this.row.get("created_at"));
        }

        String lastModifier() {
            return PostgresqlTaskTypeRepo.toString(this.row.get("updated_by"));
        }

        LocalDateTime lastModificationTime() {
            return PostgresqlTaskTypeRepo.toLocalDateTime(this.row.get("updated_at"));
        }

        void children(List<TaskTypeRow> children) {
            this.row.put("children", children);
        }

        List<TaskTypeRow> children() {
            List<TaskTypeRow> children = ObjectUtils.cast(this.row.get("children"));
            return nullIf(children, Collections.emptyList());
        }

        void sources(List<SourceEntity> sources) {
            this.row.put("sources", sources);
        }

        List<SourceEntity> sources() {
            List<SourceEntity> sources = ObjectUtils.cast(this.row.get("sources"));
            return nullIf(sources, Collections.emptyList());
        }

        TaskType toTaskType() {
            return TaskType.custom()
                    .id(this.id())
                    .name(this.name())
                    .parentId(Entities.ignoreEmpty(this.parentId()))
                    .children(this.children().stream().map(TaskTypeRow::toTaskType).collect(Collectors.toList()))
                    .sources(this.sources())
                    .creator(this.creator())
                    .creationTime(this.creationTime())
                    .lastModifier(this.lastModifier())
                    .lastModificationTime(this.lastModificationTime())
                    .build();
        }
    }
}
