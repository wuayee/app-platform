/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.domain.postgresql;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static modelengine.fit.jober.common.ErrorCodes.EXCEEDED_STATISTICAL_LIMIT_RESULT_SIZE;
import static modelengine.fit.jober.common.ErrorCodes.ORDER_BY_PROPERTY_NAME_NOT_SUPPORT;
import static modelengine.fit.jober.common.ErrorCodes.PROPERTY_NOT_EXIST_IN_ORDER_BY_PARAM;
import static modelengine.fit.jober.taskcenter.util.Sqls.longValue;
import static modelengine.fitframework.util.ObjectUtils.cast;
import static modelengine.fitframework.util.ObjectUtils.mapIfNotNull;
import static modelengine.fitframework.util.ObjectUtils.nullIf;

import modelengine.fit.jane.task.domain.PropertyDataType;
import modelengine.fit.jane.task.domain.TaskProperty;
import modelengine.fit.jane.task.util.Entities;
import modelengine.fit.jane.task.util.OperationContext;
import modelengine.fit.jane.task.util.PagedResultSet;
import modelengine.fit.jane.task.util.Pagination;
import modelengine.fit.jane.task.util.PaginationResult;
import modelengine.fit.jane.task.util.UndefinableValue;
import modelengine.fit.jober.common.ErrorCodes;
import modelengine.fit.jober.common.ServerInternalException;
import modelengine.fit.jober.common.aop.ObjectTypeEnum;
import modelengine.fit.jober.common.aop.OperateEnum;
import modelengine.fit.jober.common.aop.OperationRecord;
import modelengine.fit.jober.common.exceptions.BadRequestException;
import modelengine.fit.jober.common.exceptions.ConflictException;
import modelengine.fit.jober.common.exceptions.GoneException;
import modelengine.fit.jober.common.exceptions.NotFoundException;
import modelengine.fit.jober.taskcenter.domain.HierarchicalTaskInstance;
import modelengine.fit.jober.taskcenter.domain.SourceEntity;
import modelengine.fit.jober.taskcenter.domain.TaskEntity;
import modelengine.fit.jober.taskcenter.domain.TaskInstance;
import modelengine.fit.jober.taskcenter.domain.TaskType;
import modelengine.fit.jober.taskcenter.domain.ViewMode;
import modelengine.fit.jober.taskcenter.domain.util.AsynchronousRunner;
import modelengine.fit.jober.taskcenter.domain.util.CategoryAcceptor;
import modelengine.fit.jober.taskcenter.domain.util.CategoryChangedNotifier;
import modelengine.fit.jober.taskcenter.domain.util.IndexValueRow;
import modelengine.fit.jober.taskcenter.domain.util.InstanceEventNotifier;
import modelengine.fit.jober.taskcenter.domain.util.InstancesChangedNotifier;
import modelengine.fit.jober.taskcenter.domain.util.ListValue;
import modelengine.fit.jober.taskcenter.domain.util.PrimaryValue;
import modelengine.fit.jober.taskcenter.domain.util.TaskInstanceEventNotifier;
import modelengine.fit.jober.taskcenter.domain.util.TaskInstanceRow;
import modelengine.fit.jober.taskcenter.domain.util.TaskInstanceView;
import modelengine.fit.jober.taskcenter.domain.util.support.DefaultTaskInstanceView;
import modelengine.fit.jober.taskcenter.event.TaskInstanceCreatingEvent;
import modelengine.fit.jober.taskcenter.event.TaskInstanceModifyingEvent;
import modelengine.fit.jober.taskcenter.service.CategoryService;
import modelengine.fit.jober.taskcenter.service.TagService;
import modelengine.fit.jober.taskcenter.service.impl.RefreshInTimeTaskInstanceRepo;
import modelengine.fit.jober.taskcenter.util.DynamicSqlExecutor;
import modelengine.fit.jober.taskcenter.util.Enums;
import modelengine.fit.jober.taskcenter.util.ExecutableSql;
import modelengine.fit.jober.taskcenter.util.sql.ColumnRef;
import modelengine.fit.jober.taskcenter.util.sql.Condition;
import modelengine.fit.jober.taskcenter.util.sql.OrderBy;
import modelengine.fit.jober.taskcenter.util.sql.SqlBuilder;
import modelengine.fit.jober.taskcenter.util.sql.UpdateSql;
import modelengine.fit.jober.taskcenter.validation.InstanceValidator;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.broker.client.BrokerClient;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.plugin.Plugin;
import modelengine.fitframework.transaction.Transactional;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.MapUtils;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * 为 {@link TaskInstance.Repo} 提供基于 {@code Postgresql} 的实现。
 *
 * @author 梁济时
 * @since 2024-01-10
 */
@Component
public class PostgresqlTaskInstanceRepo implements TaskInstance.Repo {
    private static final Logger log = Logger.get(PostgresqlTaskInstanceRepo.class);

    private final DynamicSqlExecutor executor;

    private final InstanceValidator validator;

    private final Plugin plugin;

    private final TagService tagService;

    private final CategoryService categoryService;

    private final BrokerClient brokerClient;

    private final AsynchronousRunner runner;

    public PostgresqlTaskInstanceRepo(DynamicSqlExecutor executor, InstanceValidator validator, Plugin plugin,
            TagService tagService, CategoryService categoryService, BrokerClient brokerClient,
            AsynchronousRunner runner) {
        this.executor = executor;
        this.validator = validator;
        this.plugin = plugin;
        this.tagService = tagService;
        this.categoryService = categoryService;
        this.brokerClient = brokerClient;
        this.runner = runner;
    }

