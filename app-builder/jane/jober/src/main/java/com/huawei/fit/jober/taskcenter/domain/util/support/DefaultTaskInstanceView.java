/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.domain.util.support;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

import com.huawei.fit.jane.task.domain.TaskProperty;
import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.exceptions.BadRequestException;
import com.huawei.fit.jober.taskcenter.domain.CategoryEntity;
import com.huawei.fit.jober.taskcenter.domain.TaskEntity;
import com.huawei.fit.jober.taskcenter.domain.TaskInstance;
import com.huawei.fit.jober.taskcenter.domain.util.Filter;
import com.huawei.fit.jober.taskcenter.domain.util.ListValue;
import com.huawei.fit.jober.taskcenter.domain.util.TaskInstanceRow;
import com.huawei.fit.jober.taskcenter.domain.util.TaskInstanceView;
import com.huawei.fit.jober.taskcenter.service.CategoryService;
import com.huawei.fit.jober.taskcenter.service.TagService;
import com.huawei.fit.jober.taskcenter.util.ExecutableSql;
import com.huawei.fit.jober.taskcenter.util.sql.ColumnRef;
import com.huawei.fit.jober.taskcenter.util.sql.Condition;
import com.huawei.fit.jober.taskcenter.util.sql.SqlBuilder;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

/**
 * 为 {@link TaskInstanceView} 提供默认实现。
 *
 * @author 梁济时 l00815032
 * @since 2024-01-27
 */
public class DefaultTaskInstanceView implements TaskInstanceView {
    private static final Logger log = Logger.get(DefaultTaskInstanceView.class);

    private final CategoryService categoryService;

    private final TagService tagService;

    private final TaskEntity task;

    private final TaskInstance.Filter filter;

    private final OperationContext context;

    private ExecutableSql executableSql;

    public DefaultTaskInstanceView(CategoryService categoryService, TagService tagService,
            TaskEntity task, TaskInstance.Filter filter, OperationContext context) {
        this.categoryService = categoryService;
        this.tagService = tagService;
        this.task = task;
        this.filter = filter;
        this.context = context;
    }

    private ExecutableSql buildSql() {
        SqlBuilder sql = SqlBuilder.custom();
        List<Object> args = new LinkedList<>();
        TaskInstanceRow.fillSelectPrefix(sql, this.task);
        String table = this.filter.deleted() ? TaskInstanceRow.TABLE_DELETED : TaskInstanceRow.TABLE;
        sql.appendIdentifier(table).append(" AS ").appendIdentifier(TaskInstanceRow.TABLE_ALIAS);

        Condition condition = Condition.expectEqual(ColumnRef.of(TaskInstanceRow.TABLE_ALIAS,
                TaskInstanceRow.COLUMN_TASK_ID), this.task.getId())
                .and(whereColumnIn(TaskInstanceRow.COLUMN_ID, this.filter.ids()))
                .and(whereColumnIn(TaskInstanceRow.COLUMN_TYPE_ID, this.filter.typeIds()))
                .and(whereColumnIn(TaskInstanceRow.COLUMN_SOURCE_ID, this.filter.sourceIds()))
                .and(this.whereCategories(sql, args))
                .and(this.whereTags())
                .and(this.whereProperties(sql, args));

        sql.append(" WHERE ");
        condition.toSql(sql, args);
        return ExecutableSql.create(sql.toString(), args);
    }

    private static Condition whereColumnIn(String column, List<String> values) {
        List<String> actual = Optional.ofNullable(values).map(Collection::stream).orElseGet(Stream::empty)
                .map(StringUtils::trim).filter(StringUtils::isNotEmpty).distinct().collect(toList());
        if (actual.isEmpty()) {
            return null;
        } else {
            return Condition.expectIn(ColumnRef.of(TaskInstanceRow.TABLE_ALIAS, column), values);
        }
    }

