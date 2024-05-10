/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.domain.postgresql;

import static com.huawei.fit.jober.taskcenter.util.Maps.throwingMerger;
import static com.huawei.fit.jober.taskcenter.util.Sqls.longValue;
import static com.huawei.fitframework.util.ObjectUtils.cast;
import static com.huawei.fitframework.util.ObjectUtils.nullIf;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import com.huawei.fit.jane.task.domain.PropertyDataType;
import com.huawei.fit.jane.task.domain.TaskProperty;
import com.huawei.fit.jane.task.util.Dates;
import com.huawei.fit.jane.task.util.Entities;
import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.ServerInternalException;
import com.huawei.fit.jober.common.exceptions.BadRequestException;
import com.huawei.fit.jober.common.exceptions.ConflictException;
import com.huawei.fit.jober.common.exceptions.NotFoundException;
import com.huawei.fit.jober.taskcenter.domain.Index;
import com.huawei.fit.jober.taskcenter.domain.TaskEntity;
import com.huawei.fit.jober.taskcenter.domain.util.ListValue;
import com.huawei.fit.jober.taskcenter.event.TaskPropertyDeletingEvent;
import com.huawei.fit.jober.taskcenter.event.TaskPropertyIndexedEvent;
import com.huawei.fit.jober.taskcenter.event.TaskPropertyModifiedEvent;
import com.huawei.fit.jober.taskcenter.event.TaskPropertyModifyingEvent;
import com.huawei.fit.jober.taskcenter.event.TaskPropertyUnindexedEvent;
import com.huawei.fit.jober.taskcenter.service.TaskService;
import com.huawei.fit.jober.taskcenter.util.DynamicSqlExecutor;
import com.huawei.fit.jober.taskcenter.util.Enums;
import com.huawei.fit.jober.taskcenter.util.sql.Condition;
import com.huawei.fit.jober.taskcenter.util.sql.DeleteSql;
import com.huawei.fit.jober.taskcenter.util.sql.InsertSql;
import com.huawei.fit.jober.taskcenter.util.sql.SqlBuilder;
import com.huawei.fit.jober.taskcenter.util.sql.UpdateSql;
import com.huawei.fit.jober.taskcenter.validation.IndexValidator;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.event.EventHandler;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.plugin.Plugin;
import com.huawei.fitframework.transaction.Transactional;
import com.huawei.fitframework.util.CollectionUtils;
import com.huawei.fitframework.util.FunctionUtils;
import com.huawei.fitframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * 为 {@link Index.Repo} 提供基于 {@code Postgresql} 的实现。
 *
 * @author 梁济时 l00815032
 * @since 2024-01-05
 */
@Component
public class PostgresqlIndexRepo implements Index.Repo {
    private static final Logger log = Logger.get(PostgresqlIndexRepo.class);

    private final DynamicSqlExecutor executor;

    private final IndexValidator validator;

    private final Plugin plugin;

    public PostgresqlIndexRepo(DynamicSqlExecutor executor, IndexValidator validator, Plugin plugin) {
        this.executor = executor;
        this.validator = validator;
        this.plugin = plugin;
    }

    @Override
    @Transactional
    public Index create(TaskEntity task, Index.Declaration declaration, OperationContext context) {
        Row row = new Row();
        row.id(Entities.generateId());
        row.taskId(task.getId());
        row.name(this.validator.name(declaration.name()
                .required(() -> new BadRequestException(ErrorCodes.INDEX_NAME_REQUIRED))));
        row.propertyIds(propertyIds(task, declaration.propertyNames().get()));
        row.creator(context.operator());
        row.creationTime(Dates.toUtc(LocalDateTime.now()));
        row.lastModifier(row.creator());
        row.lastModificationTime(row.creationTime());
        Row.insert(this.executor, Collections.singletonList(row));
        Index index = toDomainObject(task, row);
        Set<TaskProperty> oldIndexedProperties = indexedProperties(task, FunctionUtils.alwaysTrue());
        Set<TaskProperty> newIndexedProperties = new HashSet<>(oldIndexedProperties);
        newIndexedProperties.addAll(index.properties());
        this.onIndexedChanged(task, oldIndexedProperties, newIndexedProperties);
        return index;
    }

