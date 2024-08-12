/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.domain.util;

import static com.huawei.fit.jober.taskcenter.util.Sqls.longValue;
import static com.huawei.fitframework.util.ObjectUtils.cast;
import static com.huawei.fitframework.util.ObjectUtils.nullIf;

import com.huawei.fit.jane.task.domain.PropertyDataType;
import com.huawei.fit.jane.task.domain.TaskProperty;
import com.huawei.fit.jane.task.util.Entities;
import com.huawei.fit.jober.common.ServerInternalException;
import com.huawei.fit.jober.taskcenter.domain.TaskEntity;
import com.huawei.fit.jober.taskcenter.util.DynamicSqlExecutor;
import com.huawei.fit.jober.taskcenter.util.sql.ColumnRef;
import com.huawei.fit.jober.taskcenter.util.sql.Condition;
import com.huawei.fit.jober.taskcenter.util.sql.DeleteSql;
import com.huawei.fit.jober.taskcenter.util.sql.InsertSql;
import com.huawei.fit.jober.taskcenter.util.sql.SqlBuilder;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.util.CollectionUtils;
import com.huawei.fitframework.util.StringUtils;

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
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 表示任务实例的数据对象。
 *
 * @author 梁济时
 * @since 2023-08-18
 */
public class TaskInstanceRow {
    /**
     * 表示实例对象的类型。
     */
    public static final String OBJECT_TYPE = "INSTANCE";

    /**
     * 表示任务实例的数据表。
     */
    public static final String TABLE = "task_instance_wide";

    /**
     * 表示已删除的任务实例。
     */
    public static final String TABLE_DELETED = "task_instance_deleted";

    /**
     * 表示任务实例的别名。
     */
    public static final String TABLE_ALIAS = "ins";

    /**
     * 表示任务实例的唯一标识。
     */
    public static final String COLUMN_ID = "id";

    /**
     * 表示任务定义的唯一标识。
     */
    public static final String COLUMN_TASK_ID = "task_id";

    /**
     * 表示任务类型的唯一标识。
     */
    public static final String COLUMN_TYPE_ID = "task_type_id";

    /**
     * 表示任务数据源的唯一标识。
     */
    public static final String COLUMN_SOURCE_ID = "source_id";

    /**
     * 表示任务的属性信息
     */
    public static final String PROPERTY_INFO = "info";

    /**
     * 表示任务实例的标签。
     */
    public static final String PROPERTY_TAGS = "tags";

    /**
     * 表示任务实例的类目。
     */
    public static final String PROPERTY_CATEGORIES = "categories";

    /**
     * 表示任务实例的信息前缀。
     */
    public static final String INFO_PREFIX = "info_";

    private static final int PROPERTY_COUNT = 7;

    private static final Logger log = Logger.get(TaskInstanceRow.class);

    private final Map<String, Object> values;

    public TaskInstanceRow() {
        this(null);
    }

    public TaskInstanceRow(Map<String, Object> values) {
        this.values = Optional.ofNullable(values).orElseGet(() -> new HashMap<>(PROPERTY_COUNT));
    }

    /**
     * 获取任务实例数据行中包含的数据。
     *
     * @return 表示数据行的数据的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    public Map<String, Object> values() {
        return this.values;
    }

    /**
     * 获取任务实例的唯一标识。
     *
     * @return 表示任务实例唯一标识的 {@link String}。
     */
    public String id() {
        return cast(this.values.get(COLUMN_ID));
    }

    /**
     * 设置数据对象的唯一标识。
     *
     * @param id 表示数据对象唯一标识的 {@link String}。
     */
    public void id(String id) {
        this.values.put(COLUMN_ID, id);
    }

    /**
     * 获取任务实例所属任务定义的唯一标识。
     *
     * @return 表示任务定义唯一标识的 {@link String}。
     */
    public String taskId() {
        return cast(this.values.get(COLUMN_TASK_ID));
    }

    /**
     * 设置任务实例所属任务定义的唯一标识。
     *
     * @param taskId 表示任务定义唯一标识的 {@link String}。
     */
    public void taskId(String taskId) {
        this.values.put(COLUMN_TASK_ID, taskId);
    }

