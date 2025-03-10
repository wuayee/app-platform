/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.service.impl;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static modelengine.fitframework.util.ObjectUtils.nullIf;

import modelengine.fit.jane.task.domain.PropertyDataType;
import modelengine.fit.jane.task.domain.TaskProperty;
import modelengine.fit.jane.task.util.OperationContext;
import modelengine.fit.jane.task.util.PagedResultSet;
import modelengine.fit.jane.task.util.Pagination;
import modelengine.fit.jane.task.util.PaginationResult;
import modelengine.fit.jober.taskcenter.domain.CategoryEntity;
import modelengine.fit.jober.taskcenter.domain.SourceEntity;
import modelengine.fit.jober.taskcenter.domain.TaskEntity;
import modelengine.fit.jober.taskcenter.domain.TaskInstance;
import modelengine.fit.jober.taskcenter.domain.TaskType;
import modelengine.fit.jober.taskcenter.domain.util.Filter;
import modelengine.fit.jober.taskcenter.domain.util.ListValue;
import modelengine.fit.jober.taskcenter.domain.util.TaskInstanceRow;
import modelengine.fit.jober.taskcenter.service.CategoryService;
import modelengine.fit.jober.taskcenter.service.TagService;
import modelengine.fit.jober.taskcenter.service.TaskAgendaService;
import modelengine.fit.jober.taskcenter.util.DynamicSqlExecutor;
import modelengine.fit.jober.taskcenter.util.sql.ColumnRef;
import modelengine.fit.jober.taskcenter.util.sql.Condition;
import modelengine.fit.jober.taskcenter.util.sql.OrderBy;
import modelengine.fit.jober.taskcenter.util.sql.SqlBuilder;

import modelengine.fit.jober.common.ErrorCodes;
import modelengine.fit.jober.common.exceptions.BadRequestException;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.ArrayList;
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
import java.util.stream.Stream;

/**
 * 功能描述
 *
 * @author 罗书强
 * @since 2024-01-23
 */
@Component
public class TaskAgendaServiceImpl implements TaskAgendaService {
    private static final String COLUMN_ID = "id";

    private static final String COLUMN_TASK_ID = "task_id";

    private static final String TASK_PROPERTY = "task_property";

    private static final String OBJECT_TYPE = "INSTANCE";

    private static final String TABLE_WIDE = "task_instance_wide";

    private static final String TABLE_TASK = "task";

    private static final String COLUMN_TEMPLATE_ID = "template_id";

    private static final String WITH_TABLE = "ins_task";

    private final TagService tagService;

    private final CategoryService categoryService;

    private final DynamicSqlExecutor executor;

    public TaskAgendaServiceImpl(DynamicSqlExecutor executor, TagService tagService, CategoryService categoryService) {
        this.executor = executor;
        this.tagService = tagService;
        this.categoryService = categoryService;
    }

    @Override
    public PagedResultSet<TaskInstance> listAllAgenda(TaskInstance.Filter filter, Pagination pagination,
            String templateId, OperationContext context, List<TaskEntity> taskEntityList, List<OrderBy> orderBys) {
        Map<String, Object> dataTypeSeqMap = new HashMap<>();
        List<String> taskIds = new ArrayList<>(taskEntityList.size());
        taskEntityList.forEach(taskEntity -> {
            taskIds.add(taskEntity.getId());
            List<TaskProperty> properties = taskEntity.getProperties();
            properties.forEach(property -> {
                String dataTypeSeq = property.dataType() + "_" + property.sequence();
                dataTypeSeqMap.put(dataTypeSeq, property.name());
            });
        });
        List<Object> args = new LinkedList<>();
        String selectSql = fillSelectInsPrefix(dataTypeSeqMap, filter, args, taskIds, orderBys, context,
                templateId).toString();
        args.add(pagination.offset());
        args.add(pagination.limit());
        List<Map<String, Object>> row = this.executor.executeQuery(selectSql, args);
        List<TaskInstanceRow> instances = convert(taskEntityList, row);
        fillListableProperties(taskEntityList, instances);
        fillAdditions(instances, context);
        List<TaskInstance> domainObjects = toDomainObjects(taskEntityList, instances);
        // 查询总数
        List<Object> argsCount = new LinkedList<>();
        Long count = ObjectUtils.cast(this.executor.executeScalar(
                selectCountInstances(filter, argsCount, context, templateId).toString(), argsCount));
        return PagedResultSet.create(domainObjects, PaginationResult.create(pagination, count));
    }