    @Override
    @Transactional
    @OperationRecord(objectId = -1, objectIdGetMethodName = "id", objectType = ObjectTypeEnum.INSTANCE,
            operate = OperateEnum.CREATED, declaration = 1)
    public TaskInstance create(TaskEntity task, TaskInstance.Declaration declaration, OperationContext context) {
        ownerListText(declaration, task);
        TaskInstanceCreatingEvent event = new TaskInstanceCreatingEvent(this, task, Entities.generateId(), context);
        RefreshInTimeTaskInstanceRepo repo = new RefreshInTimeTaskInstanceRepo(this.brokerClient, task);
        if (repo.processable()) {
            return repo.create(declaration, context);
        }
        event.declaration(declaration);
        this.plugin.publisherOfEvents().publishEvent(event);
        TaskInstance instance = this.new Creator(task, event.instanceId(), event.declaration(), context).create();
        this.runner.run(TaskInstanceEventNotifier.custom(this.plugin, task, context).noticeNew(instance));
        return instance;
    }

    @Override
    @Transactional
    @OperationRecord(objectId = 1, objectType = ObjectTypeEnum.INSTANCE, operate = OperateEnum.UPDATED, declaration = 2)
    public void patch(TaskEntity task, String id, TaskInstance.Declaration declaration, OperationContext context) {
        ownerListText(declaration, task);
        RefreshInTimeTaskInstanceRepo repo = new RefreshInTimeTaskInstanceRepo(this.brokerClient, task);
        if (repo.processable()) {
            repo.patch(id, declaration, context);
            return;
        }
        TaskInstance oldInstance = this.retrieve(task, id, false, context);
        TaskInstanceModifyingEvent event = new TaskInstanceModifyingEvent(this, task, context, oldInstance);
        event.declaration(declaration);
        this.plugin.publisherOfEvents().publishEvent(event);
        TaskInstance newInstance = this.new Patcher(task, oldInstance, event.declaration(), context).patch();
        this.runner.run(
                InstancesChangedNotifier.of(this.brokerClient, task).notice(newInstance, oldInstance, context),
                CategoryChangedNotifier.of(this.brokerClient, task, this.categoryService)
                        .notice(newInstance, oldInstance.categories(), newInstance.categories(), context),
                InstanceEventNotifier.custom(task, this.plugin, context).noticeOld(oldInstance).noticeNew(newInstance)
        );
    }

    private void ownerListText(TaskInstance.Declaration declaration, TaskEntity task) {
        modelengine.fit.jane.task.domain.TaskProperty taskProperty = task.getPropertyByName("owner");
        if (taskProperty != null) {
            if (taskProperty.dataType().listable()) {
                Object owner = declaration.info().get().get("owner");
                if (owner != null) {
                    String[] ownerList = owner.toString().split(",");
                    declaration.info().get().put("owner", ownerList);
                }
            }
        }
    }

    @Override
    @Transactional
    @OperationRecord(objectId = 1, objectType = ObjectTypeEnum.INSTANCE, operate = OperateEnum.DELETED)
    public void delete(TaskEntity task, String id, OperationContext context) {
        RefreshInTimeTaskInstanceRepo repo = new RefreshInTimeTaskInstanceRepo(this.brokerClient, task);
        if (repo.processable()) {
            repo.delete(id, context);
            return;
        }
        TaskInstance instance = this.retrieve(task, id, false, context);
        TaskInstanceRow.move(this.executor, task, instance.id(), TaskInstanceRow.TABLE, TaskInstanceRow.TABLE_DELETED);
        this.runner.run(InstanceEventNotifier.custom(task, this.plugin, context).noticeOld(instance));
        // 删除时不删除列表属性数据、标签、类目等关联信息，因为删除只是将其已入到了 deleted 表中，后续可能需要恢复。
    }

    @Override
    @Transactional
    public void deleteBySource(TaskEntity task, String sourceId, OperationContext context) {
        String actualSourceId = Entities.validateId(sourceId,
                () -> new BadRequestException(ErrorCodes.SOURCE_SOURCE_INVALID));
        SourceEntity source = SourceEntity.lookup(task.getTypes(), actualSourceId);
        if (source == null) {
            throw new BadRequestException(ErrorCodes.INSTANCE_SOURCE_NOT_FOUND);
        }
        List<String> ids = TaskInstanceRow.selectIdsBySource(this.executor, source.getId());
        TaskInstanceRow.moveBySource(this.executor, source.getId());
        TaskInstanceRow.deleteBySource(this.executor, source.getId());
        int limit = 50;
        int offset = 0;
        while (offset < ids.size()) {
            int to = Math.min(offset + limit, ids.size());
            List<String> page = ids.subList(offset, to);
            List<TaskInstanceRow> rows = TaskInstanceRow.selectDeleted(this.executor, task, page);
            this.fillAdditions(rows, context);
            TaskInstance[] instances = toDomainObjects(task, rows).toArray(new TaskInstance[0]);
            this.runner.run(InstanceEventNotifier.custom(task, this.plugin, context).noticeOld(instances));
            offset = to;
        }
    }

    @Override
    @Transactional
    public TaskInstance retrieve(TaskEntity task, String id, boolean isDeleted, OperationContext context) {
        RefreshInTimeTaskInstanceRepo repo = new RefreshInTimeTaskInstanceRepo(this.brokerClient, task);
        if (repo.processable()) {
            return repo.retrieve(id, context);
        }
        String actualId = Entities.validateId(id, () -> new BadRequestException(ErrorCodes.INSTANCE_ID_INVALID));
        String table = isDeleted ? TaskInstanceRow.TABLE_DELETED : TaskInstanceRow.TABLE;
        TaskInstanceRow row = TaskInstanceRow.select(this.executor, task, actualId, table);
        if (row == null) {
            if (!isDeleted && TaskInstanceRow.exist(this.executor, task, actualId, TaskInstanceRow.TABLE_DELETED)) {
                log.error("The task instance has been deleted. [task={}, instance={}]", task.getName(), id);
                throw new GoneException(ErrorCodes.INSTANCE_DELETED);
            }
            log.error("The task instance does not exist. [task={}, instance={}, deleted={}]",
                    task.getName(), id, isDeleted);
            throw new NotFoundException(ErrorCodes.INSTANCE_NOT_FOUND, actualId, task.getId());
        }
        row.tags(this.tagService.list(TaskInstanceRow.OBJECT_TYPE, actualId, context));
        row.categories(this.categoryService.listUsages(TaskInstanceRow.OBJECT_TYPE, actualId, context));
        return toDomainObject(task, row);
    }