    /**
     * 获取任务实例所属任务类型的唯一标识。
     *
     * @return 表示任务类型唯一标识的 {@link String}。
     */
    public String typeId() {
        return cast(this.values.get(COLUMN_TYPE_ID));
    }

    /**
     * 设置任务实例所属任务类型的唯一标识。
     *
     * @param typeId 表示任务类型唯一标识的 {@link String}。
     */
    public void typeId(String typeId) {
        this.values.put(COLUMN_TYPE_ID, typeId);
    }

    /**
     * 获取任务实例所属数据源的唯一标识。
     *
     * @return 表示任务数据源唯一标识的 {@link String}。
     */
    public String sourceId() {
        return cast(this.values.get(COLUMN_SOURCE_ID));
    }

    /**
     * 设置任务实例所属数据源的唯一标识。
     *
     * @param sourceId 表示任务数据源唯一标识的 {@link String}。
     */
    public void sourceId(String sourceId) {
        this.values.put(COLUMN_SOURCE_ID, sourceId);
    }

    /**
     * 获取任务实例的自定义数据。
     *
     * @return 表示自定义数据的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    public Map<String, Object> info() {
        return cast(this.values.get(PROPERTY_INFO));
    }

    /**
     * 设置任务实例的自定义数据。
     *
     * @param info 表示自定义数据的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     */
    public void info(Map<String, Object> info) {
        this.values.put(PROPERTY_INFO, info);
    }

    /**
     * 获取任务实例中定义的标签。
     *
     * @return 表示标签的列表的 {@link String}。
     */
    public List<String> tags() {
        return nullIf(cast(this.values.get(PROPERTY_TAGS)), Collections.emptyList());
    }

    /**
     * 设置任务实例中定义的标签。
     *
     * @param tags 表示标签的列表的 {@link String}。
     */
    public void tags(List<String> tags) {
        this.values.put(PROPERTY_TAGS, tags);
    }

    /**
     * 获取任务实例所属的类目。
     *
     * @return 表示类目列表的 {@link List}{@code <}{@link String}{@code >}。
     */
    public List<String> categories() {
        return nullIf(cast(this.values.get(PROPERTY_CATEGORIES)), Collections.emptyList());
    }

    /**
     * 设置任务实例所属的类目。
     *
     * @param categories 表示类目列表的 {@link List}{@code <}{@link String}{@code >}。
     */
    public void categories(List<String> categories) {
        this.values.put(PROPERTY_CATEGORIES, categories);
    }

    /**
     * 创建任务实例的数据对象。
     *
     * @param taskId 表示任务定义唯一标识的 {@link String}。
     * @param typeId 表示任务类型唯一标识的 {@link String}。
     * @param sourceId 表示任务数据源唯一标识的 {@link String}。
     * @param values 表示自定义数据的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     * @return 表示新创建的数据对象的 {@link TaskInstanceRow}。
     */
    public static TaskInstanceRow create(String taskId, String typeId, String sourceId, Map<String, Object> values) {
        TaskInstanceRow row = new TaskInstanceRow();
        row.taskId(taskId);
        row.typeId(typeId);
        row.sourceId(sourceId);
        row.info(values);
        return row;
    }

    /**
     * 将任务实例的数据对象插入到数据库中。
     *
     * @param executor 表示数据库执行器的 {@link DynamicSqlExecutor}。
     * @param task 表示任务定义的 {@link TaskEntity}。
     */
    public void insert(DynamicSqlExecutor executor, TaskEntity task) {
        InsertSql sql = InsertSql.custom().into(TABLE);
        sql.value(COLUMN_ID, this.id());
        sql.value(COLUMN_TASK_ID, this.taskId());
        sql.value(COLUMN_TYPE_ID, this.typeId());
        sql.value(COLUMN_SOURCE_ID, this.sourceId());
        List<TaskProperty> listableProperties = new LinkedList<>();
        for (TaskProperty property : task.getProperties()) {
            if (property.dataType().listable()) {
                listableProperties.add(property);
            } else {
                sql.value(property.column(), this.info().get(property.name()));
            }
        }
        int affectedRows = sql.execute(executor);
        if (affectedRows < 1) {
            log.error("No row affected when insert task instance into database.");
            throw new ServerInternalException("Failed to insert task instance into database.");
        }
        this.insertListValues(executor, listableProperties);
    }