    @Override
    public List<String> listTaskIds(TaskInstance.Filter filter, Pagination pagination, String templateId,
            OperationContext context, List<OrderBy> orderBys) {
        if (StringUtils.isEmpty(templateId)) {
            throw new BadRequestException(ErrorCodes.TASK_AGENDA_NO_TEMPLATE_ID);
        }
        // 通过templateId查询task表中的taskId, 跟task_instance_wide表连接获取taskId
        List<Object> args = new LinkedList<>();
        SqlBuilder distinctTaskIdSql = fillWithSql(filter, args, orderBys, context, templateId);
        args.add(pagination.offset());
        args.add(pagination.limit());
        List<Map<String, Object>> rows = this.executor.executeQuery(distinctTaskIdSql.toString(), args);
        return rows.stream().map(values -> String.valueOf(values.get("task_id"))).collect(Collectors.toList());
    }

    private void fillListableProperties(List<TaskEntity> tasks, List<TaskInstanceRow> instancesList) {
        for (TaskEntity task : tasks) {
            List<TaskInstanceRow> instances = instancesList.stream()
                    .filter(instance -> instance.taskId().equals(task.getId()))
                    .collect(toList());
            List<TaskProperty> listableProperties = task.getProperties()
                    .stream()
                    .filter(property -> property.dataType().listable())
                    .collect(toList());
            if (CollectionUtils.isEmpty(listableProperties) || CollectionUtils.isEmpty(instances)) {
                return;
            }
            List<String> instanceIds = instances.stream().map(TaskInstanceRow::id).collect(toList());
            Map<String, List<ListValue>> listValues = listableProperties.stream()
                    .map(property -> property.dataType().tableOfList())
                    .map(table -> ListValue.selectByInstances(executor, table, instanceIds))
                    .flatMap(Collection::stream)
                    .collect(groupingBy(ListValue::instanceId));
            for (TaskInstanceRow instance : instances) {
                Map<String, List<Object>> propertyValues = Optional.ofNullable(listValues.get(instance.id()))
                        .map(Collection::stream)
                        .orElseGet(Stream::empty)
                        .sorted(Comparator.comparingInt(ListValue::index))
                        .collect(groupingBy(ListValue::propertyId, mapping(ListValue::value, toList())));
                for (TaskProperty property : listableProperties) {
                    Map<String, Object> info = new LinkedHashMap<>(instance.info());
                    List<Object> values = nullIf(propertyValues.get(property.id()), Collections.emptyList());
                    info.put(property.name(), values);
                    instance.info(Collections.unmodifiableMap(info));
                }
            }
        }
    }

    private void fillAdditions(List<TaskInstanceRow> rows, OperationContext context) {
        List<String> ids = rows.stream().map(TaskInstanceRow::id).collect(Collectors.toList());
        Map<String, List<String>> categories = this.categoryService.listUsages(OBJECT_TYPE, ids, context);
        for (TaskInstanceRow row : rows) {
            row.categories(nullIf(categories.get(row.id()), Collections.emptyList()));
        }
        Map<String, List<String>> tags = this.tagService.list(OBJECT_TYPE, ids, context);
        for (TaskInstanceRow row : rows) {
            row.tags(nullIf(tags.get(row.id()), Collections.emptyList()));
        }
    }

    private List<TaskInstance> toDomainObjects(List<TaskEntity> tasks, List<TaskInstanceRow> rows) {
        List<TaskInstance> instances = new ArrayList<>(rows.size());
        for (TaskEntity task : tasks) {
            Map<String, SourceEntity> sources = new HashMap<>();
            Map<String, TaskType> types = new HashMap<>();
            TaskType.traverse(task.getTypes(), type -> {
                types.put(type.id(), type);
                type.sources().forEach(source -> sources.put(source.getId(), source));
            });
            for (TaskInstanceRow row : rows) {
                if (task.getId().equals(row.taskId())) {
                    TaskInstance instance = TaskInstance.custom()
                            .id(row.id())
                            .task(task)
                            .type(types.get(row.typeId()))
                            .source(sources.get(row.sourceId()))
                            .tags(row.tags())
                            .info(row.info())
                            .categories(row.categories())
                            .build();
                    instances.add(instance);
                }
            }
        }
        return instances;
    }

