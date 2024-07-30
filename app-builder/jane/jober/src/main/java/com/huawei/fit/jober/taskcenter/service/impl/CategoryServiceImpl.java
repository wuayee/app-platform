/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.service.impl;

import static com.huawei.fitframework.util.ObjectUtils.cast;
import static com.huawei.fitframework.util.ObjectUtils.nullIf;

import com.huawei.fit.jane.task.domain.PropertyCategory;
import com.huawei.fit.jane.task.domain.PropertyCategoryDeclaration;
import com.huawei.fit.jane.task.util.Dates;
import com.huawei.fit.jane.task.util.Entities;
import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jane.task.util.UndefinableValue;
import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.ServerInternalException;
import com.huawei.fit.jober.common.exceptions.BadRequestException;
import com.huawei.fit.jober.taskcenter.domain.CategoryEntity;
import com.huawei.fit.jober.taskcenter.service.CategoryService;
import com.huawei.fit.jober.taskcenter.util.DynamicSqlExecutor;
import com.huawei.fit.jober.taskcenter.util.Maps;
import com.huawei.fit.jober.taskcenter.util.Sqls;
import com.huawei.fit.jober.taskcenter.util.sql.Column;
import com.huawei.fit.jober.taskcenter.util.sql.ColumnRef;
import com.huawei.fit.jober.taskcenter.util.sql.Condition;
import com.huawei.fit.jober.taskcenter.util.sql.DeleteSql;
import com.huawei.fit.jober.taskcenter.util.sql.InsertSql;
import com.huawei.fit.jober.taskcenter.util.sql.SqlBuilder;
import com.huawei.fit.jober.taskcenter.util.sql.Table;
import com.huawei.fit.jober.taskcenter.util.sql.UpdateSql;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.transaction.Transactional;
import com.huawei.fitframework.util.CollectionUtils;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.StringUtils;

import lombok.RequiredArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 为 {@link CategoryService} 提供实现。
 *
 * @author 梁济时 l00815032
 * @since 2023-08-18
 */