    private void insertListValues(DynamicSqlExecutor executor, List<TaskProperty> properties) {
        Map<String, List<TaskProperty>> tableGrouped = properties.stream().collect(Collectors.groupingBy(
                property -> property.dataType().tableOfList(), LinkedHashMap::new, Collectors.toList()));
        tableGrouped.forEach((key, value) -> this.insertListValues(executor, key, value));
    }

    private void insertListValues(DynamicSqlExecutor executor, String table,
            List<TaskProperty> properties) {
        List<ListValue> listValues = properties.stream()
                .map(this::listValue)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        ListValue.insert(executor, table, listValues);
    }

    private List<ListValue> listValue(TaskProperty property) {
        List<?> propertyValues = cast(this.info().get(property.name()));
        List<ListValue> results = new ArrayList<>(propertyValues.size());
        int index = 0;
        for (Object value : propertyValues) {
            ListValue result = new ListValue();
            result.id(Entities.generateId());
            result.instanceId(this.id());
            result.propertyId(property.id());
            result.index(++index);
            result.value(value);
            results.add(result);
        }
        return results;
    }

    /**
     * 填充查询语句的前缀部分
     *
     * @param sql 表示查询语句的 {@link SqlBuilder}。
     * @param task 表示任务定义的 {@link TaskEntity}。
     */
    public static void fillSelectPrefix(SqlBuilder sql, TaskEntity task) {
        sql.append("SELECT ")
                .appendIdentifier(TABLE_ALIAS).append('.').appendIdentifier(COLUMN_ID).append(", ")
                .appendIdentifier(TABLE_ALIAS).append('.').appendIdentifier(COLUMN_TASK_ID).append(", ")
                .appendIdentifier(TABLE_ALIAS).append('.').appendIdentifier(COLUMN_TYPE_ID).append(", ")
                .appendIdentifier(TABLE_ALIAS).append('.').appendIdentifier(COLUMN_SOURCE_ID);
        for (TaskProperty property : task.getProperties()) {
            if (property.dataType().listable()) {
                continue;
            }
            String alias = INFO_PREFIX + property.name();
            sql.append(", ").appendIdentifier(TABLE_ALIAS).append('.').appendIdentifier(property.column());
            sql.append(" AS ").appendIdentifier(alias);
        }
        sql.append(" FROM ");
    }

    /**
     * 将数据库查询结果转换为任务实例的数据对象。
     *
     * @param task 表示任务定义的 {@link TaskEntity}。
     * @param row 表示数据库查询结果的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     * @return 表示转换后的数据对象的 {@link TaskInstanceRow}。
     */
    public static TaskInstanceRow convert(TaskEntity task, Map<String, Object> row) {
        Map<String, Object> actual = new HashMap<>(PROPERTY_COUNT);
        Map<String, Object> info = new HashMap<>(task.getProperties().size());
        for (Map.Entry<String, Object> entry : row.entrySet()) {
            String key = entry.getKey();
            if (StringUtils.startsWithIgnoreCase(key, INFO_PREFIX)) {
                key = key.substring(INFO_PREFIX.length());
                TaskProperty property = task.getPropertyByName(key);
                PropertyDataType propertyDataType = property.dataType();
                Object value = propertyDataType.fromPersistence(entry.getValue());
                info.put(key, value);
            } else {
                actual.put(key, entry.getValue());
            }
        }
        actual.put(PROPERTY_INFO, info);
        return new TaskInstanceRow(actual);
    }