    private SqlBuilder fillWithSql(TaskInstance.Filter filter, List<Object> whereArgs,
            List<OrderBy> orderBys, OperationContext context, String templateId) {
        SqlBuilder selectSql = fillSelectPrefix(filter, whereArgs, orderBys, context, templateId);
        SqlBuilder sql = SqlBuilder.custom();
        sql.append("WITH ").appendIdentifier(WITH_TABLE).append(" AS (");
        sql.append(selectSql.toString());
        sql.append(" )");
        sql.append(" SELECT DISTINCT ON (")
                .appendIdentifier(WITH_TABLE)
                .append(".")
                .appendIdentifier(COLUMN_TASK_ID)
                .append(") ")
                .appendIdentifier(WITH_TABLE)
                .append(".")
                .appendIdentifier(COLUMN_TASK_ID)
                .append(" FROM ")
                .appendIdentifier(WITH_TABLE);
        return sql;
    }

    private SqlBuilder fillSelectPrefix(TaskInstance.Filter filter, List<Object> whereArgs,
            List<OrderBy> orderBys, OperationContext context, String templateId) {
        SqlBuilder sql = SqlBuilder.custom();
        sql.append("SELECT ")
                .appendIdentifier("ins")
                .append('.')
                .appendIdentifier(COLUMN_TASK_ID)
                .append(" FROM ")
                .appendIdentifier(TABLE_WIDE)
                .append(" AS ")
                .appendIdentifier("ins");
        Condition instancesCondition = whereInstances(filter).orElse(null);
        Condition categoriesCondition = whereCategories(sql, whereArgs, filter).orElse(null);
        Condition tagsCondition = whereTags(filter, context).orElse(null);
        Condition propertiesCondition = whereProperties(filter.infos(), templateId);
        Condition condition = Condition.and(instancesCondition,
                Condition.and(categoriesCondition, tagsCondition), propertiesCondition);
        selectTaskIdsSql(sql);
        whereArgs.add(templateId);
        if (condition != null) {
            sql.append(" AND ");
            condition.toSql(sql, whereArgs);
        }
        orderBySql(orderBys, sql, templateId);
        sql.append(" OFFSET ? LIMIT ?");
        return sql;
    }

    private void orderBySql(List<OrderBy> orderBys, SqlBuilder sql, String templateId) {
        // 通过模板id获取模板property信息，查询name是宽表列名
        List<Map<String, Object>> rows = templatePropertiesInfo(templateId);
        if (!orderBys.isEmpty()) {
            sql.append(" ORDER BY ");
            for (OrderBy orderBy : orderBys) {
                final String infoPrefix = "info.";
                if (!StringUtils.startsWithIgnoreCase(orderBy.property(), infoPrefix)) {
                    throw new BadRequestException(ErrorCodes.ORDER_BY_PROPERTY_NAME_NOT_SUPPORT);
                }
                Optional<Object> info = rows.stream()
                        .filter(map -> orderBy.property().substring(infoPrefix.length()).equals(map.get("name")))
                        .map(map -> map.get("info"))
                        .findFirst();
                if (!info.isPresent()) {
                    throw new BadRequestException(ErrorCodes.PROPERTY_NOT_EXIST_IN_ORDER_BY_PARAM);
                }
                sql.appendIdentifier("ins")
                        .append('.')
                        .appendIdentifier(ObjectUtils.cast(info.get()))
                        .append(' ')
                        .append(orderBy.order())
                        .append(", ");
            }
            sql.backspace(2);
        }
    }

    private List<Map<String, Object>> templatePropertiesInfo(String templateId) {
        String propertySql = "SELECT name, (LOWER(data_type) || '_' || sequence) AS info "
                + "FROM task_template_property WHERE task_template_id IN (SELECT find_template_parents(?) as id)";
        List<Map<String, Object>> rows = this.executor.executeQuery(propertySql, Collections.singletonList(templateId));
        if (rows.isEmpty()) {
            throw new BadRequestException(ErrorCodes.TASK_AGENDA_NOT_EXIST_IN_TEMPLATE_PARAM);
        }
        return rows;
    }

    private Optional<Condition> whereCategories(SqlBuilder selectSql, List<Object> selectArgs,
            TaskInstance.Filter filter) {
        List<CategoryEntity> categories = categoryService.listByNames(filter.categories());
        if (categories.isEmpty()) {
            return Optional.empty();
        }
        Map<String, List<String>> groupedIds = categories.stream()
                .collect(groupingBy(CategoryEntity::getGroup, mapping(CategoryEntity::getId, toList())));
        Condition condition = null;
        int index = 0;
        for (List<String> categoryIds : groupedIds.values()) {
            index++;
            String alias = "cu" + index;
            selectSql.append(" INNER JOIN ")
                    .appendIdentifier("category_usage")
                    .append(" AS ")
                    .appendIdentifier(alias)
                    .append(" ON ")
                    .appendIdentifier(alias)
                    .append('.')
                    .appendIdentifier("object_id")
                    .append(" = ")
                    .appendIdentifier("ins");
            selectSql.append('.')
                    .appendIdentifier("id")
                    .append(" AND ")
                    .appendIdentifier(alias)
                    .append('.')
                    .appendIdentifier("object_type")
                    .append(" = ?");
            selectArgs.add("INSTANCE");
            condition = Condition.and(condition, Condition.expectIn(ColumnRef.of(alias, "category_id"), categoryIds));
        }
        return Optional.ofNullable(condition);
    }