    @Override
    @Transactional
    public void patch(TaskEntity task, String id, Index.Declaration declaration, OperationContext context) {
        String actualId = Entities.validateId(id, () -> new BadRequestException(ErrorCodes.INDEX_ID_INVALID));
        Index index = Optional.ofNullable(task.getIndexes()).map(Collection::stream).orElseGet(Stream::empty)
                .filter(Objects::nonNull).filter(idx -> StringUtils.equalsIgnoreCase(idx.id(), actualId))
                .findAny().orElseThrow(() -> new NotFoundException(ErrorCodes.INDEX_NOT_FOUND));
        UpdateSql sql = UpdateSql.custom().table(Row.TABLE)
                .set(Row.COLUMN_LAST_MODIFIER, context.operator())
                .set(Row.COLUMN_LAST_MODIFICATION_TIME, Dates.toUtc(LocalDateTime.now()))
                .where(Condition.expectEqual(Row.COLUMN_ID, actualId));
        declaration.name().ifDefined(name -> sql.set(Row.COLUMN_NAME, this.validator.name(name)));
        int affectedRows = sql.execute(this.executor);
        if (affectedRows < 1) {
            log.error("No row affected when patch index.");
            throw new ServerInternalException("Failed to patch index into database.");
        }
        declaration.propertyNames().ifDefined(propertyNames -> {
            // 使用两个 Set 缓存建立当前索引前后，任务定义中被索引的属性的集合，以便于添加完成后比较被索引属性的差异。
            Set<TaskProperty> oldIndexedProperties = indexedProperties(task, FunctionUtils.alwaysTrue());
            Set<TaskProperty> newIndexedProperties = indexedProperties(task, idx -> idx != index);
            List<String> expectedPropertyNames = Optional.ofNullable(propertyNames)
                    .map(Collection::stream).orElseGet(Stream::empty).map(StringUtils::trim)
                    .filter(StringUtils::isNotEmpty).collect(toList());
            List<PropertyRow> newPropertyRows = new LinkedList<>(); // 表示原本未建立，需要新建的索引属性。
            Set<TaskProperty> currentProperties = new HashSet<>(index.properties());
            for (String propertyName : expectedPropertyNames) {
                TaskProperty property = task.getPropertyByName(propertyName);
                if (property == null) {
                    log.error("Property to index does not exist. [task={}, property={}]", task.getName(), propertyName);
                    throw new BadRequestException(ErrorCodes.INDEX_UNKNOWN_PROPERTY);
                }
                newIndexedProperties.add(property);
                if (currentProperties.remove(property)) {
                    // 从原本包含的属性中移除掉正在建立索引的属性，最终该集合中剩余的属性即是需要删除的属性。
                    continue;
                }
                // 没有移除成功，则说明索引中原本不包含该属性，需要新建。
                PropertyRow row = new PropertyRow();
                row.id(Entities.generateId());
                row.indexId(actualId);
                row.propertyId(property.id());
                newPropertyRows.add(row);
            }
            PropertyRow.insert(this.executor, newPropertyRows);
            if (!currentProperties.isEmpty()) {
                PropertyRow.delete(this.executor, actualId, currentProperties.stream()
                        .map(TaskProperty::id).collect(toList()));
            }
            this.onIndexedChanged(task, oldIndexedProperties, newIndexedProperties);
        });
    }

    @Override
    @Transactional
    public void delete(TaskEntity task, String id, OperationContext context) {
        String actualId = Entities.validateId(id, () -> new BadRequestException(ErrorCodes.INDEX_ID_INVALID));
        Row.delete(this.executor, actualId);
        Set<TaskProperty> oldIndexedProperties = indexedProperties(task, FunctionUtils.alwaysTrue());
        Set<TaskProperty> newIndexedProperties = indexedProperties(task, index -> !index.id().equals(actualId));
        this.onIndexedChanged(task, oldIndexedProperties, newIndexedProperties);
    }

    @Override
    @Transactional
    public Index retrieve(TaskEntity task, String id, OperationContext context) {
        String actualId = Entities.validateId(id, () -> new BadRequestException(ErrorCodes.INDEX_ID_INVALID));
        Row row = Row.select(executor, actualId);
        return toDomainObject(task, row);
    }

    @Override
    @Transactional
    public List<Index> list(TaskEntity task, OperationContext context) {
        return this.list(Collections.singletonList(task), context);
    }

    @Override
    @Transactional
    public List<Index> list(List<TaskEntity> tasks, OperationContext context) {
        Map<String, TaskEntity> indexed = Optional.ofNullable(tasks).map(Collection::stream).orElseGet(Stream::empty)
                .filter(Objects::nonNull).collect(toMap(TaskEntity::getId, Function.identity()));
        List<Row> rows = Row.selectByTasks(this.executor, new ArrayList<>(indexed.keySet()));
        Map<String, List<Row>> groups = rows.stream().collect(groupingBy(Row::taskId));
        List<Index> indexes = new ArrayList<>(rows.size());
        for (Map.Entry<String, List<Row>> entry : groups.entrySet()) {
            indexes.addAll(toDomainObjects(indexed.get(entry.getKey()), entry.getValue()));
        }
        return indexes;
    }

    @Override
    @Transactional
    public void save(TaskEntity task, List<Index.Declaration> declarations, OperationContext context) {
        this.new Saver(task, declarations, context).save();
    }

    private class Saver {
        private final TaskEntity task;

        private final List<Index.Declaration> declarations;

        private final OperationContext context;

        private Map<String, Index> indexes;

        private Set<String> indexNames;

        private List<Row> insertingIndexRows;

        private List<String> updatingIndexIds;

        private List<PropertyRow> insertingPropertyRows;

        private Map<String, Set<String>> deletingIndexPropertyIds;

        private LocalDateTime operationTime;

        private Set<TaskProperty> oldIndexedProperties;

        private Set<TaskProperty> newIndexedProperties;