    /**
     * 将数据库查询结果转换为任务实例的数据对象。
     *
     * @param task 表示任务定义的 {@link TaskEntity}。
     * @param rows 表示数据库查询结果的列表的 {@link List}{@code <}{@link Map}{@code <}{@link String}{@code ,
     * }{@link Object}{@code >}{@code >}。
     * @return 表示转换后的数据对象的列表的 {@link List}{@code <}{@link TaskInstanceRow}{@code >}。
     */
    public static List<TaskInstanceRow> convert(TaskEntity task, List<Map<String, Object>> rows) {
        return rows.stream()
                .map(row -> convert(task, row))
                .collect(Collectors.toList());
    }

    /**
     * 从数据库中选择指定的任务实例。
     *
     * @param executor 表示数据库执行器的 {@link DynamicSqlExecutor}。
     * @param task 表示任务定义的 {@link TaskEntity}。
     * @param instanceId 表示任务实例唯一标识的 {@link String}。
     * @param table 表示任务实例的数据表的 {@link String}。
     * @return 表示任务实例的数据对象的 {@link TaskInstanceRow}。
     */
    public static TaskInstanceRow select(DynamicSqlExecutor executor, TaskEntity task, String instanceId,
            String table) {
        SqlBuilder sql = SqlBuilder.custom();
        fillSelectPrefix(sql, task);
        sql.appendIdentifier(table).append(" AS ").appendIdentifier(TABLE_ALIAS).append(" WHERE ")
                .appendIdentifier(TABLE_ALIAS).append('.').appendIdentifier(COLUMN_ID).append(" = ? AND ")
                .appendIdentifier(TABLE_ALIAS).append('.').appendIdentifier(COLUMN_TASK_ID).append(" = ?");
        List<Map<String, Object>> rows = executor.executeQuery(sql.toString(), Arrays.asList(instanceId, task.getId()));
        if (rows.isEmpty()) {
            return null;
        }
        TaskInstanceRow row = convert(task, rows.get(0));
        List<TaskProperty> listableProperties = task.getProperties().stream()
                .filter(property -> property.dataType().listable())
                .collect(Collectors.toList());
        Map<String, List<ListValue>> propertyValues = listableProperties.stream()
                .map(property -> property.dataType().tableOfList())
                .distinct()
                .map(listTable -> ListValue.selectByInstance(executor, listTable, instanceId))
                .flatMap(Collection::stream)
                .collect(Collectors.groupingBy(ListValue::propertyId));
        row.info(new LinkedHashMap<>(row.info()));
        for (TaskProperty property : listableProperties) {
            List<ListValue> listValues = propertyValues.get(property.id());
            List<Object> currentValues;
            if (listValues == null) {
                currentValues = Collections.emptyList();
            } else {
                currentValues = listValues.stream()
                        .sorted(Comparator.comparingInt(ListValue::index))
                        .map(ListValue::value)
                        .collect(Collectors.toList());
            }
            row.info().put(property.name(), property.dataType().fromPersistence(currentValues));
        }
        return row;
    }

    /**
     * 检查指定的任务实例是否存在于数据库中。
     *
     * @param executor 表示数据库执行器的 {@link DynamicSqlExecutor}。
     * @param task 表示任务定义的 {@link TaskEntity}。
     * @param instanceId 表示任务实例的唯一标识的 {@link String}。
     * @param table 表示任务实例的数据表的名称的 {@link String}。
     * @return 如果任务实例存在，则返回 {@code true}；否则，返回 {@code false}。
     */
    public static boolean exist(DynamicSqlExecutor executor, TaskEntity task, String instanceId, String table) {
        SqlBuilder sql = SqlBuilder.custom().append("SELECT COUNT(1) FROM ").appendIdentifier(table).append(" WHERE ")
                .appendIdentifier(COLUMN_ID).append(" = ? AND ").appendIdentifier(COLUMN_TASK_ID).append(" = ?");
        long count = longValue(executor.executeScalar(sql.toString(), Arrays.asList(instanceId, task.getId())));
        return count > 0;
    }

    /**
     * 从数据库中选择指定的任务实例。
     *
     * @param executor 表示数据库执行器的 {@link DynamicSqlExecutor}。
     * @param task 表示任务定义的 {@link TaskEntity}。
     * @param primary 表示任务实例的主键值的 {@link PrimaryValue}。
     * @return 表示任务实例的数据对象的 {@link TaskInstanceRow}。
     */
    public static TaskInstanceRow select(DynamicSqlExecutor executor, TaskEntity task, PrimaryValue primary) {
        return select(executor, task, primary, TABLE);
    }

