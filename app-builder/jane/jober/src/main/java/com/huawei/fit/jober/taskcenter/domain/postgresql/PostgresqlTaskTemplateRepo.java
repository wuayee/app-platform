/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.domain.postgresql;

import com.huawei.fit.jane.task.domain.DomainObject;
import com.huawei.fit.jane.task.util.Entities;
import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jane.task.util.UndefinableValue;
import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.ServerInternalException;
import com.huawei.fit.jober.common.aop.TenantAuthentication;
import com.huawei.fit.jober.common.exceptions.BadRequestException;
import com.huawei.fit.jober.common.exceptions.ConflictException;
import com.huawei.fit.jober.common.exceptions.NotFoundException;
import com.huawei.fit.jober.taskcenter.domain.TaskTemplate;
import com.huawei.fit.jober.taskcenter.domain.TaskTemplateProperty;
import com.huawei.fit.jober.taskcenter.filter.TaskTemplateFilter;
import com.huawei.fit.jober.taskcenter.util.DynamicSqlExecutor;
import com.huawei.fit.jober.taskcenter.util.sql.Condition;
import com.huawei.fit.jober.taskcenter.util.sql.DeleteSql;
import com.huawei.fit.jober.taskcenter.util.sql.InsertSql;
import com.huawei.fit.jober.taskcenter.util.sql.SqlBuilder;
import com.huawei.fit.jober.taskcenter.util.sql.UpdateSql;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.model.RangedResultSet;
import com.huawei.fitframework.transaction.Transactional;
import com.huawei.fitframework.util.CollectionUtils;
import com.huawei.fitframework.util.StringUtils;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 为 {@link TaskTemplate.Repo} 提供实现
 *
 * @author yWX1299574
 * @since 2023-12-08 17:03
 */
@Component
@RequiredArgsConstructor
public class PostgresqlTaskTemplateRepo implements TaskTemplate.Repo {
    private static final Logger log = Logger.get(PostgresqlTaskTemplateRepo.class);

    private final TaskTemplateProperty.Repo propertyRepo;

    private final DynamicSqlExecutor executor;

    private final static String TABLE_NAME = "task_template";

    private final static String TABLE_NAME_EXTEND = "extend_table";

    private final static String COLUMN_ID = "id";

    private final static String COLUMN_PARENT_ID = "parent_id";

    private final static String COLUMN_NAME = "name";

    private final static String COLUMN_DESCRIPTION = "description";

    private final static String COLUMN_TENANT_ID = "tenant_id";

    @Override
    @Transactional
    @TenantAuthentication
    public TaskTemplate create(TaskTemplate.Declaration declaration, OperationContext context) {
        log.info("Start create a task template. OperationContext={}", context);
        if (Objects.isNull(declaration)) {
            log.error("Can't create a task template by null declaration.");
            throw new BadRequestException(ErrorCodes.TASK_TEMPLATE_DECLARATION_NOT_NULL);
        }

        UndefinableValue<String> name = declaration.name();
        UndefinableValue<String> description = declaration.description();
        UndefinableValue<String> parentTemplateId = declaration.parentTemplateId();
        // 先存储TaskTemplate
        // 名称必填，且和数据库原有名称不重复
        String required = name.required(() -> {
            log.error("Create task template required name.");
            return new BadRequestException(ErrorCodes.TASK_TEMPLATE_NAME_REQUIRED);
        });
        String actualName = this.checkTaskTemplateNameUnused(required, context.tenantId());
        String actualParentTemplateId = parentTemplateId.withDefault("00000000000000000000000000000000");
        String actualId = Entities.generateId();
        String actualDescription = description.withDefault("");
        String actualTenantId = getActualTenantId(context.tenantId());
        executeTemplate(actualId, actualName, actualDescription, actualParentTemplateId, actualTenantId);

        // 父模板不为空且子模板的properties也不为空，则根据父模板信息去除子模板中与父模板重复的字段
        List<TaskTemplateProperty> taskTemplateProperties = new ArrayList<>();
        if (declaration.properties().defined()) {
            List<TaskTemplateProperty.Declaration> properties = parentTemplateId.defined()
                    ? getActualTemplateProperties(context, actualParentTemplateId, declaration.properties().get())
                    : declaration.properties().get();
            // 然后存储properties
            taskTemplateProperties.addAll(propertyRepo.create(actualId, properties, context));
        }
        log.info("Success create a task template. id={}, name={}.", actualId, actualName);
        return TaskTemplate.custom()
                .id(actualId)
                .name(actualName)
                .description(actualDescription)
                .properties(taskTemplateProperties)
                .tenantId(actualTenantId)
                .build();
    }