        private Saver(TaskEntity task, List<Index.Declaration> declarations, OperationContext context) {
            this.task = task;
            this.declarations = Optional.ofNullable(declarations).map(Collection::stream).orElseGet(Stream::empty)
                    .filter(Objects::nonNull).collect(toList());
            this.context = context;
        }

        private void reset() {
            this.indexes = Optional.ofNullable(this.task.getIndexes()).map(Collection::stream).orElseGet(Stream::empty)
                    .filter(Objects::nonNull)
                    .collect(toMap(Index::name, Function.identity(), throwingMerger(), LinkedHashMap::new));
            this.indexNames = new HashSet<>();
            this.insertingIndexRows = new LinkedList<>();
            this.updatingIndexIds = new LinkedList<>();
            this.insertingPropertyRows = new LinkedList<>();
            this.deletingIndexPropertyIds = new LinkedHashMap<>();
            this.operationTime = Dates.toUtc(LocalDateTime.now());
            this.oldIndexedProperties = this.indexes.values().stream()
                    .map(Index::properties)
                    .flatMap(Collection::stream)
                    .collect(toCollection(LinkedHashSet::new));
            this.newIndexedProperties = new LinkedHashSet<>();
        }

        private void analyseDeclarations() {
            for (Index.Declaration declaration : this.declarations) {
                String name = validator.name(declaration.name()
                        .required(() -> new BadRequestException(ErrorCodes.INDEX_NAME_REQUIRED)));
                if (!this.indexNames.add(name)) {
                    log.error("An index with the same name already exists in the task. [task={}, index={}]",
                            task.getName(), name);
                    throw new ConflictException(ErrorCodes.INDEX_NAME_DUPLICATE);
                }
                List<TaskProperty> properties = properties(this.task, declaration.propertyNames()
                        .required(() -> new BadRequestException(ErrorCodes.INDEX_PROPERTY_REQUIRED)));
                List<String> propertyIds = properties.stream().map(TaskProperty::id).collect(toList());
                Index existingIndex = this.indexes.remove(name);
                if (existingIndex == null) {
                    // 原本不存在该名称的索引，需要创建新的索引记录。
                    Row row = Row.create(this.task.getId(), name, this.context.operator(), this.operationTime);
                    this.insertingIndexRows.add(row);
                    this.insertingPropertyRows.addAll(PropertyRow.create(row.id(), propertyIds));
                } else {
                    this.updatingIndexIds.add(existingIndex.id());
                    Set<String> existingPropertyIds = existingIndex.properties().stream()
                            .map(TaskProperty::id).collect(toCollection(LinkedHashSet::new));
                    propertyIds.forEach(existingPropertyIds::remove);
                    if (!existingPropertyIds.isEmpty()) {
                        this.deletingIndexPropertyIds.put(existingIndex.id(), existingPropertyIds);
                    }
                    Set<String> newPropertyIds = new LinkedHashSet<>(propertyIds);
                    existingIndex.properties().forEach(property -> newPropertyIds.remove(property.id()));
                    this.insertingPropertyRows.addAll(PropertyRow.create(existingIndex.id(), newPropertyIds));
                }
                this.newIndexedProperties.addAll(properties);
            }
        }

        private void persist() {
            List<String> deletingIndexIds = this.indexes.values().stream().map(Index::id).collect(toList());
            Row.insert(executor, this.insertingIndexRows);
            if (!this.updatingIndexIds.isEmpty()) {
                int updatedRows = UpdateSql.custom().table(Row.TABLE)
                        .set(Row.COLUMN_LAST_MODIFIER, this.context.operator())
                        .set(Row.COLUMN_LAST_MODIFICATION_TIME, this.operationTime)
                        .where(Condition.expectIn(Row.COLUMN_ID, this.updatingIndexIds))
                        .execute(executor);
                if (updatedRows < this.updatingIndexIds.size()) {
                    log.error("Unexpected affected rows occurs when update indexes. [task={}, expected={}, actual={}]",
                            this.task.getName(), this.updatingIndexIds.size(), updatedRows);
                    throw new ServerInternalException("Failed to update indexes of task.");
                }
            }
            Row.delete(executor, deletingIndexIds);
            PropertyRow.insert(executor, this.insertingPropertyRows);
            Condition deletingPropertyCondition = null;
            if (!deletingIndexIds.isEmpty()) {
                deletingPropertyCondition = Condition.expectIn(PropertyRow.COLUMN_INDEX_ID, deletingIndexIds);
            }
            deletingPropertyCondition = this.deletingIndexPropertyIds.entrySet().stream()
                    .map(entry -> propertyCondition(entry.getKey(), entry.getValue()))
                    .reduce(deletingPropertyCondition, (c1, c2) -> Condition.or(c1, c2));
            if (deletingPropertyCondition != null) {
                DeleteSql.custom().from(PropertyRow.TABLE).where(deletingPropertyCondition).execute(executor);
            }
        }

        private void save() {
            if (this.declarations.isEmpty()) {
                return;
            }
            this.reset();
            this.analyseDeclarations();
            this.persist();
            onIndexedChanged(this.task, oldIndexedProperties, newIndexedProperties);
        }
    }