    /**
     * 从数据库中选择指定的已删除的任务实例。
     *
     * @param executor 表示数据库执行器的 {@link DynamicSqlExecutor}。
     * @param task 表示任务定义的 {@link TaskEntity}。
     * @param primary 表示任务实例的主键值的 {@link PrimaryValue}。
     * @return 表示任务实例的数据对象的 {@link TaskInstanceRow}。
     */
    public static TaskInstanceRow selectHistory(DynamicSqlExecutor executor, TaskEntity task, PrimaryValue primary) {
        return select(executor, task, primary, TABLE_DELETED);
    }

    private static TaskInstanceRow select(DynamicSqlExecutor executor, TaskEntity task, PrimaryValue primaryValue,
            String table) {
        if (primaryValue.isEmpty()) {
            return null;
        }
        SqlBuilder sql = SqlBuilder.custom();
        fillSelectPrefix(sql, task);
        sql.appendIdentifier(table).append(" AS ").appendIdentifier(TABLE_ALIAS).append(" WHERE ");
        sql.appendIdentifier(TABLE_ALIAS).append('.').appendIdentifier(COLUMN_TASK_ID).append(" = ?");
        List<Object> args = new LinkedList<>();
        args.add(task.getId());
        for (Map.Entry<String, Object> entry : primaryValue.values().entrySet()) {
            TaskProperty property = task.getPropertyByName(entry.getKey());
            sql.append(" AND ");
            sql.appendIdentifier(TABLE_ALIAS).append('.').appendIdentifier(property.column()).append(" = ?");
            args.add(entry.getValue());
        }
        List<Map<String, Object>> rows = executor.executeQuery(sql.toString(), args);
        if (rows.isEmpty()) {
            return null;
        }
        return convert(task, rows.get(0));
    }

    /**
     * 从数据库中删除指定的任务实例。
     *
     * @param executor 表示数据库执行器的 {@link DynamicSqlExecutor}。
     * @param task 表示任务定义的 {@link TaskEntity}。
     * @param id 表示任务实例的唯一标识的 {@link String}。
     * @param table 表示任务实例的数据表的名称的 {@link String}。
     */
    public static void delete(DynamicSqlExecutor executor, TaskEntity task, String id, String table) {
        Condition condition = Condition.expectEqual(COLUMN_ID, id);
        condition = condition.and(Condition.expectEqual(COLUMN_TASK_ID, task.getId()));
        DeleteSql deleteSql = DeleteSql.custom().from(table).where(condition);
        int affectedRows = deleteSql.execute(executor);
        if (affectedRows < 1) {
            log.error("No row affected when delete task instance from database. [task={}, instanceId={}]",
                    task.getName(), id);
            throw new ServerInternalException("Failed to delete task instance.");
        }
    }

    /**
     * 将指定的任务实例从一个数据表移动到另一个数据表。
     *
     * @param executor 表示数据库执行器的 {@link DynamicSqlExecutor}。
     * @param task 表示任务定义的 {@link TaskEntity}。
     * @param id 表示任务实例的唯一标识的 {@link String}。
     * @param sourceTable 表示源数据表的名称的 {@link String}。
     * @param targetTable 表示目标数据表的名称的 {@link String}。
     */
    public static void move(DynamicSqlExecutor executor, TaskEntity task, String id,
            String sourceTable, String targetTable) {
        SqlBuilder sql = SqlBuilder.custom().append("INSERT INTO ").appendIdentifier(targetTable)
                .append(" SELECT * FROM ").appendIdentifier(sourceTable).append(" WHERE ")
                .appendIdentifier(COLUMN_ID).append(" = ? AND ").appendIdentifier(COLUMN_TASK_ID).append(" = ?");
        int affectedRows = executor.executeUpdate(sql.toString(), Arrays.asList(id, task.getId()));
        if (affectedRows < 1) {
            log.error("Failed to move task instance. [task={}, instance={}, from={}, to={}]",
                    task.getId(), id, sourceTable, targetTable);
            throw new ServerInternalException("Failed to move task instance.");
        }
        delete(executor, task, id, sourceTable);
    }