    private void executeTemplate(String actualId, String actualName, String actualDescription,
            String actualParentTemplateId, String actualTenantId) {
        int execute = InsertSql.custom()
                .into(TABLE_NAME)
                .value(COLUMN_ID, actualId)
                .value(COLUMN_NAME, actualName)
                .value(COLUMN_DESCRIPTION, actualDescription)
                .value(COLUMN_TENANT_ID, actualTenantId)
                .execute(executor);
        if (execute != 1) {
            log.error("Error create task template: failed to insert task template into database.");
            throw new ServerInternalException("Failed to insert task template into database.");
        }
        int executeExtend = InsertSql.custom()
                .into(TABLE_NAME_EXTEND)
                .value(COLUMN_ID, actualId)
                .value(COLUMN_PARENT_ID, actualParentTemplateId)
                .execute(executor);
        if (executeExtend != 1) {
            log.error("Error create task template extend: failed to insert task template extend into database.");
            throw new ServerInternalException("Failed to insert task template extend into database.");
        }
    }

    private List<TaskTemplateProperty.Declaration> getActualTemplateProperties(OperationContext context,
            String actualParentTemplateId, List<TaskTemplateProperty.Declaration> properties) {
        // 获取继承信息
        List<TaskTemplateProperty> taskTemplateProperties = propertyRepo.list(actualParentTemplateId, context);
        // 去除子模板中与父模板重复的字段
        List<String> parentNames = taskTemplateProperties.stream()
                .map(TaskTemplateProperty::name)
                .distinct()
                .collect(Collectors.toList());
        properties = properties.stream()
                .filter(property -> !parentNames.contains(property.name().get()))
                .collect(Collectors.toList());
        return properties;
    }

    @Override
    @Transactional
    @TenantAuthentication
    public void patch(String id, TaskTemplate.Declaration declaration, OperationContext context) {
        log.info("Start update a task template. Id={}, operationContext={}", id, context);
        if (Objects.isNull(declaration)) {
            log.error("Can't modify a task template by null declaration.");
            throw new BadRequestException(ErrorCodes.TASK_TEMPLATE_DECLARATION_NOT_NULL);
        }
        String actualId = Entities.validateId(id, () -> new BadRequestException(ErrorCodes.TASK_TEMPLATE_ID_INVALID));
        // 校验修改的内容
        if (!declaration.name().defined() && !declaration.description().defined() && !declaration.properties()
                .defined()) {
            throw new BadRequestException(ErrorCodes.TASK_TEMPLATE_EMPTY_DECLARATION);
        }
        // 先检索得到原有的taskTemplate
        TaskTemplate retrieve = this.retrieve(actualId, context);

        UpdateSql update = UpdateSql.custom().table(TABLE_NAME);
        // 修改name、description
        if (declaration.name().defined()) {
            String name = declaration.name().get();
            if (StringUtils.equals(retrieve.name(), name)) {
                throw new ConflictException(ErrorCodes.TASK_TEMPLATE_NAME_NO_MODIFY);
            }
            checkTaskTemplateNameUnused(name, context.tenantId());
            update.set(COLUMN_NAME, name);
        }
        if (declaration.description().defined()) {
            update.set(COLUMN_DESCRIPTION, declaration.description().get());
        }
        update.where(Condition.expectEqual(COLUMN_ID, actualId));

        if ((declaration.name().defined() || declaration.description().defined()) && update.execute(executor) != 1) {
            throw new ServerInternalException("Failed to update task template into database, where id=" + actualId);
        }

        if (declaration.properties().defined()) {
            updateProperties(declaration, context, retrieve, actualId);
        }
    }