    private static Condition propertyCondition(String indexId, Collection<String> propertyIds) {
        return Condition.expectEqual(PropertyRow.COLUMN_INDEX_ID, indexId)
                .and(Condition.expectIn(PropertyRow.COLUMN_PROPERTY_ID, propertyIds));
    }

    private static List<String> propertyIds(TaskEntity task, List<String> propertyNames) {
        return properties(task, propertyNames).stream().map(TaskProperty::id).collect(toList());
    }

    private static List<TaskProperty> properties(TaskEntity task, List<String> propertyNames) {
        List<String> actual = nullIf(propertyNames, Collections.emptyList());
        List<TaskProperty> properties = new ArrayList<>(actual.size());
        for (String propertyName : actual) {
            String actualPropertyName = StringUtils.trim(propertyName);
            if (StringUtils.isEmpty(actualPropertyName)) {
                continue;
            }
            TaskProperty property = task.getPropertyByName(propertyName);
            if (property == null) {
                log.error("The property to index does not exist. [task={}, property={}]", task.getName(), propertyName);
                throw new BadRequestException(ErrorCodes.INDEX_UNKNOWN_PROPERTY);
            }
            if (!property.dataType().indexable()) {
                log.error("The property in the data type cannot be indexed. [task={}, property={}, dataType={}]",
                        task.getName(), propertyName, Enums.toString(property.dataType()));
                throw new BadRequestException(ErrorCodes.INDEX_DATA_TYPE_UNSUPPORTED);
            }
            properties.add(property);
        }
        if (properties.isEmpty()) {
            log.error("No property specified to index.");
            throw new BadRequestException(ErrorCodes.INDEX_PROPERTY_REQUIRED);
        }
        return properties;
    }

    private void onIndexedChanged(TaskEntity task, Set<TaskProperty> oldIndexed, Set<TaskProperty> newIndexed) {
        CollectionUtils.difference(oldIndexed, newIndexed).forEach(property -> {
            TaskPropertyUnindexedEvent event = new TaskPropertyUnindexedEvent(this, task, property);
            this.plugin.publisherOfEvents().publishEvent(event);
        });
        CollectionUtils.difference(newIndexed, oldIndexed).forEach(property -> {
            TaskPropertyIndexedEvent event = new TaskPropertyIndexedEvent(this, task, property);
            this.plugin.publisherOfEvents().publishEvent(event);
        });
    }

    private static Set<TaskProperty> indexedProperties(TaskEntity task, Predicate<Index> predicate) {
        return Optional.ofNullable(task.getIndexes()).map(Collection::stream).orElseGet(Stream::empty)
                .filter(Objects::nonNull).filter(predicate).map(Index::properties).flatMap(Collection::stream)
                .filter(Objects::nonNull).collect(toCollection(HashSet::new));
    }

    private static class Row extends AbstractDomainRow {
        private static final String TABLE = "index";

        private static final String TABLE_PROPERTY = "index_property";

        private static final String COLUMN_NAME = "name";

        private static final String COLUMN_TASK_ID = "task_id";

        private static final String PROPERTY_PROPERTY_IDS = "properties";

        private static final String COLUMN_INDEX_ID = "index_id";

        private static final String COLUMN_PROPERTY_ID = "property_id";

        Row() {
            this(null);
        }

        Row(Map<String, Object> values) {
            super(Optional.ofNullable(values).orElseGet(() -> new HashMap<>(7)));
        }

        String name() {
            return cast(this.get(COLUMN_NAME));
        }

        void name(String name) {
            this.set(COLUMN_NAME, name);
        }

        String taskId() {
            return cast(this.get(COLUMN_TASK_ID));
        }

        void taskId(String taskId) {
            this.set(COLUMN_TASK_ID, taskId);
        }

        List<String> propertyIds() {
            Object value = this.get(PROPERTY_PROPERTY_IDS);
            if (value == null) {
                value = Collections.emptyList();
                this.set(PROPERTY_PROPERTY_IDS, value);
            }
            return cast(value);
        }

        void propertyIds(List<String> propertyIds) {
            this.set(PROPERTY_PROPERTY_IDS, propertyIds);
        }

        static void insert(DynamicSqlExecutor executor, List<Row> rows) {
            if (rows.isEmpty()) {
                return;
            }
            InsertSql sql = InsertSql.custom().into(TABLE);
            List<PropertyRow> propertyRows = new LinkedList<>();
            for (Row row : rows) {
                sql.next();
                sql.value(COLUMN_ID, row.id());
                sql.value(COLUMN_NAME, row.name());
                sql.value(COLUMN_TASK_ID, row.taskId());
                sql.value(COLUMN_CREATOR, row.creator());
                sql.value(COLUMN_CREATION_TIME, row.creationTime());
                sql.value(COLUMN_LAST_MODIFIER, row.lastModifier());
                sql.value(COLUMN_LAST_MODIFICATION_TIME, row.lastModificationTime());
                propertyRows.addAll(row.propertyIds().stream().map(propertyId -> {
                    PropertyRow propertyRow = new PropertyRow();
                    propertyRow.id(Entities.generateId());
                    propertyRow.indexId(row.id());
                    propertyRow.propertyId(propertyId);
                    return propertyRow;
                }).collect(toList()));
            }
            int affectedRows = sql.execute(executor);
            if (affectedRows < rows.size()) {
                log.error("Unexpected affected rows occurs when insert indexes. [expected={}, actual={}]",
                        rows.size(), affectedRows);
                throw new ServerInternalException("Failed to insert indexes into database.");
            }
            PropertyRow.insert(executor, propertyRows);
        }