    /**
     * 从数据库中选择与指定数据源关联的所有任务实例。
     *
     * @param executor 表示数据库执行器的 {@link DynamicSqlExecutor}。
     * @param sourceId 表示数据源唯一标识的 {@link String}。
     * @return 表示任务实例唯一标识的 {@link List}{@code <}{@link String}{@code >}。
     */
    public static List<String> selectIdsBySource(DynamicSqlExecutor executor, String sourceId) {
        SqlBuilder sql = SqlBuilder.custom().append("SELECT ").appendIdentifier(COLUMN_ID).append(" FROM ")
                .appendIdentifier(TABLE).append(" WHERE ").appendIdentifier(COLUMN_SOURCE_ID).append(" = ?");
        List<Map<String, Object>> rows = executor.executeQuery(sql.toString(), Collections.singletonList(sourceId));
        return rows.stream().<String>map(row -> cast(row.get(COLUMN_ID))).collect(Collectors.toList());
    }

    /**
     * 将与指定数据源关联的所有任务实例移动到历史表中。
     *
     * @param executor 表示数据库执行器的 {@link DynamicSqlExecutor}。
     * @param sourceId 表示数据源唯一标识的 {@link String}。
     */
    public static void moveBySource(DynamicSqlExecutor executor, String sourceId) {
        SqlBuilder sql = SqlBuilder.custom().append("INSERT INTO ").appendIdentifier(TABLE_DELETED)
                .append(" SELECT * FROM ").appendIdentifier(TABLE).append(" WHERE ").appendIdentifier(COLUMN_SOURCE_ID)
                .append(" = ?");
        int affectedRows = executor.executeUpdate(sql.toString(), Collections.singletonList(sourceId));
        log.info("Total {} rows affected when move instances to deleted in source. [sourceId={}]",
                affectedRows, sourceId);
    }

    /**
     * 从数据库中删除与指定数据源关联的所有任务实例。
     *
     * @param executor 表示数据库执行器的 {@link DynamicSqlExecutor}。
     * @param sourceId 表示数据源唯一标识的 {@link String}。
     */
    public static void deleteBySource(DynamicSqlExecutor executor, String sourceId) {
        DeleteSql sql = DeleteSql.custom().from(TABLE).where(Condition.expectEqual(COLUMN_SOURCE_ID, sourceId));
        int affectedRows = sql.execute(executor);
        log.info("Total {} task instances deleted in source. [sourceId={}]", affectedRows, sourceId);
    }

    /**
     * 从数据库中选择指定的已删除的任务实例。
     *
     * @param executor 表示数据库执行器的 {@link DynamicSqlExecutor}。
     * @param task 表示任务定义的 {@link TaskEntity}。
     * @param ids 表示任务实例唯一标识的列表的 {@link List}{@code <}{@link String}{@code >}。
     * @return 表示任务实例的列表的 {@link List}{@code <}{@link TaskInstanceRow}{@code >}。
     */
    public static List<TaskInstanceRow> selectDeleted(DynamicSqlExecutor executor, TaskEntity task, List<String> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        SqlBuilder sql = SqlBuilder.custom();
        fillSelectPrefix(sql, task);
        Condition condition = Condition.expectIn(ColumnRef.of(TABLE_ALIAS, COLUMN_ID), ids);
        condition = condition.and(Condition.expectEqual(ColumnRef.of(TABLE_ALIAS, COLUMN_TASK_ID), task.getId()));
        sql.appendIdentifier(TABLE_DELETED).append(" AS ").appendIdentifier(TABLE_ALIAS).append(" WHERE ");
        List<Object> args = new LinkedList<>();
        condition.toSql(sql, args);
        List<Map<String, Object>> rows = executor.executeQuery(sql.toString(), args);
        return convert(task, rows);
    }
}