    private void updateProperties(TaskTemplate.Declaration declaration, OperationContext context, TaskTemplate retrieve,
            String actualId) {
        // 先分组：有id的为修改的，其他为新增
        Map<Boolean, List<TaskTemplateProperty.Declaration>> collect = declaration.properties()
                .get()
                .stream()
                .collect(Collectors.groupingBy(dp -> dp.id().defined()));

        List<TaskTemplateProperty.Declaration> toBeUpdate = collect.getOrDefault(true, Collections.emptyList());
        List<TaskTemplateProperty.Declaration> toBeCreate = collect.getOrDefault(false, Collections.emptyList());

        Set<String> updateIds = toBeUpdate.stream()
                .map(TaskTemplateProperty.Declaration::id)
                .map(UndefinableValue::get)
                .collect(Collectors.toSet());
        List<String> toBeDeleteIds = retrieve.properties()
                .stream()
                .map(DomainObject::id)
                .filter(oldId -> !updateIds.contains(oldId))
                .collect(Collectors.toList());

        propertyRepo.delete(toBeDeleteIds, context);
        propertyRepo.create(actualId, toBeCreate, context);

        Map<String, TaskTemplateProperty> retrieveProperties = retrieve.properties()
                .stream()
                .collect(Collectors.toMap(TaskTemplateProperty::id, Function.identity()));
        toBeUpdate.stream()
                // 将输入的properties过滤一下，未输入名称和数据类型的、或者与现有property相同的字段清理掉都过滤掉
                .map(property -> this.modifyDeclaration(property, retrieveProperties))
                .filter(property -> property.name().defined() || property.dataType().defined())
                .forEach(updateProperty -> propertyRepo.patch(actualId, updateProperty.id().get(), updateProperty,
                        context));
    }

    private TaskTemplateProperty.Declaration modifyDeclaration(TaskTemplateProperty.Declaration property,
            Map<String, TaskTemplateProperty> retrieveProperties) {
        String id = property.id().get();
        TaskTemplateProperty oldProperty = retrieveProperties.get(id);
        TaskTemplateProperty.Declaration.Builder builder = TaskTemplateProperty.Declaration.custom().id(id);
        if (property.name().defined() && !oldProperty.name().equals(property.name().get())) {
            builder.name(property.name().get());
        }
        if (property.dataType().defined() && !oldProperty.dataType()
                .name()
                .equals(property.dataType().get())) {
            builder.dataType(property.dataType().get());
        }
        return builder.build();
    }

    @Override
    @Transactional
    @TenantAuthentication
    public void delete(String id, OperationContext context) {
        String actualId = Entities.validateId(id, () -> new BadRequestException(ErrorCodes.TASK_TEMPLATE_ID_INVALID));
        List<String> args = Collections.singletonList(actualId);
        // 查询模板是否有子模板，没有才能删除
        String extendSql = "SELECT 1 FROM extend_table WHERE parent_id = ? LIMIT 1";
        if (Objects.equals(1, executor.executeScalar(extendSql, args))) {
            throw new ConflictException(ErrorCodes.TASK_TEMPLATE_IS_PARENT);
        }
        String sql = "SELECT 1 FROM task WHERE template_id = ? LIMIT 1";
        if (Objects.equals(1, executor.executeScalar(sql, args))) {
            throw new ConflictException(ErrorCodes.TASK_TEMPLATE_USED);
        }
        propertyRepo.deleteByTaskTemplateId(actualId, context);
        int delete = DeleteSql.custom()
                .from(TABLE_NAME)
                .where(Condition.expectEqual(COLUMN_ID, actualId))
                .execute(executor);
        if (delete != 1) {
            throw new ServerInternalException("Delete task template error.");
        }
        int deleteExtend = DeleteSql.custom()
                .from(TABLE_NAME_EXTEND)
                .where(Condition.expectEqual(COLUMN_ID, actualId))
                .execute(executor);
        if (deleteExtend != 1) {
            throw new ServerInternalException("Delete task template extend error.");
        }
    }

    @Override
    @Transactional
    public TaskTemplate retrieve(String id, OperationContext context) {

        String actualId = Entities.validateId(id, () -> new BadRequestException(ErrorCodes.TASK_TEMPLATE_ID_INVALID));

        String sql = this.generateSelectBaseSql() + " FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = ?";
        List<String> args = Collections.singletonList(actualId);

        List<Map<String, Object>> maps = executor.executeQuery(sql, args);
        if (CollectionUtils.isEmpty(maps)) {
            throw new NotFoundException(ErrorCodes.TASK_TEMPLATE_NOT_FOUND);
        }
        if (maps.size() > 1) {
            throw new ConflictException(ErrorCodes.TASK_TEMPLATE_FOUND_MORE_THAN_ONE);
        }
        List<TaskTemplateProperty> properties = propertyRepo.list(actualId, context);
        return convertFromRowMap(maps.get(0), properties);
    }