        static Row select(DynamicSqlExecutor executor, String indexId) {
            SqlBuilder sql = SqlBuilder.custom();
            fillSelectPrefix(sql);
            sql.appendIdentifier(COLUMN_ID).append(" = ?");
            List<Object> args = Collections.singletonList(indexId);
            List<Map<String, Object>> raws = executor.executeQuery(sql.toString(), args);
            if (raws.isEmpty()) {
                log.error("The index does not exist. [id={}]", indexId);
                throw new NotFoundException(ErrorCodes.INDEX_NOT_FOUND);
            }
            return convert(executor, raws).get(0);
        }

        static List<Row> selectByTasks(DynamicSqlExecutor executor, List<String> taskIds) {
            if (taskIds.isEmpty()) {
                return Collections.emptyList();
            }
            SqlBuilder sql = SqlBuilder.custom();
            fillSelectPrefix(sql);
            sql.appendIdentifier(COLUMN_TASK_ID).append(" IN (")
                    .appendRepeatedly("?, ", taskIds.size()).backspace(2).append(')');
            List<Map<String, Object>> raws = executor.executeQuery(sql.toString(), taskIds);
            if (raws.isEmpty()) {
                return Collections.emptyList();
            }
            return convert(executor, raws);
        }

        private static void fillSelectPrefix(SqlBuilder sql) {
            sql.append("SELECT ")
                    .appendIdentifier(COLUMN_ID).append(", ")
                    .appendIdentifier(COLUMN_NAME).append(", ")
                    .appendIdentifier(COLUMN_TASK_ID).append(", ")
                    .appendIdentifier(COLUMN_CREATOR).append(", ")
                    .appendIdentifier(COLUMN_CREATION_TIME).append(", ")
                    .appendIdentifier(COLUMN_LAST_MODIFIER).append(", ")
                    .appendIdentifier(COLUMN_LAST_MODIFICATION_TIME).append(" FROM ").appendIdentifier(TABLE)
                    .append(" WHERE ");
        }

        private static List<Row> convert(DynamicSqlExecutor executor, List<Map<String, Object>> raws) {
            Map<String, Row> rows = raws.stream().map(Row::new).collect(toMap(Row::id, Function.identity()));
            Map<String, List<String>> properties = selectProperties(executor, rows.keySet());
            for (Map.Entry<String, List<String>> entry : properties.entrySet()) {
                Row row = rows.get(entry.getKey());
                row.propertyIds(entry.getValue());
            }
            return new ArrayList<>(rows.values());
        }

        private static Map<String, List<String>> selectProperties(DynamicSqlExecutor executor, Set<String> indexIds) {
            SqlBuilder sql = SqlBuilder.custom().append("SELECT ")
                    .appendIdentifier(COLUMN_ID).append(", ")
                    .appendIdentifier(COLUMN_INDEX_ID).append(", ")
                    .appendIdentifier(COLUMN_PROPERTY_ID).append(" FROM ").appendIdentifier(TABLE_PROPERTY)
                    .append(" WHERE ").appendIdentifier(COLUMN_INDEX_ID).append(" IN (")
                    .appendRepeatedly("?, ", indexIds.size()).backspace(2).append(')');
            List<Map<String, Object>> raws = executor.executeQuery(sql.toString(), new ArrayList<>(indexIds));
            return raws.stream().collect(groupingBy(row -> (String) row.get(COLUMN_INDEX_ID),
                    mapping(row -> (String) row.get(COLUMN_PROPERTY_ID), toList())));
        }

        static Row create(String taskId, String name, String operator, LocalDateTime operationTime) {
            Row row = new Row();
            row.id(Entities.generateId());
            row.taskId(taskId);
            row.name(name);
            row.creator(operator);
            row.creationTime(operationTime);
            row.lastModifier(operator);
            row.lastModificationTime(operationTime);
            return row;
        }

        static void delete(DynamicSqlExecutor executor, String id) {
            int affectedRows = DeleteSql.custom().from(Row.TABLE).where(Condition.expectEqual(Row.COLUMN_ID, id))
                    .execute(executor);
            if (affectedRows < 1) {
                log.error("No row affected when delete index. [id={}]", id);
                throw new NotFoundException(ErrorCodes.INDEX_NOT_FOUND);
            }
            PropertyRow.deleteByIndexes(executor, Collections.singletonList(id));
        }