    private Condition whereCategories(SqlBuilder selectSql, List<Object> selectArgs) {
        List<CategoryEntity> categories = this.categoryService.listByNames(this.filter.categories());
        if (categories.isEmpty()) {
            return null;
        }
        Map<String, List<String>> groupedIds = categories.stream().collect(groupingBy(
                CategoryEntity::getGroup, mapping(CategoryEntity::getId, toList())));
        int index = 0;
        Condition condition = null;
        for (List<String> categoryIds : groupedIds.values()) {
            index++;
            String alias = "cu" + index;
            selectSql.append(" INNER JOIN ").appendIdentifier("category_usage").append(" AS ")
                    .appendIdentifier(alias).append(" ON ").appendIdentifier(alias).append('.')
                    .appendIdentifier("object_id").append(" = ").appendIdentifier(TaskInstanceRow.TABLE_ALIAS)
                    .append('.').appendIdentifier(TaskInstanceRow.COLUMN_ID).append(" AND ").appendIdentifier(alias)
                    .append('.').appendIdentifier("object_type").append(" = ?");
            selectArgs.add(TaskInstanceRow.OBJECT_TYPE);
            condition = Condition.and(condition, Condition.expectIn(ColumnRef.of(alias, "category_id"), categoryIds));
        }
        return condition;
    }

    private Condition whereTags() {
        Map<String, String> tags = this.tagService.identify(this.filter.tags(), this.context);
        if (tags.isEmpty()) {
            return null;
        }
        ColumnRef idColumn = ColumnRef.of(TaskInstanceRow.TABLE_ALIAS, TaskInstanceRow.COLUMN_ID);
        SqlBuilder sql = SqlBuilder.custom().append(idColumn).append(" IN (SELECT ").appendIdentifier("object_id")
                .append(" FROM ").appendIdentifier("tag_usage").append(" WHERE ");
        List<Object> args = new ArrayList<>(tags.values().size());
        Condition condition = Condition.expectIn("tag_id", tags.values());
        condition.toSql(sql, args);
        sql.append(')');
        return Condition.of(sql.toString(), args);
    }

    private Condition whereProperties(SqlBuilder selectSql, List<Object> selectArgs) {
        Condition indexedCondition = null;
        Condition unindexedCondition = null;
        for (Map.Entry<String, List<String>> entry : this.filter.infos().entrySet()) {
            String propertyName = entry.getKey();
            List<String> propertyValues = entry.getValue();
            TaskProperty property = this.task.getPropertyByName(propertyName);
            if (property == null) {
                log.error("Unknown property occurs. [task={}, property={}]", this.task.getName(), propertyName);
                throw new BadRequestException(ErrorCodes.TASK_PROPERTY_NOT_FOUND);
            }
            AtomicBoolean indexed = new AtomicBoolean();
            Condition condition = this.conditionOf(selectSql, selectArgs, property, propertyValues, indexed);
            if (indexed.get()) {
                indexedCondition = Condition.and(indexedCondition, condition);
            } else {
                unindexedCondition = Condition.and(unindexedCondition, condition);
            }
        }
        return Condition.and(indexedCondition, unindexedCondition);
    }

    private Condition conditionOf(SqlBuilder sql, List<Object> args, TaskProperty property, List<String> values,
            AtomicBoolean outIndexed) {
        if (this.task.isPropertyIndexed(property.name())) {
            if (property.dataType().listable()) {
                return this.createIndexedListCondition(property, values, outIndexed);
            } else {
                return this.createIndexedScalarCondition(sql, args, property, values, outIndexed);
            }
        } else {
            if (property.dataType().listable()) {
                return this.createUnindexedListCondition(property, values);
            } else {
                return this.createUnindexedScalarCondition(property, values);
            }
        }
    }