@Component
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private static final Logger log = Logger.get(CategoryServiceImpl.class);

    private static final String SQL_MODULE = "category";

    private final DynamicSqlExecutor executor;

    private static CategoryEntity readCategoryEntity(Map<String, Object> row) {
        CategoryEntity category = new CategoryEntity();
        category.setId(ObjectUtils.cast(row.get("id")));
        category.setName(ObjectUtils.cast(row.get("category")));
        category.setGroup(ObjectUtils.cast(row.get("group")));
        return category;
    }

    private static CategoryEntity toEntity(CategoryRow row) {
        CategoryEntity entity = new CategoryEntity();
        entity.setId(row.id());
        entity.setName(row.name());
        entity.setGroup(row.groupName());
        return entity;
    }

    private static PropertyCategory toEntity(Map<String, Object> row) {
        PropertyCategory entity = new PropertyCategory();
        entity.setValue(ObjectUtils.cast(row.get("property_value")));
        entity.setCategory(ObjectUtils.cast(row.get("category")));
        return entity;
    }

    private static Map<String, List<PropertyCategory>> declare(
            Map<String, List<PropertyCategoryDeclaration>> declarations) {
        Map<String, List<PropertyCategory>> map = new HashMap<>(declarations.size());
        for (Map.Entry<String, List<PropertyCategoryDeclaration>> entry : declarations.entrySet()) {
            List<PropertyCategoryDeclaration> current = nullIf(entry.getValue(), Collections.emptyList());
            List<PropertyCategory> categories = new ArrayList<>(current.size());
            map.put(entry.getKey(), categories);
            for (PropertyCategoryDeclaration declaration : current) {
                PropertyCategory category = new PropertyCategory();
                category.setCategory(UndefinableValue.require(declaration.getCategory(),
                        () -> new BadRequestException(ErrorCodes.PROPERTY_CATEGORY_REQUIRED)));
                category.setValue(UndefinableValue.require(declaration.getValue(),
                        () -> new BadRequestException(ErrorCodes.PROPERTY_CATEGORY_VALUE_REQUIRED)));
                categories.add(category);
            }
        }
        return map;
    }

    @Override
    @Transactional
    public Map<String, List<PropertyCategory>> matchers(List<String> propertyIds) {
        List<String> actualPropertyIds = nullIf(propertyIds, Collections.emptyList());
        actualPropertyIds = actualPropertyIds.stream().filter(Entities::isId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(actualPropertyIds)) {
            return Collections.emptyMap();
        }
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT \"cm\".\"property_id\", \"cm\".\"value\" AS \"property_value\", "
                + "\"c\".\"name\" AS \"category\" FROM \"category\" AS \"c\" INNER JOIN \"category_matcher\" AS "
                + "\"cm\" ON \"cm\".\"category_id\" = \"c\".\"id\" WHERE ");
        Sqls.in(sql, "\"cm\".\"property_id\"", propertyIds.size());
        List<Object> args = new ArrayList<>(propertyIds.size());
        args.addAll(propertyIds);
        List<Map<String, Object>> rows = this.executor.executeQuery(sql.toString(), args);
        return rows.stream()
                .collect(Collectors.groupingBy(row -> ObjectUtils.cast(row.get("property_id")),
                        Collectors.mapping(CategoryServiceImpl::toEntity, Collectors.toList())));
    }

    @Override
    @Transactional
    public Map<String, List<PropertyCategory>> saveMatchers(
            Map<String, List<PropertyCategoryDeclaration>> declarations) {
        if (declarations == null || declarations.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, List<PropertyCategory>> categories = declare(declarations);
        List<String> categoryNames = categories.values()
                .stream()
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .map(PropertyCategory::getCategory)
                .distinct()
                .collect(Collectors.toList());
        List<CategoryEntity> items = this.listByNames(categoryNames);
        Map<String, CategoryEntity> nameItems = items.stream()
                .collect(Collectors.toMap(CategoryEntity::getName, Function.identity()));
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO category_matcher(\"id\", \"category_id\", \"property_id\", \"value\") VALUES");
        List<Object> args = new LinkedList<>();
        for (Map.Entry<String, List<PropertyCategory>> entry : categories.entrySet()) {
            String propertyId = entry.getKey();
            for (PropertyCategory category : entry.getValue()) {
                String propertyValue = category.getValue();
                String categoryId = nameItems.get(category.getCategory()).getId();
                sql.append("(?, ?, ?, ?), ");
                args.addAll(Arrays.asList(Entities.generateId(), categoryId, propertyId, propertyValue));
            }
        }
        StringBuilder deleteSql = new StringBuilder();
        deleteSql.append("DELETE FROM \"category_matcher\" WHERE ");
        Sqls.in(deleteSql, "\"property_id\"", categories.size());
        List<Object> deleteArgs = new ArrayList<>(declarations.keySet());
        List<String> ids;
        if (!args.isEmpty()) {
            sql.setLength(sql.length() - 2);
            sql.append(" ON CONFLICT (\"category_id\", \"property_id\", \"value\") "
                    + "DO UPDATE SET \"property_id\" = EXCLUDED.\"property_id\" RETURNING id");
            List<Map<String, Object>> rows = this.executor.executeQuery(sql.toString(), args);
            ids = rows.stream().map(row -> ObjectUtils.<String>cast(row.get("id"))).collect(Collectors.toList());
            Sqls.andNotIn(deleteSql, "\"id\"", ids.size());
            deleteArgs.addAll(ids);
        }
        this.executor.executeUpdate(deleteSql.toString(), deleteArgs);
        return categories;
    }

    @Override
    @Transactional
    public void saveUsages(String objectType, String objectId, List<String> categories, OperationContext context) {
        List<String> actualCategories = canonicalize(categories);
        Map<String, CategoryRow> rows = CategoryRow.all(this.executor);
        List<CategoryRow> knownCategories = actualCategories.stream().map(rows::get).filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (actualCategories.size() > knownCategories.size()) {
            List<String> known = knownCategories.stream().map(CategoryRow::name).collect(Collectors.toList());
            Set<String> unknown = CollectionUtils.difference(actualCategories, known);
            log.error("Unknown categories to save. [values={}]", unknown);
            throw new BadRequestException(ErrorCodes.UNKNOWN_CATEGORY);
        }
        List<CategoryUsageRow> usageRows = CategoryUsageRow.selectByObject(this.executor, objectType, objectId);
        Map<String, CategoryUsageRow> usages = usageRows.stream().collect(Collectors.toMap(CategoryUsageRow::groupId,
                Function.identity(), Maps.throwingMerger(), HashMap::new));
        List<CategoryUsageRow> newRows = new LinkedList<>();
        for (CategoryRow row : knownCategories) {
            CategoryUsageRow usage = usages.remove(row.groupId());
            if (usage == null) {
                CategoryUsageRow newRow = new CategoryUsageRow();
                newRow.id(Entities.generateId());
                newRow.objectType(objectType);
                newRow.objectId(objectId);
                newRow.groupId(row.groupId());
                newRow.categoryId(row.id());
                newRow.creator(context.operator());
                newRow.creationTime(Dates.toUtc(LocalDateTime.now()));
                newRow.lastModifier(newRow.creator());
                newRow.lastModificationTime(newRow.creationTime());
                newRows.add(newRow);
            } else {
                int affectedRows = UpdateSql.custom().table(CategoryUsageRow.TABLE)
                        .set(CategoryUsageRow.COLUMN_CATEGORY_ID, row.id())
                        .set(CategoryUsageRow.COLUMN_LAST_MODIFIER, context.operator())
                        .set(CategoryUsageRow.COLUMN_LAST_MODIFICATION_TIME, Dates.toUtc(LocalDateTime.now()))
                        .where(Condition.expectEqual(CategoryUsageRow.COLUMN_ID, usage.id()))
                        .execute(this.executor);
                if (affectedRows < 1) {
                    throw new ServerInternalException(StringUtils.format(
                            "Failed to update usage of category into database. [id={0}]", usage.id()));
                }
            }
        }
        CategoryUsageRow.insert(this.executor, newRows);
        if (!usages.isEmpty()) {
            List<String> categoryIds = canonicalize(usages.values().stream().map(CategoryUsageRow::categoryId));
            log.info("Delete redundant usages of object. [objectType={}, objectId={}, categoryIds=[{}]]",
                    objectType, objectId, String.join(", ", categoryIds));
            List<String> usageIds = usages.values().stream().map(CategoryUsageRow::id).collect(Collectors.toList());
            CategoryUsageRow.delete(this.executor, usageIds);
        }
    }

    @Override
    @Transactional
    public List<String> listUsages(String objectType, String objectId, OperationContext context) {
        Map<String, List<String>> usages = this.listUsages(objectType, Collections.singletonList(objectId), context);
        return nullIf(usages.get(objectId), Collections.emptyList());
    }

    @Override
    @Transactional
    public Map<String, List<String>> listUsages(String objectType, List<String> objectIds, OperationContext context) {
        List<String> actualObjectIds = Optional.ofNullable(objectIds).map(Collection::stream).orElseGet(Stream::empty)
                .map(Entities::canonicalizeId).filter(Entities::isId).collect(Collectors.toList());
        List<CategoryUsageRow> rows = CategoryUsageRow.selectByObject(this.executor, objectType, actualObjectIds);
        if (rows.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, String> categories = CategoryRow.all(this.executor).values().stream().collect(
                Collectors.toMap(CategoryRow::id, CategoryRow::name));
        return rows.stream().collect(Collectors.groupingBy(CategoryUsageRow::objectId,
                Collectors.mapping(row -> categories.get(row.categoryId()), Collectors.toList())));
    }

    @Override
    @Transactional
    public void deleteByProperty(String propertyId) {
        this.executor.executeUpdate("DELETE FROM \"category_matcher\" WHERE \"property_id\" = ?",
                Collections.singletonList(propertyId));
    }

    @Override
    @Transactional
    public void deleteByTaskIds(Collection<String> taskIds) {
        StringBuilder sql = new StringBuilder();
        sql.append("DELETE FROM \"category_matcher\" AS \"cm\" USING \"task_property\" AS \"tp\" "
                + "WHERE \"cm\".\"property_id\" = \"tp\".\"id\" AND ");
        Sqls.in(sql, "\"tp\".\"task_id\"", taskIds.size());
        List<Object> args = new ArrayList<>(taskIds);
        this.executor.executeUpdate(sql.toString(), args);
    }

    @Override
    public List<CategoryEntity> listByNames(Collection<String> categoryNames) {
        Set<String> names = Optional.ofNullable(categoryNames)
                .map(Collection::stream)
                .orElseGet(Stream::empty)
                .map(StringUtils::trim)
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.toCollection(HashSet::new));
        if (names.isEmpty()) {
            return Collections.emptyList();
        }
        SqlBuilder sql = SqlBuilder.custom();
        sql.append(Sqls.script(SQL_MODULE, "select")).append("WHERE ");
        sql.append(ColumnRef.of("c", "name")).append(" IN (").appendRepeatedly("?, ", names.size())
                .backspace(2).append(')');
        List<Object> args = new ArrayList<>(names);
        List<Map<String, Object>> rows = this.executor.executeQuery(sql.toString(), args);
        List<CategoryEntity> categories = rows.stream()
                .map(CategoryServiceImpl::readCategoryEntity)
                .collect(Collectors.toList());
        Set<String> actualNames = categories.stream().map(CategoryEntity::getName).collect(Collectors.toSet());
        names.removeAll(actualNames);
        if (!names.isEmpty()) {
            throw new BadRequestException(ErrorCodes.UNKNOWN_CATEGORY, String.join(", ", names));
        } else {
            return categories;
        }
    }

    @Override
    public List<CategoryEntity> listByIds(Collection<String> categoryIds) {
        Set<String> ids = Optional.ofNullable(categoryIds)
                .map(Collection::stream)
                .orElseGet(Stream::empty)
                .map(StringUtils::trim)
                .filter(Entities::isId)
                .collect(Collectors.toSet());
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }
        return CategoryRow.all(this.executor).values().stream()
                .filter(row -> ids.contains(row.id()))
                .map(CategoryServiceImpl::toEntity)
                .sorted(Comparator.comparing(CategoryEntity::getName))
                .collect(Collectors.toList());
    }

    private static List<String> canonicalize(Stream<String> stream) {
        return stream.map(StringUtils::trim).filter(StringUtils::isNotEmpty).collect(Collectors.toList());
    }

    private static List<String> canonicalize(Collection<String> values) {
        return canonicalize(Optional.ofNullable(values).map(Collection::stream).orElseGet(Stream::empty));
    }

    private abstract static class AbstractRow {
        private final Map<String, Object> values;

        AbstractRow(Map<String, Object> values) {
            this.values = values;
        }

        /**
         * 获取指定键的值。
         *
         * @param key 键
         * @return 值
         */
        protected Object get(String key) {
            return this.values.get(key);
        }

        /**
         * 设置指定键的值。
         *
         * @param key 键
         * @param value 值
         */
        protected void set(String key, Object value) {
            this.values.put(key, value);
        }

        /**
         * 获取指定键的字符串值。
         *
         * @param key 键
         * @return 字符串值
         */
        protected String stringValue(String key) {
            return cast(this.get(key));
        }

        /**
         * 设置指定键的字符串值。
         *
         * @param key 键
         * @param value 字符串值
         */
        protected void stringValue(String key, String value) {
            this.set(key, value);
        }

        /**
         * 获取指定键的日期时间值。
         *
         * @param key 键
         * @return 日期时间值
         */
        protected LocalDateTime datetimeValue(String key) {
            Object value = this.get(key);
            if (value instanceof Timestamp) {
                value = ((Timestamp) value).toLocalDateTime();
                this.set(key, value);
            }
            return cast(value);
        }

        /**
         * 设置指定键的日期时间值。
         *
         * @param key 键
         * @param value 日期时间值
         */
        protected void datetimeValue(String key, LocalDateTime value) {
            this.set(key, value);
        }
    }

    private static class CategoryRow extends AbstractRow {
        private static volatile Map<String, CategoryRow> allRows;

        private static final Object MONITOR = new byte[0];

        CategoryRow(Map<String, Object> values) {
            super(values);
        }

        String id() {
            return this.stringValue("id");
        }

        String name() {
            return this.stringValue("name");
        }

        String groupId() {
            return this.stringValue("group_id");
        }

        String groupName() {
            return this.stringValue("group_name");
        }

        static Map<String, CategoryRow> all(DynamicSqlExecutor executor) {
            if (allRows != null) {
                return allRows;
            }
            synchronized (MONITOR) {
                if (allRows != null) {
                    return allRows;
                }
                SqlBuilder sql = SqlBuilder.custom().append("SELECT ");
                sql.append(Column.of("c", "id", "id")).append(", ");
                sql.append(Column.of("c", "name", "name")).append(", ");
                sql.append(Column.of("cg", "id", "group_id")).append(", ");
                sql.append(Column.of("cg", "name", "group_name")).append(" FROM ");
                sql.append(Table.of("category", "c"));
                sql.append(" INNER JOIN ").append(Table.of("category_group", "cg"));
                sql.append(" ON ").append(Column.of("cg", "id", null));
                sql.append(" = ").append(Column.of("c", "category_group_id", null));
                List<Map<String, Object>> rows = executor.executeQuery(sql.toString());
                allRows = rows.stream()
                        .map(CategoryRow::new)
                        .collect(Collectors.toMap(CategoryRow::name, Function.identity()));
            }
            return allRows;
        }
    }

    private static class CategoryUsageRow extends AbstractRow {
        static final String TABLE = "category_usage";

        static final String COLUMN_ID = "id";

        static final String COLUMN_OBJECT_TYPE = "object_type";

        static final String COLUMN_OBJECT_ID = "object_id";

        static final String COLUMN_CATEGORY_GROUP_ID = "category_group_id";

        static final String COLUMN_CATEGORY_ID = "category_id";

        static final String COLUMN_CREATOR = "created_by";

        static final String COLUMN_CREATION_TIME = "created_at";

        static final String COLUMN_LAST_MODIFIER = "updated_by";

        static final String COLUMN_LAST_MODIFICATION_TIME = "updated_at";

        CategoryUsageRow() {
            this(null);
        }

        CategoryUsageRow(Map<String, Object> values) {
            super(nullIf(values, new HashMap<>(9)));
        }

        String id() {
            return this.stringValue(COLUMN_ID);
        }

        void id(String id) {
            this.stringValue(COLUMN_ID, id);
        }

        String objectType() {
            return this.stringValue(COLUMN_OBJECT_TYPE);
        }

        void objectType(String objectType) {
            this.stringValue(COLUMN_OBJECT_TYPE, objectType);
        }

        String objectId() {
            return this.stringValue(COLUMN_OBJECT_ID);
        }

        void objectId(String objectId) {
            this.stringValue(COLUMN_OBJECT_ID, objectId);
        }

        String groupId() {
            return this.stringValue(COLUMN_CATEGORY_GROUP_ID);
        }

        void groupId(String groupId) {
            this.stringValue(COLUMN_CATEGORY_GROUP_ID, groupId);
        }

        String categoryId() {
            return this.stringValue(COLUMN_CATEGORY_ID);
        }

        void categoryId(String categoryId) {
            this.stringValue(COLUMN_CATEGORY_ID, categoryId);
        }

        String creator() {
            return cast(this.stringValue(COLUMN_CREATOR));
        }

        void creator(String creator) {
            this.stringValue(COLUMN_CREATOR, creator);
        }

        LocalDateTime creationTime() {
            return this.datetimeValue(COLUMN_CREATION_TIME);
        }

        void creationTime(LocalDateTime creationTime) {
            this.datetimeValue(COLUMN_CREATION_TIME, creationTime);
        }

        String lastModifier() {
            return cast(this.stringValue(COLUMN_LAST_MODIFIER));
        }

        void lastModifier(String lastModifier) {
            this.stringValue(COLUMN_LAST_MODIFIER, lastModifier);
        }

        LocalDateTime lastModificationTime() {
            return this.datetimeValue(COLUMN_LAST_MODIFICATION_TIME);
        }

        void lastModificationTime(LocalDateTime lastModificationTime) {
            this.datetimeValue(COLUMN_LAST_MODIFICATION_TIME, lastModificationTime);
        }

        static void insert(DynamicSqlExecutor executor, Collection<CategoryUsageRow> rows) {
            List<CategoryUsageRow> actual = Optional.ofNullable(rows).map(Collection::stream).orElseGet(Stream::empty)
                    .filter(Objects::nonNull).collect(Collectors.toList());
            if (actual.isEmpty()) {
                return;
            }
            InsertSql sql = InsertSql.custom().into(TABLE);
            for (CategoryUsageRow row : actual) {
                sql = sql.next();
                sql.value(COLUMN_ID, row.id());
                sql.value(COLUMN_OBJECT_TYPE, row.objectType());
                sql.value(COLUMN_OBJECT_ID, row.objectId());
                sql.value(COLUMN_CATEGORY_GROUP_ID, row.groupId());
                sql.value(COLUMN_CATEGORY_ID, row.categoryId());
                sql.value(COLUMN_CREATOR, row.creator());
                sql.value(COLUMN_CREATION_TIME, row.creationTime());
                sql.value(COLUMN_LAST_MODIFIER, row.lastModifier());
                sql.value(COLUMN_LAST_MODIFICATION_TIME, row.lastModificationTime());
            }
            int affectedRows = sql.execute(executor);
            if (affectedRows < actual.size()) {
                throw new ServerInternalException("Failed to insert category usage into database.");
            }
        }

        static List<CategoryUsageRow> selectByObject(DynamicSqlExecutor executor, String objectType, String objectId) {
            return selectByObject(executor, objectType, Collections.singletonList(objectId));
        }

        static List<CategoryUsageRow> selectByObject(DynamicSqlExecutor executor, String objectType,
                List<String> objectIds) {
            if (objectIds.isEmpty()) {
                return Collections.emptyList();
            }
            SqlBuilder sql = SqlBuilder.custom();
            List<Object> args = new LinkedList<>();
            sql.append("SELECT ");
            sql.appendIdentifier(COLUMN_ID).append(", ");
            sql.appendIdentifier(COLUMN_OBJECT_TYPE).append(", ");
            sql.appendIdentifier(COLUMN_OBJECT_ID).append(", ");
            sql.appendIdentifier(COLUMN_CATEGORY_GROUP_ID).append(", ");
            sql.appendIdentifier(COLUMN_CATEGORY_ID).append(", ");
            sql.appendIdentifier(COLUMN_CREATOR).append(", ");
            sql.appendIdentifier(COLUMN_CREATION_TIME).append(", ");
            sql.appendIdentifier(COLUMN_LAST_MODIFIER).append(", ");
            sql.appendIdentifier(COLUMN_LAST_MODIFICATION_TIME);
            sql.append(" FROM ").appendIdentifier(TABLE).append(" WHERE ");
            if (objectIds.size() > 1) {
                sql.appendIdentifier(COLUMN_OBJECT_ID).append(" IN (").appendRepeatedly("?, ", objectIds.size());
                sql.backspace(2).append(") AND ");
                args.addAll(objectIds);
            } else {
                sql.appendIdentifier(COLUMN_OBJECT_ID).append(" = ? AND ");
                args.add(objectIds.get(0));
            }
            sql.appendIdentifier(COLUMN_OBJECT_TYPE).append(" = ?");
            args.add(objectType);
            List<Map<String, Object>> rows = executor.executeQuery(sql.toString(), args);
            return rows.stream().map(CategoryUsageRow::new).collect(Collectors.toCollection(LinkedList::new));
        }

        static void delete(DynamicSqlExecutor executor, Collection<String> ids) {
            List<String> actualIds = canonicalize(ids);
            if (actualIds.isEmpty()) {
                return;
            }
            DeleteSql.custom().from(TABLE).where(Condition.expectIn(COLUMN_ID, actualIds)).execute(executor);
        }
    }
}