    @Override
    @Transactional
    public PagedResultSet<TaskInstance> list(TaskEntity task, TaskInstance.Filter filter, Pagination pagination,
            List<OrderBy> orderBys, ViewMode viewMode, OperationContext context) {
        RefreshInTimeTaskInstanceRepo repo = new RefreshInTimeTaskInstanceRepo(this.brokerClient, task);
        if (repo.processable()) {
            return repo.list(filter, pagination.offset(), pagination.limit(), context);
        }
        InstanceViewer viewer = factoryOfViewer(nullIf(viewMode, ViewMode.LIST)).create(task, filter, context,
                orderBys);
        return viewer.view(pagination);
    }

    @Override
    public String getMetaId(String id) {
        String sql = "select task_id from task_instance_wide where id = ?";
        List<Object> args = new ArrayList<>();
        args.add(id);
        return ObjectUtils.cast(this.executor.executeScalar(sql, args));
    }

    @Override
    public Map<String, Long> statistics(TaskEntity task, TaskInstance.Filter filter, String column,
            OperationContext context) {
        return new StatisticsViewer(task, filter, context).view(column);
    }

    @Override
    @Transactional
    public void recover(TaskEntity task, String id, OperationContext context) {
        String actualId = Entities.validateId(id, () -> new BadRequestException(ErrorCodes.INSTANCE_ID_INVALID));
        TaskInstanceRow row = TaskInstanceRow.select(this.executor, task, actualId, TaskInstanceRow.TABLE_DELETED);
        if (row == null) {
            log.error("The task instance to recover does not exist. [task={}, instance={}]",
                    task.getName(), id);
            throw new NotFoundException(ErrorCodes.INSTANCE_NOT_FOUND);
        }
        this.fillAdditions(Collections.singletonList(row), context);
        TaskInstance instance = toDomainObject(task, row);
        TaskInstanceRow.move(this.executor, task, instance.id(), TaskInstanceRow.TABLE_DELETED, TaskInstanceRow.TABLE);
        this.runner.run(InstanceEventNotifier.custom(task, this.plugin, context).noticeNew(instance));
    }

    private static void validateTypeAndSource(TaskEntity task, TaskInstanceRow row) {
        if (Entities.isEmpty(row.typeId())) {
            if (Entities.isEmpty(row.sourceId())) {
                log.error("The task type is required when no source specified.");
                throw new BadRequestException(ErrorCodes.INSTANCE_TYPE_REQUIRED);
            }
            TaskType type = TaskType.lookup(task.getTypes(), current -> current.sources().stream()
                    .anyMatch(source -> Entities.match(source.getId(), row.sourceId())));
            if (type == null) {
                log.error("The source of task instance does not exist. [sourceId={}]", row.sourceId());
                throw new BadRequestException(ErrorCodes.INSTANCE_SOURCE_NOT_FOUND);
            }
            row.typeId(type.id());
        } else {
            TaskType type = TaskType.lookup(task.getTypes(), row.typeId());
            if (type == null) {
                log.error("The type of task instance does not exist. [typeId={}]", row.typeId());
                throw new BadRequestException(ErrorCodes.INSTANCE_TYPE_NOT_FOUND);
            }
            if (!Entities.isEmpty(row.sourceId()) && type.sources().stream()
                    .noneMatch(source -> Entities.match(source.getId(), row.sourceId()))) {
                log.error("The source of task instance not found in type. [sourceId={}]", row.sourceId());
                throw new BadRequestException(ErrorCodes.INSTANCE_SOURCE_NOT_FOUND);
            }
        }
    }

    /**
     * 标准化用户的自定义属性内容。
     *
     * @param task 表示任务实例所属任务定义的 {@link TaskEntity}。
     * @param info 表示任务实例的自定义属性内容的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     * @param isDefined 若为 {@code true}，则只包含 {@code info} 中定义了的属性，否则包含任务定义中所有属性的内容。
     * @return 表示标准化后的自定义属性内容的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    private static Map<String, Object> canonicalizeInfo(TaskEntity task, Map<String, Object> info, boolean isDefined) {
        Map<String, Object> canonical = new LinkedHashMap<>(task.getProperties().size());
        for (TaskProperty property : task.getProperties()) {
            String key = property.name();
            if (isDefined && !info.containsKey(key)) {
                continue;
            }
            Object value = info.get(key);
            if (property.required() && value == null) {
                log.error("The property is required but value not supplied. [task={}, property={}]",
                        task.getName(), property.name());
                throw new BadRequestException(ErrorCodes.INSTANCE_PROPERTY_REQUIRED, property.name());
            }
            if (property.dataType() == PropertyDataType.TEXT) {
                if (Objects.nonNull(value) && !(value instanceof String)) {
                    value = value.toString();
                }
            }
            value = property.dataType().fromExternal(value);
            canonical.put(property.name(), value);
        }
        return canonical;
    }

    private static TaskInstance toDomainObject(TaskEntity task, TaskInstanceRow row) {
        return toDomainObjects(task, Collections.singletonList(row)).get(0);
    }

    private static List<TaskInstance> toDomainObjects(TaskEntity task, List<TaskInstanceRow> rows) {
        Map<String, TaskType> types = new HashMap<>();
        Map<String, SourceEntity> sources = new HashMap<>();
        TaskType.traverse(task.getTypes(), type -> {
            types.put(type.id(), type);
            type.sources().forEach(source -> sources.put(source.getId(), source));
        });
        List<TaskInstance> instances = new ArrayList<>(rows.size());
        for (TaskInstanceRow row : rows) {
            TaskInstance instance = TaskInstance.custom()
                    .id(row.id())
                    .task(task)
                    .type(types.get(row.typeId()))
                    .source(sources.get(row.sourceId()))
                    .info(row.info())
                    .tags(row.tags())
                    .categories(row.categories())
                    .build();
            instances.add(instance);
        }
        return instances;
    }

    /**
     * 为操作提供基类。
     *
     * @author 梁济时
     * @since 2024-01-11
     */
    private static class Operation {
        /**
         * 表示任务实体的{@link TaskEntity}
         */
        protected final TaskEntity task;