        static void delete(DynamicSqlExecutor executor, List<String> ids) {
            if (ids.isEmpty()) {
                return;
            }
            int affectedRows = DeleteSql.custom().from(Row.TABLE)
                    .where(Condition.expectIn(Row.COLUMN_ID, ids))
                    .execute(executor);
            if (affectedRows < ids.size()) {
                log.error("Unexpected affected rows occurs when delete indexes. [ids={}]", ids);
                throw new ServerInternalException("Failed to delete indexes.");
            }
        }
    }

    private static class PropertyRow {
        static final String TABLE = "index_property";

        static final String COLUMN_INDEX_ID = "index_id";

        static final String COLUMN_PROPERTY_ID = "property_id";

        private final Map<String, Object> values;

        PropertyRow() {
            this(null);
        }

        PropertyRow(Map<String, Object> values) {
            this.values = Optional.ofNullable(values).orElseGet(() -> new HashMap<>(3));
        }

        String id() {
            return cast(this.values.get(AbstractDomainRow.COLUMN_ID));
        }

        void id(String id) {
            this.values.put(AbstractDomainRow.COLUMN_ID, id);
        }

        String indexId() {
            return cast(this.values.get(COLUMN_INDEX_ID));
        }

        void indexId(String indexId) {
            this.values.put(COLUMN_INDEX_ID, indexId);
        }

        String propertyId() {
            return cast(this.values.get(COLUMN_PROPERTY_ID));
        }

        void propertyId(String propertyId) {
            this.values.put(COLUMN_PROPERTY_ID, propertyId);
        }

        static void insert(DynamicSqlExecutor executor, List<PropertyRow> rows) {
            if (rows.isEmpty()) {
                return;
            }
            InsertSql sql = InsertSql.custom().into(TABLE);
            for (PropertyRow row : rows) {
                sql.next();
                sql.value(AbstractDomainRow.COLUMN_ID, row.id());
                sql.value(COLUMN_INDEX_ID, row.indexId());
                sql.value(COLUMN_PROPERTY_ID, row.propertyId());
            }
            int affectedRows = sql.execute(executor);
            if (affectedRows < rows.size()) {
                log.error("Unexpected affected rows occurs when insert properties of index. [expected={}, actual={}]",
                        rows.size(), affectedRows);
                throw new ServerInternalException("Failed to insert properties of index into database.");
            }
        }

        static void deleteByIndexes(DynamicSqlExecutor executor, List<String> indexIds) {
            DeleteSql.custom().from(TABLE).where(Condition.expectIn(COLUMN_INDEX_ID, indexIds)).execute(executor);
        }

        static void delete(DynamicSqlExecutor executor, String indexId, List<String> propertyIds) {
            if (propertyIds.isEmpty()) {
                return;
            }
            Condition condition = Condition.expectEqual(COLUMN_INDEX_ID, indexId);
            condition = condition.and(Condition.expectIn(COLUMN_PROPERTY_ID, propertyIds));
            int affectedRows = DeleteSql.custom().from(TABLE).where(condition).execute(executor);
            if (affectedRows < propertyIds.size()) {
                log.error("Unexpected affected rows occurs when delete properties of index. [expected={}, actual={}]",
                        propertyIds.size(), affectedRows);
                throw new ServerInternalException("Failed to delete properties of index from database.");
            }
        }

        static PropertyRow create(String indexId, String propertyId) {
            PropertyRow row = new PropertyRow();
            row.id(Entities.generateId());
            row.indexId(indexId);
            row.propertyId(propertyId);
            return row;
        }

        static List<PropertyRow> create(String indexId, Collection<String> propertyIds) {
            return propertyIds.stream().map(propertyId -> create(indexId, propertyId)).collect(toList());
        }
    }

    private static Index toDomainObject(TaskEntity task, Row row) {
        List<Index> indexes = toDomainObjects(task, Collections.singletonList(row));
        return indexes.get(0);
    }

    private static List<Index> toDomainObjects(TaskEntity task, List<Row> rows) {
        Map<String, TaskProperty> properties = task.getProperties().stream()
                .collect(toMap(TaskProperty::id, Function.identity()));
        List<Index> indexes = new ArrayList<>(rows.size());
        for (Row row : rows) {
            List<TaskProperty> indexProperties = row.propertyIds().stream().map(properties::get)
                    .filter(Objects::nonNull).collect(toList());
            Index index = Index.custom().id(row.id()).name(row.name()).task(task).properties(indexProperties)
                    .creator(row.creator()).creationTime(Dates.fromUtc(row.creationTime()))
                    .lastModifier(row.lastModifier()).lastModificationTime(Dates.fromUtc(row.lastModificationTime()))
                    .build();
            indexes.add(index);
        }
        return indexes;
    }

    public static abstract class AbstractPropertyEventHandler {
        protected final DynamicSqlExecutor executor;

        public AbstractPropertyEventHandler(DynamicSqlExecutor executor) {
            this.executor = executor;
        }

        /**
         * 建立索引数据。
         *
         * @param property 表示待建立索引数据的属性的 {@link TaskProperty}。
         */
        protected void buildIndexValues(TaskProperty property) {
            this.buildIndexValues(null, property);
        }