    private Optional<Condition> whereTags(TaskInstance.Filter filter, OperationContext context) {
        Map<String, String> tags = this.tagService.identify(filter.tags(), context);
        if (tags.isEmpty()) {
            return Optional.empty();
        }
        ColumnRef idColumn = ColumnRef.of("ins", "id");
        Condition condition = Condition.expectIn("tag_id", tags.values());
        SqlBuilder sql = SqlBuilder.custom()
                .append(idColumn)
                .append(" IN (SELECT ")
                .appendIdentifier("object_id")
                .append(" FROM ")
                .appendIdentifier("tag_usage")
                .append(" WHERE ");
        List<Object> args = new ArrayList<>(tags.values().size());
        condition.toSql(sql, args);
        sql.append(')');
        return Optional.of(Condition.of(sql.toString(), args));
    }

    private Optional<Condition> whereInstances(TaskInstance.Filter filter) {
        List<String> instances = filter.ids();
        if (instances.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(Condition.expectIn(ColumnRef.of("ins", COLUMN_ID), instances));
    }

    private SqlBuilder fillSelectInsPrefix(Map<String, Object> dataTypeSeqMap, TaskInstance.Filter filter,
            List<Object> whereArgs, List<String> taskIds, List<OrderBy> orderBys, OperationContext context,
            String templateId) {
        SqlBuilder sql = SqlBuilder.custom();
        sql.append("SELECT ins.id, ins.task_id, ins.source_id, ins.task_type_id");
        for (Map.Entry<String, Object> entry : dataTypeSeqMap.entrySet()) {
            String key = entry.getKey();
            if (key.contains("LIST_TEXT")) {
                continue;
            }
            String alias = "info_" + entry.getValue();
            sql.append(", ").appendIdentifier("ins").append('.').appendIdentifier(StringUtils.toLowerCase(key));
            sql.append(" AS ").appendIdentifier(alias);
        }
        sql.append(" FROM ").appendIdentifier(TABLE_WIDE).append(" AS ").appendIdentifier("ins");
        Condition condition = Condition.expectIn(ColumnRef.of("ins", COLUMN_TASK_ID), taskIds)
            .and(whereCategories(sql, whereArgs, filter).orElse(null))
            .and(whereTags(filter, context).orElse(null))
            .and(whereInstances(filter).orElse(null))
            .and(whereProperties(filter.infos(), templateId));
        sql.append(" WHERE ");
        condition.toSql(sql, whereArgs);
        orderBySql(orderBys, sql, templateId);
        sql.append(" OFFSET ? LIMIT ?");
        return sql;
    }

    private SqlBuilder selectCountInstances(TaskInstance.Filter filter, List<Object> whereArgs,
            OperationContext context, String templateId) {
        SqlBuilder sql = SqlBuilder.custom();
        sql.append("SELECT count(*)");
        sql.append(" FROM ").appendIdentifier(TABLE_WIDE).append(" AS ").appendIdentifier("ins");
        Condition condition = Condition.and(whereCategories(sql, whereArgs, filter).orElse(null),
                Condition.and(whereInstances(filter).orElse(null), whereTags(filter, context).orElse(null)),
                whereProperties(filter.infos(), templateId));
        whereArgs.add(templateId);
        selectTaskIdsSql(sql);
        if (condition != null) {
            sql.append(" AND ");
            condition.toSql(sql, whereArgs);
        }
        return sql;
    }

    private static void selectTaskIdsSql(SqlBuilder sql) {
        sql.append(" WHERE ")
                .appendIdentifier("ins")
                .append('.')
                .appendIdentifier(COLUMN_TASK_ID)
                .append(" IN (SELECT ")
                .appendIdentifier(TABLE_TASK)
                .append('.')
                .appendIdentifier(COLUMN_ID)
                .append(" FROM ")
                .appendIdentifier(TABLE_TASK)
                .append(" WHERE ")
                .appendIdentifier(TABLE_TASK)
                .append('.')
                .appendIdentifier(COLUMN_TEMPLATE_ID)
                .append(" IN (SELECT find_template_children(?) AS id))");
    }

    private Condition whereProperties(Map<String, List<String>> infos, String templateId) {
        Condition unindexedCondition = null;
        List<Map<String, Object>> rows = templatePropertiesInfo(templateId);
        Map<Object, Object> properties = rows.stream()
                .collect(Collectors.toMap(map -> map.get("name"), map -> map.get("info")));
        for (Map.Entry<String, List<String>> entry : infos.entrySet()) {
            String key = entry.getKey();
            List<String> values = entry.getValue();
            String column = StringUtils.toLowerCase(ObjectUtils.cast(properties.get(key)));
            if (StringUtils.isEmpty(column)) {
                throw new BadRequestException(ErrorCodes.TASK_AGENDA_NOT_EXIST_IN_FILTER_PARAM);
            }
            if (column.contains("list_text")) {
                unindexedCondition = Condition.and(unindexedCondition,
                        createUnindexedListCondition(values, templateId));
            } else {
                unindexedCondition = Condition.and(unindexedCondition, createUnindexedScalarCondition(values, column));
            }
        }
        return unindexedCondition;
    }

    private static Condition createUnindexedScalarCondition(List<String> values, String column) {
        return values.stream()
                .map(value -> Filter.parser().parse(PropertyDataType.TEXT, value))
                .map(name -> name.toCondition(ColumnRef.of(StringUtils.toLowerCase(column))))
                .reduce(null, (condition1, condition2) -> Condition.or(condition1, condition2));
    }

    private Condition createUnindexedListCondition(List<String> values, String templateId) {
        ColumnRef column = ColumnRef.of(TaskInstanceRow.TABLE_ALIAS, TaskInstanceRow.COLUMN_ID);
        SqlBuilder conditionSql = SqlBuilder.custom().append(column).append(" IN (");
        conditionSql.append("SELECT DISTINCT ")
                .appendIdentifier(ListValue.COLUMN_INSTANCE_ID)
                .append(" FROM ")
                .appendIdentifier(PropertyDataType.LIST_TEXT.tableOfList())
                .append(" WHERE ")
                .appendIdentifier(ListValue.COLUMN_PROPERTY_ID)
                .append(" IN (SELECT ")
                .appendIdentifier(COLUMN_ID)
                .append(" FROM ")
                .appendIdentifier(TASK_PROPERTY)
                .append(" WHERE ")
                .appendIdentifier(COLUMN_TASK_ID)
                .append(" IN (SELECT ")
                .appendIdentifier(COLUMN_ID)
                .append(" FROM ")
                .appendIdentifier(TABLE_TASK)
                .append(" WHERE ")
                .appendIdentifier(COLUMN_TEMPLATE_ID)
                .append(" IN (SELECT find_template_children(?) AS id))) ");
        ColumnRef valueColumn = ColumnRef.of(ListValue.COLUMN_VALUE);
        List<Object> conditionArgs = new LinkedList<>();
        conditionArgs.add(templateId);
        Condition condition = values.stream()
                .map(value -> Filter.parser().parse(PropertyDataType.LIST_TEXT.elementType(), value))
                .map(filter -> filter.toCondition(valueColumn))
                .reduce(null, (c1, c2) -> Condition.or(c1, c2));
        if (condition != null) {
            conditionSql.append(" AND ");
            condition.toSql(conditionSql, conditionArgs);
        }
        conditionSql.append(')');
        return Condition.of(conditionSql.toString(), conditionArgs);
    }

    private List<TaskInstanceRow> convert(List<TaskEntity> tasks, List<Map<String, Object>> rows) {
        return rows.stream().map(row -> {
            TaskEntity taskEntity =
                    tasks.stream().filter(task -> task.getId().equals(row.get("task_id"))).findFirst().orElse(null);
            return convert(taskEntity, row);
        }).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
    }

    private Optional<TaskInstanceRow> convert(TaskEntity task, Map<String, Object> row) {
        if (task == null) {
            return Optional.empty();
        }
        Map<String, Object> actual = new HashMap<>();
        Map<String, Object> info = new HashMap<>();
        for (Map.Entry<String, Object> entry : row.entrySet()) {
            String key = entry.getKey();
            if (StringUtils.startsWithIgnoreCase(key, "info_")) {
                key = key.substring("info_".length());
                TaskProperty property = task.getPropertyByName(key);
                if (property == null) {
                    continue;
                }
                PropertyDataType propertyDataType = property.dataType();
                Object value = propertyDataType.fromPersistence(entry.getValue());
                info.put(key, value);
            } else {
                actual.put(key, entry.getValue());
            }
        }
        actual.put("info", info);
        return Optional.of(new TaskInstanceRow(actual));
    }
}