        private List<TaskProperty> indexedProperties;

        Operation(TaskEntity task) {
            this.task = task;
        }

        protected List<TaskProperty> getIndexedProperties() {
            if (this.indexedProperties == null) {
                this.indexedProperties = this.task.getProperties().stream()
                        .filter(property -> this.task.isPropertyIndexed(property.name()))
                        .collect(toList());
            }
            return this.indexedProperties;
        }
    }

    private class Creator extends Operation {
        private final String instanceId;

        private final TaskInstance.Declaration declaration;

        private final OperationContext context;

        Creator(TaskEntity task, String instanceId, TaskInstance.Declaration declaration, OperationContext context) {
            super(task);
            this.instanceId = instanceId;
            this.declaration = declaration;
            this.context = context;
        }

        private void acceptBasicProperties(TaskInstanceRow row) {
            String typeId = UndefinableValue.withDefault(this.declaration.typeId(), Entities.emptyId());
            typeId = validator.typeId(typeId);
            row.typeId(typeId);
            String sourceId = UndefinableValue.withDefault(this.declaration.sourceId(), Entities.emptyId());
            sourceId = validator.sourceId(sourceId);
            row.sourceId(sourceId);
            validateTypeAndSource(this.task, row);
        }

        private void acceptCustomProperties(TaskInstanceRow row) {
            Map<String, Object> info = this.declaration.info()
                    .required(() -> new BadRequestException(ErrorCodes.INSTANCE_INFO_REQUIRED));
            info = validator.info(info);
            info = canonicalizeInfo(this.task, info, false);
            row.info(info);
        }

        private void checkDuplicate(TaskInstanceRow row) {
            PrimaryValue primary = this.task.computePrimaryValue(row.info());
            if (TaskInstanceRow.select(executor, this.task, primary) != null) {
                log.error("A task instance with the same primary value already exists.");
                throw new ConflictException(ErrorCodes.INSTANCE_EXISTS);
            }
            if (TaskInstanceRow.selectHistory(executor, this.task, primary) != null) {
                log.error("A task instance with the same primary value already deleted.");
                throw new GoneException(ErrorCodes.INSTANCE_DELETED);
            }
        }

        private void insertIndexes(TaskInstance instance) {
            Map<String, List<IndexValueRow>> groups = new HashMap<>();
            for (TaskProperty property : this.getIndexedProperties()) {
                PropertyDataType dataType;
                List<?> values;
                if (property.dataType().listable()) {
                    values = nullIf(cast(instance.info().get(property.name())), Collections.emptyList());
                    dataType = property.dataType().elementType();
                } else {
                    values = Collections.singletonList(instance.info().get(property.name()));
                    dataType = property.dataType();
                }
                groups.computeIfAbsent(dataType.tableOfIndex(), key -> new LinkedList<>()).addAll(values.stream()
                        .map(value -> IndexValueRow.create(instance.id(), property.id(), value))
                        .collect(toList()));
            }
            groups.forEach((table, rows) -> IndexValueRow.insert(executor, table, rows));
        }

        TaskInstance create() {
            TaskInstanceRow row = new TaskInstanceRow();
            row.id(this.instanceId);
            row.taskId(this.task.getId());
            this.acceptBasicProperties(row);
            this.acceptCustomProperties(row);
            row.tags(UndefinableValue.withDefault(this.declaration.tags(), Collections.emptyList()));
            row.categories(CategoryAcceptor.of(this.task).obtain(row.info()));
            this.checkDuplicate(row);
            row.insert(executor, this.task);
            tagService.save(TaskInstanceRow.OBJECT_TYPE, row.id(), row.tags(), this.context);
            categoryService.saveUsages(TaskInstanceRow.OBJECT_TYPE, row.id(), row.categories(), this.context);
            TaskInstance instance = toDomainObject(this.task, row);
            this.insertIndexes(instance);
            return instance;
        }
    }

    private class Patcher extends Operation {
        private final TaskInstance instance;

        private final TaskInstance.Declaration declaration;

        private final OperationContext context;

        private Patcher(TaskEntity task, TaskInstance instance, TaskInstance.Declaration declaration,
                OperationContext context) {
            super(task);
            this.instance = instance;
            this.declaration = declaration;
            this.context = context;
        }

        private void acceptBasicProperties(TaskInstanceRow row, Map<String, Object> modifications) {
            this.declaration.typeId().ifDefined(value -> {
                String typeId = validator.typeId(value);
                modifications.put(TaskInstanceRow.COLUMN_TYPE_ID, typeId);
                row.typeId(typeId);
            });
            this.declaration.sourceId().ifDefined(value -> {
                String sourceId = validator.sourceId(value);
                modifications.put(TaskInstanceRow.COLUMN_SOURCE_ID, sourceId);
                row.sourceId(sourceId);
            });
            validateTypeAndSource(task, row);
        }