        /**
         * 建立索引数据。
         *
         * @param task 表示待建立索引数据的任务定义的 {@link TaskEntity}。
         * @param property 表示待建立索引数据的属性的 {@link TaskProperty}。
         */
        protected void buildIndexValues(TaskEntity task, TaskProperty property) {
            String table = property.dataType().tableOfIndex();
            if (table == null) {
                log.error("The data type of property does not support indexes. [property={}, dataType={}]",
                        property.name(), property.dataType());
                throw new ServerInternalException("Unsupported data type to build index.");
            }
            SqlBuilder sql = SqlBuilder.custom().append("INSERT INTO ")
                    .appendIdentifier(table).append('(').appendIdentifier("id").append(", ")
                    .appendIdentifier("instance_id").append(", ").appendIdentifier("property_id").append(", ")
                    .appendIdentifier("value").append(") ");
            List<Object> args;
            if (property.dataType().listable()) {
                sql.append("SELECT generate_uuid_text(), ").appendIdentifier(ListValue.COLUMN_INSTANCE_ID).append(", ")
                        .appendIdentifier(ListValue.COLUMN_PROPERTY_ID).append(", ")
                        .appendIdentifier(ListValue.COLUMN_VALUE).append(" FROM ")
                        .appendIdentifier(property.dataType().tableOfList()).append(" WHERE ")
                        .appendIdentifier(ListValue.COLUMN_PROPERTY_ID).append(" = ?");
                args = Collections.singletonList(property.id());
            } else {
                String taskId;
                if (task == null) {
                    taskId = obtainTaskId(this.executor, property.id());
                } else {
                    taskId = task.getId();
                }
                sql.append("SELECT generate_uuid_text(), \"id\", ?, ").appendIdentifier(property.column())
                        .append(" FROM ").appendIdentifier("task_instance_wide")
                        .append(" WHERE ").appendIdentifier("task_id").append(" = ?");
                args = Arrays.asList(property.id(), taskId);
            }
            int affectedRows = this.executor.executeUpdate(sql.toString(), args);
            log.info("Total {} rows inserted into {} to build index.", affectedRows, table);
        }

        /**
         * 移除索引数据。
         *
         * @param property 表示待移除索引数据的属性的 {@link TaskProperty}。
         */
        protected void removeIndexValues(TaskProperty property) {
            if (!property.dataType().indexable()) {
                return;
            }
            String table = property.dataType().tableOfIndex();
            int affectedRows = DeleteSql.custom().from(table)
                    .where(Condition.expectEqual("property_id", property.id()))
                    .execute(this.executor);
            log.info("Total {} rows deleted from {} to remove index.", affectedRows, table);
        }

        /**
         * 从所有索引定义中删除指定属性，如果删除该属性后的索引中不再包含其他属性，则同时删除索引。
         *
         * @param property 表示待删除索引数据的属性的 {@link TaskProperty}。
         */
        protected boolean unindexProperty(TaskProperty property) {
            SqlBuilder sql = SqlBuilder.custom().append("SELECT ").appendIdentifier(PropertyRow.COLUMN_INDEX_ID)
                    .append(", COUNT(1) AS ").appendIdentifier("count").append(" FROM ")
                    .appendIdentifier(PropertyRow.TABLE).append(" WHERE ").appendIdentifier(PropertyRow.COLUMN_INDEX_ID)
                    .append(" IN (").append("SELECT DISTINCT ").appendIdentifier(PropertyRow.COLUMN_INDEX_ID)
                    .append(" FROM ").appendIdentifier(PropertyRow.TABLE).append(" WHERE ")
                    .appendIdentifier(PropertyRow.COLUMN_PROPERTY_ID).append(" = ?) GROUP BY ")
                    .appendIdentifier(PropertyRow.COLUMN_INDEX_ID);
            List<Object> args = Collections.singletonList(property.id());
            List<Map<String, Object>> rows = this.executor.executeQuery(sql.toString(), args);
            if (rows.isEmpty()) {
                return false;
            }
            List<String> deletingIndexIds = new LinkedList<>();
            for (Map<String, Object> row : rows) {
                String indexId = cast(row.get(PropertyRow.COLUMN_INDEX_ID));
                long count = longValue(row.get("count"));
                if (count == 1) {
                    deletingIndexIds.add(indexId);
                }
            }
            DeleteSql.custom().from(PropertyRow.TABLE)
                    .where(Condition.expectEqual(PropertyRow.COLUMN_PROPERTY_ID, property.id()))
                    .execute(this.executor);
            Row.delete(this.executor, deletingIndexIds);
            return true;
        }
    }

    @Component
    public static class IndexedEventHandler extends AbstractPropertyEventHandler
            implements EventHandler<TaskPropertyIndexedEvent> {
        public IndexedEventHandler(DynamicSqlExecutor executor) {
            super(executor);
        }

        @Override
        @Transactional
        public void handleEvent(TaskPropertyIndexedEvent event) {
            this.buildIndexValues(event.task(), event.property());
        }
    }

    @Component
    public static class UnindexedEventHandler extends AbstractPropertyEventHandler
            implements EventHandler<TaskPropertyUnindexedEvent> {
        public UnindexedEventHandler(DynamicSqlExecutor executor) {
            super(executor);
        }

        @Override
        @Transactional
        public void handleEvent(TaskPropertyUnindexedEvent event) {
            this.removeIndexValues(event.property());
        }
    }