    @Override
    @Transactional
    public RangedResultSet<TaskTemplate> list(TaskTemplateFilter filter, long offset, int limit,
            OperationContext context) {
        // 特殊处理public的情况
        String tenantId = getActualTenantId(context.tenantId());
        SqlBuilder sql = SqlBuilder.custom();
        sql.append(" FROM ").appendIdentifier(TABLE_NAME);
        sql.append(" WHERE ").appendIdentifier(COLUMN_TENANT_ID).append("= ? ");
        List<Object> args = new LinkedList<>();
        args.add(tenantId);
        this.addIdCondition(filter.getIds(), sql, args);
        this.addNameConditions(filter, sql, args);
        long total = ((Number) executor.executeScalar("SELECT COUNT(1) " + sql, args)).longValue();

        sql.append(" ORDER BY ").append(COLUMN_NAME).append(" OFFSET ? LIMIT ?");
        args.add(offset);
        args.add(limit);

        List<Map<String, Object>> rows = executor.executeQuery(this.generateSelectBaseSql() + sql, args);
        List<String> ids = rows.stream().map(row -> (String) row.get(COLUMN_ID)).collect(Collectors.toList());
        Map<String, List<TaskTemplateProperty>> properties = propertyRepo.list(ids, context);

        List<TaskTemplate> result = rows.stream()
                .map(row -> convertFromRowMap(row, properties.get((String) row.get(COLUMN_ID))))
                .collect(Collectors.toList());

        return RangedResultSet.create(result, (int) offset, limit, (int) total);
    }

    private String getActualTenantId(String tenantId) {
        if (tenantId.equals("public")) {
            String sql = "Select id from tenant where name = 'public'";
            Object result = executor.executeScalar(sql);
            if (Objects.isNull(result)) {
                throw new ServerInternalException("The public tenant is not found by sql: " + sql);
            }
            return result.toString();
        }
        return tenantId;
    }

    @Override
    public boolean exist(String id) {
        String actualId = Entities.validateId(id, () -> new BadRequestException(ErrorCodes.TASK_TEMPLATE_ID_INVALID));
        String sql = "SELECT 1 FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = ?";
        List<String> args = Collections.singletonList(actualId);

        return Objects.equals(1, executor.executeScalar(sql, args));
    }

    @Override
    public String defaultTemplateId() {
        String sql = this.generateSelectBaseSql() + " FROM " + TABLE_NAME + " WHERE " + COLUMN_NAME + " = ?";
        List<String> args = Collections.singletonList("普通任务");
        List<Map<String, Object>> rows = executor.executeQuery(sql, args);
        if (CollectionUtils.isEmpty(rows)) {
            throw new ServerInternalException(
                    "The default template is not found. Please add the default template to the database.");
        }
        return (String) rows.get(0).get(COLUMN_ID);
    }

    private void addNameConditions(TaskTemplateFilter filter, SqlBuilder sql, List<Object> args) {
        if (filter.getNames().defined() && CollectionUtils.isNotEmpty(filter.getNames().get())) {
            sql.append(" AND (");
            List<String> names = filter.getNames().get();
            String nameConditions = names.stream()
                    .map(name -> " " + COLUMN_NAME + " LIKE ? ")
                    .collect(Collectors.joining("OR"));
            sql.append(nameConditions);
            sql.append(")");

            args.addAll(names.stream().map(name -> "%" + name + "%").collect(Collectors.toList()));
        }
    }

    private String checkTaskTemplateNameUnused(String name, String tenantId) {
        String sql = "SELECT 1 FROM " + TABLE_NAME + " WHERE name = ? and tenant_id = ? LIMIT 1";
        List<String> args = Arrays.asList(name, tenantId);
        if (Objects.equals(1, executor.executeScalar(sql, args))) {
            log.error("Task template name already exist in same tenant.");
            throw new ConflictException(ErrorCodes.TASK_TEMPLATE_NAME_EXIST);
        }
        return name;
    }

    private String generateSelectBaseSql() {
        return "SELECT " + String.join(",", COLUMN_ID, COLUMN_NAME, COLUMN_DESCRIPTION, COLUMN_TENANT_ID);
    }

    private TaskTemplate convertFromRowMap(Map<String, Object> row, List<TaskTemplateProperty> properties) {
        return TaskTemplate.custom()
                .name((String) row.get(COLUMN_NAME))
                .description((String) row.get(COLUMN_DESCRIPTION))
                .id((String) row.get(COLUMN_ID))
                .tenantId((String) row.get(COLUMN_TENANT_ID))
                .properties(properties)
                .build();
    }

    private void addIdCondition(UndefinableValue<List<String>> condition, SqlBuilder sql, List<Object> args) {
        if (condition.defined() && CollectionUtils.isNotEmpty(condition.get())) {
            sql.append(" AND ");
            sql.appendIdentifier(COLUMN_ID).append(" IN ");
            String conditions = condition.get().stream().map((c) -> "?").collect(Collectors.joining(",", "(", ")"));
            sql.append(conditions);
            args.addAll(condition.get());
        }
    }
}