        private void checkDuplicate(Map<String, Object> info) {
            PrimaryValue primary = this.task.computePrimaryValue(info);
            TaskInstanceRow existing = TaskInstanceRow.select(executor, this.task, primary);
            if (existing != null && !Entities.match(existing.id(), this.instance.id())) {
                log.error("A task instance with the same primary already exists. [instanceId={}]", existing.id());
                throw new ConflictException(ErrorCodes.INSTANCE_EXISTS);
            }
            existing = TaskInstanceRow.selectHistory(executor, this.task, primary);
            if (existing != null) {
                log.error("A task instance with the same primary already deleted. [instanceId={}]", existing.id());
                throw new GoneException(ErrorCodes.INSTANCE_DELETED);
            }
        }

        private void updateIndexes(TaskInstance newInstance) {
            // 在保存索引时，所有已经建立了索引的数据列，在用以存储对应数据类型的索引值的数据表中必然有其对应的值。因为：
            // 1. 如果先建立了索引，之后创建任务实例，在创建时会保存索引值；
            // 2. 如果先有任务实例，再建立索引，则在建立索引时会将所有已有任务实例的索引值补齐；
            // 因此，以此为前提，此处直接对所有值发生变化的且用作索引的属性的索引值进行更新即可。
            Map<String, Object> diffs = this.instance.diff(newInstance);
            Map<String, Object> diffsIndex = new HashMap<>();
            for (Map.Entry<String, Object> entry : diffs.entrySet()) {
                if (this.task.isPropertyIndexed(entry.getKey())) {
                    diffsIndex.put(entry.getKey(), entry.getValue());
                }
            }
            if (diffsIndex.isEmpty()) {
                return;
            }
            Map<String, Map<TaskProperty, Object>> tables = new HashMap<>();
            for (Map.Entry<String, Object> entry : diffsIndex.entrySet()) {
                TaskProperty property = this.task.getPropertyByName(entry.getKey());
                String table = property.dataType().tableOfIndex();
                Map<TaskProperty, Object> values = tables.computeIfAbsent(table, key -> new HashMap<>());
                values.put(property, entry.getValue());
            }
            tables.forEach(this::updateIndexes);
        }

        private void updateIndexes(String table, Map<TaskProperty, Object> diffs) {
            Map<String, List<?>> actual = new HashMap<>(diffs.size());
            for (Map.Entry<TaskProperty, Object> entry : diffs.entrySet()) {
                TaskProperty property = entry.getKey();
                Object value = property.dataType().toPersistence(entry.getValue());
                if (property.dataType().listable()) {
                    actual.put(property.id(), cast(value));
                } else {
                    actual.put(property.id(), Collections.singletonList(value));
                }
            }
            List<IndexValueRow> rows = IndexValueRow.select(executor, table, this.instance.id(), actual.keySet());
            Map<String, List<IndexValueRow>> current = rows.stream().collect(groupingBy(IndexValueRow::propertyId));
            List<IndexValueRow> insertingValueRows = new LinkedList<>();
            List<IndexValueRow> updatingValueRows = new LinkedList<>();
            List<String> deletingValueRowIds = new LinkedList<>();
            for (Map.Entry<String, List<?>> entry : actual.entrySet()) {
                String propertyId = entry.getKey();
                List<?> newValues = entry.getValue();
                List<IndexValueRow> oldRows = nullIf(current.get(propertyId), Collections.emptyList());
                int oldIndex = 0;
                int newIndex = 0;
                while (oldIndex < oldRows.size() && newIndex < newValues.size()) {
                    IndexValueRow oldRow = oldRows.get(oldIndex++);
                    oldRow.value(newValues.get(newIndex++));
                    updatingValueRows.add(oldRow);
                }
                deletingValueRowIds.addAll(oldRows.subList(oldIndex, oldRows.size()).stream()
                        .map(IndexValueRow::id).collect(toList()));
                insertingValueRows.addAll(newValues.subList(newIndex, newValues.size()).stream()
                        .map(value -> IndexValueRow.create(this.instance.id(), propertyId, value)).collect(toList()));
            }
            IndexValueRow.update(executor, table, updatingValueRows);
            IndexValueRow.insert(executor, table, insertingValueRows);
            IndexValueRow.delete(executor, table, deletingValueRowIds);
        }

        private void save(TaskInstanceRow row) {
            Map<String, Object> modifications = new LinkedHashMap<>();
            this.acceptBasicProperties(row, modifications);
            List<TaskProperty> listableProperties = new LinkedList<>();
            this.declaration.info().ifDefined(info -> {
                Map<String, Object> actual = validator.info(info);
                if (MapUtils.isEmpty(actual)) {
                    return;
                }
                actual = canonicalizeInfo(this.task, actual, true);
                row.info(new HashMap<>(row.info()));
                for (Map.Entry<String, Object> entry : actual.entrySet()) {
                    TaskProperty property = this.task.getPropertyByName(entry.getKey());
                    if (property.dataType().listable()) {
                        listableProperties.add(property);
                    } else {
                        modifications.put(property.column(), entry.getValue());
                    }
                    row.info().put(property.name(), entry.getValue());
                }
                this.checkDuplicate(row.info());
                List<String> categories = CategoryAcceptor.of(task).obtain(row.info());
                row.categories(categories);
            });
            if (!modifications.isEmpty()) {
                UpdateSql sql = UpdateSql.custom().table(TaskInstanceRow.TABLE);
                for (Map.Entry<String, Object> entry : modifications.entrySet()) {
                    sql.set(entry.getKey(), entry.getValue());
                }
                sql.where(Condition.expectEqual(TaskInstanceRow.COLUMN_ID, this.instance.id()));
                int affectedRows = sql.execute(executor);
                if (affectedRows < 1) {
                    log.error("No row affected when update task instance into database.");
                    throw new ServerInternalException("Failed to update task instance into database.");
                }
            }
            this.saveListableProperties(row, listableProperties);
        }