    /**
     * 处理 {@link TaskPropertyModifyingEvent} 事件。
     * <p>如果修改前属性已被用作索引，但是修改后的数据类型将不再支持索引，则将从索引中删除该属性。如果删除属性后，索引中不再包含其他属性，则同时删除索引。</p>
     *
     * @author 梁济时 l00815032
     * @since 2024-01-31
     */
    @Component
    public static class PropertyModifyingEventHandler extends AbstractPropertyEventHandler
            implements EventHandler<TaskPropertyModifyingEvent> {
        private final Plugin plugin;

        private final TaskService taskService;

        public PropertyModifyingEventHandler(DynamicSqlExecutor executor, Plugin plugin, TaskService taskService) {
            super(executor);
            this.plugin = plugin;
            this.taskService = taskService;
        }

        @Override
        @Transactional
        public void handleEvent(TaskPropertyModifyingEvent event) {
            if (!event.declaration().dataType().defined() || !event.property().dataType().indexable()) {
                return;
            }
            String dataTypeString = StringUtils.trim(event.declaration().dataType().get());
            PropertyDataType dataType;
            if (StringUtils.isEmpty(dataTypeString)) {
                dataType = PropertyDataType.DEFAULT;
            } else {
                dataType = Enums.parse(PropertyDataType.class, dataTypeString);
            }
            if (!dataType.indexable() && this.unindexProperty(event.property())) {
                TaskEntity task = obtainTask(this.executor, event.property().id(), this.taskService);
                this.plugin.publisherOfEvents()
                        .publishEvent(new TaskPropertyUnindexedEvent(this, task, event.property()));
            }
        }
    }

    /**
     * 处理 {@link TaskPropertyModifiedEvent} 事件。
     * <p>如果所修改的属性已被用作索引，且其数据类型发生了变化，则删除原有数据类型的索引数据，并重建新的数据类型的索引数据。</p>
     *
     * @author 梁济时 l00815032
     * @since 2024-01-31
     */
    @Component
    public static class PropertyModifiedEventHandler extends AbstractPropertyEventHandler
            implements EventHandler<TaskPropertyModifiedEvent> {
        public PropertyModifiedEventHandler(DynamicSqlExecutor executor) {
            super(executor);
        }

        @Override
        @Transactional
        public void handleEvent(TaskPropertyModifiedEvent event) {
            if (!event.oldProperty().dataType().indexable()
                    || event.property().dataType() == event.oldProperty().dataType()) {
                return;
            }
            if (this.indexed(event.property().id())) {
                this.removeIndexValues(event.oldProperty());
                this.buildIndexValues(event.property());
            }
        }

        private boolean indexed(String propertyId) {
            SqlBuilder sql = SqlBuilder.custom().append("SELECT COUNT(1) FROM ").appendIdentifier("index_property")
                    .append(" WHERE ").appendIdentifier("property_id").append(" = ?");
            List<Object> args = Collections.singletonList(propertyId);
            long count = longValue(executor.executeScalar(sql.toString(), args));
            return count > 0;
        }
    }

    /**
     * 处理 {@link TaskPropertyDeletingEvent} 事件。
     * <p>如果被删除的属性被用作索引，则将从索引中删除该属性。如果删除属性后，索引中不再包含其他属性，则同时删除索引。</p>
     *
     * @author 梁济时 l00815032
     * @since 2024-01-31
     */
    @Component
    public static class PropertyDeletingEventHandler extends AbstractPropertyEventHandler
            implements EventHandler<TaskPropertyDeletingEvent> {
        private final Plugin plugin;

        private final TaskService taskService;

        public PropertyDeletingEventHandler(DynamicSqlExecutor executor, Plugin plugin,
                TaskService taskService) {
            super(executor);
            this.plugin = plugin;
            this.taskService = taskService;
        }

        @Override
        @Transactional
        public void handleEvent(TaskPropertyDeletingEvent event) {
            if (!event.property().dataType().indexable() || !this.unindexProperty(event.property())) {
                return;
            }
            TaskEntity task = obtainTask(this.executor, event.property().id(), this.taskService);
            this.plugin.publisherOfEvents()
                    .publishEvent(new TaskPropertyUnindexedEvent(this, task, event.property()));
        }
    }

    private static String obtainTaskId(DynamicSqlExecutor executor, String propertyId) {
        SqlBuilder sql = SqlBuilder.custom().append("SELECT ").appendIdentifier("task_id").append(" FROM ")
                .appendIdentifier("task_property").append(" WHERE ").appendIdentifier("id").append(" = ?");
        List<Object> args = Collections.singletonList(propertyId);
        return cast(executor.executeScalar(sql.toString(), args));
    }

    private static TaskEntity obtainTask(DynamicSqlExecutor executor, String propertyId, TaskService taskService) {
        String taskId = obtainTaskId(executor, propertyId);
        return taskService.retrieve(taskId, OperationContext.empty());
    }
}