    private Condition createIndexedScalarCondition(SqlBuilder sql, List<Object> args,
            TaskProperty property, List<String> values, AtomicBoolean indexable) {
        Condition condition = null;
        indexable.set(true);
        String alias = TaskInstanceRow.INFO_PREFIX + property.name() + "_index";
        for (String value : values) {
            Filter current = Filter.parser().parse(property.dataType(), value);
            if (!current.indexable()) {
                indexable.set(false);
                break;
            }
            condition = Condition.or(condition, current.toCondition(ColumnRef.of(alias, "value")));
        }
        if (!indexable.get()) {
            return this.createUnindexedScalarCondition(property, values);
        }
        sql.append(" INNER JOIN ").appendIdentifier(property.dataType().tableOfIndex()).append(" AS ")
                .appendIdentifier(alias).append(" ON ").appendIdentifier(alias).append('.')
                .appendIdentifier("instance_id").append(" = ").appendIdentifier(TaskInstanceRow.TABLE_ALIAS)
                .append('.').appendIdentifier(TaskInstanceRow.COLUMN_ID).append(" AND ").appendIdentifier(alias)
                .append('.').appendIdentifier("property_id").append(" = ?");
        args.add(property.id());
        return condition;
    }

    private Condition createUnindexedScalarCondition(TaskProperty property, List<String> values) {
        ColumnRef column = ColumnRef.of(TaskInstanceRow.TABLE_ALIAS, property.column());
        return values.stream()
                .map(value -> Filter.parser().parse(property.dataType(), value))
                .map(filter -> filter.toCondition(column))
                .reduce(null, (condition1, condition2) -> Condition.or(condition1, condition2));
    }

    private Condition createIndexedListCondition(TaskProperty property, List<String> values, AtomicBoolean indexable) {
        Condition condition = null;
        ColumnRef valueColumn = ColumnRef.of("value");
        indexable.set(true);
        for (String value : values) {
            Filter parsedFilter = Filter.parser().parse(property.dataType().elementType(), value);
            if (!parsedFilter.indexable()) {
                indexable.set(false);
                break;
            }
            condition = Condition.or(condition, parsedFilter.toCondition(valueColumn));
        }
        if (!indexable.get()) {
            return this.createUnindexedListCondition(property, values);
        }
        condition = Condition.and(Condition.expectEqual("property_id", property.id()), condition);
        SqlBuilder sql = SqlBuilder.custom();
        List<Object> args = new ArrayList<>(values.size() + 1);
        sql.append(ColumnRef.of(TaskInstanceRow.TABLE_ALIAS, TaskInstanceRow.COLUMN_ID))
                .append(" IN (SELECT DISTINCT ").appendIdentifier("instance_id").append(" FROM ")
                .appendIdentifier(property.dataType().tableOfIndex()).append(" WHERE ");
        condition.toSql(sql, args);
        sql.append(')');
        return Condition.of(sql.toString(), args);
    }

    private Condition createUnindexedListCondition(TaskProperty property, List<String> values) {
        ColumnRef column = ColumnRef.of(TaskInstanceRow.TABLE_ALIAS, TaskInstanceRow.COLUMN_ID);
        SqlBuilder conditionSql = SqlBuilder.custom().append(column).append(" IN (");
        List<Object> conditionArgs = new LinkedList<>();
        conditionSql.append("SELECT DISTINCT ").appendIdentifier(ListValue.COLUMN_INSTANCE_ID).append(" FROM ")
                .appendIdentifier(property.dataType().tableOfList()).append(" WHERE ");
        ColumnRef valueColumn = ColumnRef.of(ListValue.COLUMN_VALUE);
        Condition condition = values.stream()
                .map(value -> Filter.parser().parse(property.dataType().elementType(), value))
                .map(filter -> filter.toCondition(valueColumn))
                .reduce(null, (c1, c2) -> Condition.or(c1, c2));
        condition = Condition.expectEqual(ListValue.COLUMN_PROPERTY_ID, property.id()).and(condition);
        condition.toSql(conditionSql, conditionArgs);
        conditionSql.append(')');
        return Condition.of(conditionSql.toString(), conditionArgs);
    }

    @Override
    public ExecutableSql sql() {
        if (this.executableSql == null) {
            this.executableSql = this.buildSql();
        }
        return this.executableSql;
    }
}