        private void saveListableProperties(TaskInstanceRow row, List<TaskProperty> properties) {
            Map<String, List<TaskProperty>> tableGrouped = properties.stream().collect(groupingBy(
                    property -> property.dataType().tableOfList(), LinkedHashMap::new, toList()));
            tableGrouped.forEach((key, value) -> this.saveListableProperties(row, key, value));
        }

        private void saveListableProperties(TaskInstanceRow row, String table, List<TaskProperty> properties) {
            ListValue.deleteByInstance(executor, table, this.instance.id());
            List<ListValue> listValues = properties.stream()
                    .map(property -> this.createNewListValues(row, property))
                    .flatMap(Collection::stream)
                    .collect(toList());
            ListValue.insert(executor, table, listValues);
        }

        private List<ListValue> createNewListValues(TaskInstanceRow row, TaskProperty property) {
            List<Object> values = cast(row.info().get(property.name()));
            if (CollectionUtils.isEmpty(values)) {
                return Collections.emptyList();
            }
            List<ListValue> listValues = new ArrayList<>(values.size());
            int index = 0;
            while (index < values.size()) {
                Object value = values.get(index++);
                ListValue listValue = new ListValue();
                listValue.id(Entities.generateId());
                listValue.instanceId(this.instance.id());
                listValue.propertyId(property.id());
                listValue.index(index);
                listValue.value(value);
                listValues.add(listValue);
            }
            return listValues;
        }

        TaskInstance patch() {
            TaskInstanceRow row = this.createRow();
            this.save(row);
            this.declaration.tags().ifDefined(value -> {
                row.tags(Optional.ofNullable(value).map(Collection::stream).orElseGet(Stream::empty)
                        .map(StringUtils::trim).filter(StringUtils::isNotEmpty).collect(toList()));
                tagService.save(TaskInstanceRow.OBJECT_TYPE, this.instance.id(), row.tags(), this.context);
            });
            if (!Entities.equals(this.instance.categories(), row.categories())) {
                categoryService.saveUsages(TaskInstanceRow.OBJECT_TYPE, row.id(), row.categories(), this.context);
            }
            TaskInstance newInstance = toDomainObject(this.task, row);
            this.updateIndexes(newInstance);
            return newInstance;
        }

        private TaskInstanceRow createRow() {
            TaskInstanceRow row = new TaskInstanceRow();
            row.id(this.instance.id());
            row.taskId(this.instance.task().getId());
            row.typeId(this.instance.type().id());
            row.sourceId(mapIfNotNull(this.instance.source(), SourceEntity::getId));
            row.info(this.instance.info());
            row.tags(this.instance.tags());
            row.categories(this.instance.categories());
            return row;
        }
    }

    private abstract class Viewer {
        /**
         * with table
         */
        protected static final String WITH_TABLE = "w_ins";

        /**
         * 表示任务实体的{@link TaskEntity}
         */
        protected final TaskEntity task;

        /**
         * 操作上下文
         */
        protected final OperationContext context;
        private final TaskInstance.Filter filter;

        private List<TaskProperty> listableProperties;

        Viewer(TaskEntity task, TaskInstance.Filter filter, OperationContext context) {
            this.task = task;
            this.filter = filter;
            this.context = context;
        }

        /**
         * 填充SQL语句
         *
         * @param sql 表示SQL语句构造器的{@link SqlBuilder}
         * @param args 待填充的参数的{@link List}{@code <}{@link Object}{@code >}
         * @return SQL语句构造器
         */
        protected SqlBuilder fillWithSql(SqlBuilder sql, List<Object> args) {
            TaskInstanceView view = new DefaultTaskInstanceView(categoryService, tagService, this.task, this.filter,
                    this.context);
            ExecutableSql executableSql = view.sql();
            sql.append("WITH ").appendIdentifier(WITH_TABLE).append(" AS (").append(executableSql.sql()).append(") ");
            args.addAll(executableSql.args());
            return sql;
        }

        /**
         * 返回可列的属性
         *
         * @return 返回可列的属性
         */
        protected List<TaskProperty> listableProperties() {
            if (this.listableProperties == null) {
                this.listableProperties = this.task.getProperties().stream()
                        .filter(property -> property.dataType().listable())
                        .collect(toList());
            }
            return this.listableProperties;
        }
    }

    /**
     * 数据查看类
     */
    public class StatisticsViewer extends Viewer {
        StatisticsViewer(TaskEntity task, TaskInstance.Filter filter, OperationContext context) {
            super(task, filter, context);
        }

        /**
         * 查看groupBy列
         *
         * @param groupByColumn 需要groupBy的列
         * @return 返回sql执行的返回值
         */
        public Map<String, Long> view(String groupByColumn) {
            SqlBuilder sql = SqlBuilder.custom();
            List<Object> args = new LinkedList<>();
            this.fillWithSql(sql, args);
            String withSql = sql.toString();
            String countSql = SqlBuilder.custom()
                    .append(withSql)
                    .append("SELECT count(DISTINCT(info_")
                    .append(groupByColumn)
                    .append(")) FROM ")
                    .appendIdentifier(WITH_TABLE)
                    .toString();
            sql = SqlBuilder.custom()
                    .append(withSql)
                    .append("SELECT info_")
                    .append(groupByColumn)
                    .append(", COUNT(1) FROM ")
                    .appendIdentifier(WITH_TABLE)
                    .append(" GROUP BY info_")
                    .append(groupByColumn);
            long count = longValue(executor.executeScalar(countSql, args));
            if (count > 200L) {
                log.warn("Exceeded statistical limit result size.");
                throw new BadRequestException(EXCEEDED_STATISTICAL_LIMIT_RESULT_SIZE);
            }
            Map<String, Long> resultMap = new HashMap<>();
            List<Map<String, Object>> result = executor.executeQuery(sql.toString(), args);
            result.forEach(
                    row -> resultMap.put(ObjectUtils.cast(row.get("info_" + groupByColumn)),
                            longValue(row.get("count"))));
            return resultMap;
        }
    }

    private abstract class InstanceViewer extends Viewer {
        private final List<OrderBy> orderBys;

        InstanceViewer(TaskEntity task, TaskInstance.Filter filter, OperationContext context, List<OrderBy> orderBys) {
            super(task, filter, context);
            this.orderBys = orderBys;
        }

        /**
         * 查询任务实例
         *
         * @param baseSql 表示基本SQL的{@link String}
         * @param baseArgs 表示基本的SQL参数的{@link List}{@code <}{@link Object}{@code >}
         * @param pagination 表示分页的{@link Pagination}
         * @return 查询得到的任务实例列表
         */
        protected List<TaskInstance> list(String baseSql, List<Object> baseArgs, Pagination pagination) {
            if (pagination.limit() < 1) {
                return Collections.emptyList();
            }
            SqlBuilder sql = SqlBuilder.custom().append(baseSql);
            if (!this.orderBys.isEmpty()) {
                sql.append(" ORDER BY ");
                for (OrderBy orderBy : this.orderBys) {
                    ColumnRef column = ColumnRef.of(WITH_TABLE, this.getOrderByColumnName(orderBy.property()));
                    sql.append(column).append(' ').append(orderBy.order()).append(", ");
                }
                sql.backspace(2);
            }
            List<Object> args = new LinkedList<>(baseArgs);
            sql.append(" OFFSET ? LIMIT ?");
            args.addAll(Arrays.asList(pagination.offset(), pagination.limit()));
            List<Map<String, Object>> rows = executor.executeQuery(sql.toString(), args);
            List<TaskInstanceRow> instances = TaskInstanceRow.convert(this.task, rows);
            this.fillListableProperties(instances);
            fillAdditions(instances, this.context);
            return toDomainObjects(this.task, instances);
        }

        private String getOrderByColumnName(String orderBy) {
            final String infoPrefix = "info.";
            if (!StringUtils.startsWithIgnoreCase(orderBy, infoPrefix)) {
                throw new BadRequestException(ORDER_BY_PROPERTY_NAME_NOT_SUPPORT);
            }
            TaskProperty property = this.task.getPropertyByName(orderBy.substring(infoPrefix.length()));
            if (property == null) {
                throw new BadRequestException(PROPERTY_NOT_EXIST_IN_ORDER_BY_PARAM);
            }
            return TaskInstanceRow.INFO_PREFIX + property.name();
        }

        /**
         * 填充可列的属性
         *
         * @param instances 表示任务实例数据行列表的{@link List}{@code <}{@link TaskInstanceRow}{@code >}
         */
        public void fillListableProperties(List<TaskInstanceRow> instances) {
            if (CollectionUtils.isEmpty(this.listableProperties()) || CollectionUtils.isEmpty(instances)) {
                return;
            }
            List<String> instanceIds = instances.stream().map(TaskInstanceRow::id).collect(toList());
            Map<String, List<ListValue>> listValues = this.listableProperties().stream()
                    .map(property -> property.dataType().tableOfList())
                    .map(table -> ListValue.selectByInstances(executor, table, instanceIds))
                    .flatMap(Collection::stream)
                    .collect(groupingBy(ListValue::instanceId));
            for (TaskInstanceRow instance : instances) {
                Map<String, List<Object>> propertyValues = Optional.ofNullable(listValues.get(instance.id()))
                        .map(Collection::stream).orElseGet(Stream::empty)
                        .sorted(Comparator.comparingInt(ListValue::index))
                        .collect(groupingBy(ListValue::propertyId, mapping(ListValue::value, toList())));
                for (TaskProperty property : this.listableProperties()) {
                    List<Object> values = nullIf(propertyValues.get(property.id()), Collections.emptyList());
                    Map<String, Object> info = new LinkedHashMap<>(instance.info());
                    info.put(property.name(), values);
                    instance.info(info);
                }
            }
        }

        abstract PagedResultSet<TaskInstance> view(Pagination pagination);
    }

    @FunctionalInterface
    private interface ViewerFactory {
        /**
         * 创建一个实例视图
         *
         * @param task 表示任务的{@link TaskEntity}
         * @param filter 表示任务筛选器的{@link TaskInstance.Filter}
         * @param context 表示操作上下文的{@link OperationContext}
         * @param orderBys 表示排序条件的{@link List}{@code <}{@link OrderBy}{@code >}
         * @return 返回实例视图
         */
        InstanceViewer create(TaskEntity task, TaskInstance.Filter filter, OperationContext context,
                List<OrderBy> orderBys);
    }

    private ViewerFactory factoryOfViewer(ViewMode mode) {
        switch (mode) {
            case LIST:
                return ListViewer::new;
            case TREE:
                return TreeViewer::new;
            default:
                throw new IllegalArgumentException("Unknown view mode: " + Enums.toString(mode));
        }
    }

    private class ListViewer extends InstanceViewer {
        ListViewer(TaskEntity task, TaskInstance.Filter filter, OperationContext context, List<OrderBy> orderBys) {
            super(task, filter, context, orderBys);
        }

        @Override
        PagedResultSet<TaskInstance> view(Pagination pagination) {
            SqlBuilder sql = SqlBuilder.custom();
            List<Object> args = new LinkedList<>();
            this.fillWithSql(sql, args);
            String withSql = sql.toString();
            sql = SqlBuilder.custom().append(withSql).append("SELECT COUNT(1) FROM ").appendIdentifier(WITH_TABLE);
            long count = longValue(executor.executeScalar(sql.toString(), args));
            SqlBuilder baseSql = SqlBuilder.custom();
            baseSql.append(withSql).append("SELECT * FROM ").appendIdentifier(WITH_TABLE);
            List<TaskInstance> instances = this.list(baseSql.toString(), args, pagination);
            return PagedResultSet.create(instances, PaginationResult.create(pagination, count));
        }
    }

    private class TreeViewer extends InstanceViewer {
        private static final String ID_KEY = "id";

        private static final String PARENT_KEY = "decomposed_from";

        TreeViewer(TaskEntity task, TaskInstance.Filter filter, OperationContext context, List<OrderBy> orderBys) {
            super(task, filter, context, orderBys);
        }

        @Override
        PagedResultSet<TaskInstance> view(Pagination pagination) {
            List<Object> baseArgs = new LinkedList<>();
            String baseSql = this.fillWithSql(SqlBuilder.custom(), baseArgs).toString();
            SqlBuilder prefix = SqlBuilder.custom().append(baseSql).append("SELECT ");
            SqlBuilder suffix = SqlBuilder.custom().append(" FROM ").appendIdentifier(WITH_TABLE);
            boolean isTreeSupported = this.hasTextProperty(ID_KEY) && this.hasTextProperty(PARENT_KEY);
            if (isTreeSupported) {
                ColumnRef parentColumn = ColumnRef.of(WITH_TABLE, TaskInstanceRow.INFO_PREFIX + PARENT_KEY);
                String idColumnName = TaskInstanceRow.INFO_PREFIX + TaskInstanceRow.COLUMN_ID;
                suffix.append(" WHERE ").append(parentColumn).append(" IS NULL OR ").append(parentColumn)
                        .append(" NOT IN (SELECT ").appendIdentifier(idColumnName).append(" FROM ")
                        .appendIdentifier(WITH_TABLE).append(" WHERE ").appendIdentifier(idColumnName)
                        .append(" IS NOT NULL)");
            }
            SqlBuilder sql = SqlBuilder.custom().append(prefix.toString()).append("COUNT(1)").append(suffix.toString());
            long count = longValue(executor.executeScalar(sql.toString(), baseArgs));
            List<TaskInstance> instances = this.list(prefix + "*" + suffix, baseArgs, pagination);
            if (isTreeSupported) {
                instances = this.buildTree(baseSql, baseArgs, instances).stream()
                        .map(TaskInstance.class::cast).collect(toList());
            }
            return PagedResultSet.create(instances, PaginationResult.create(pagination, count));
        }

        private boolean hasTextProperty(String key) {
            TaskProperty property = this.task.getPropertyByName(key);
            return property != null && property.dataType() == PropertyDataType.TEXT;
        }

        private List<HierarchicalTaskInstance> buildTree(String baseSql, List<Object> baseArgs,
                List<TaskInstance> instances) {
            if (instances.isEmpty()) {
                return Collections.emptyList();
            }
            List<String> ids = this.getCustomIds(instances);
            SqlBuilder sql = SqlBuilder.custom();
            sql.append(baseSql).append("SELECT * FROM ").appendIdentifier(WITH_TABLE).append(" WHERE ");
            List<Object> args = new ArrayList<>(baseArgs.size() + ids.size());
            args.addAll(baseArgs);
            Condition condition = Condition.expectIn(ColumnRef.of(WITH_TABLE, TaskInstanceRow.INFO_PREFIX + PARENT_KEY),
                    ids);
            condition.toSql(sql, args);
            List<Map<String, Object>> rows = executor.executeQuery(sql.toString(), args);
            List<TaskInstanceRow> childRows = TaskInstanceRow.convert(this.task, rows);
            this.fillListableProperties(childRows);
            fillAdditions(childRows, this.context);
            List<TaskInstance> children = toDomainObjects(this.task, childRows);
            convertListOwner(children);
            List<HierarchicalTaskInstance> hierarchicalChildren = this.buildTree(baseSql, baseArgs, children);
            Map<String, List<HierarchicalTaskInstance>> grouped = hierarchicalChildren.stream().collect(
                    groupingBy(this::getParentId, toList()));
            return instances.stream()
                    .map(instance -> HierarchicalTaskInstance.of(instance, grouped.get(this.getId(instance))))
                    .collect(toList());
        }

        private void convertListOwner(List<TaskInstance> children) {
            for (TaskInstance instance : children) {
                if (instance.info().get("owner") != null) {
                    if (instance.info().get("owner") instanceof ArrayList) {
                        instance.info().put("owner", String.join(",", (ArrayList) instance.info().get("owner")));
                    }
                }
            }
        }

        private List<String> getCustomIds(List<TaskInstance> instances) {
            return instances.stream()
                    .map(instance -> instance.info().get(ID_KEY))
                    .map(PropertyDataType.TEXT::fromExternal)
                    .map(String.class::cast)
                    .distinct()
                    .collect(toList());
        }

        private String getId(TaskInstance instance) {
            return cast(instance.info().get(ID_KEY));
        }

        private String getParentId(TaskInstance instance) {
            return cast(instance.info().get(PARENT_KEY));
        }
    }

    private void fillAdditions(List<TaskInstanceRow> rows, OperationContext context) {
        List<String> ids = rows.stream().map(TaskInstanceRow::id).collect(toList());
        Map<String, List<String>> tags = this.tagService.list(TaskInstanceRow.OBJECT_TYPE, ids, context);
        for (TaskInstanceRow row : rows) {
            row.tags(nullIf(tags.get(row.id()), Collections.emptyList()));
        }
        Map<String, List<String>> categories = this.categoryService.listUsages(TaskInstanceRow.OBJECT_TYPE, ids,
                context);
        for (TaskInstanceRow row : rows) {
            row.categories(nullIf(categories.get(row.id()), Collections.emptyList()));
        }
    }
}
