/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.service.impl;

import com.huawei.fit.jane.task.domain.TaskProperty;
import com.huawei.fit.jane.task.util.Entities;
import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jane.task.util.UndefinableValue;
import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.ServerInternalException;
import com.huawei.fit.jober.common.exceptions.BadRequestException;
import com.huawei.fit.jober.taskcenter.declaration.OperationRecordDeclaration;
import com.huawei.fit.jober.taskcenter.domain.OperationRecordEntity;
import com.huawei.fit.jober.taskcenter.domain.TaskEntity;
import com.huawei.fit.jober.taskcenter.filter.OperationRecordFilter;
import com.huawei.fit.jober.taskcenter.service.OperationRecordService;
import com.huawei.fit.jober.taskcenter.service.TaskService;
import com.huawei.fit.jober.taskcenter.util.DynamicSqlExecutor;
import com.huawei.fit.jober.taskcenter.util.sql.InsertSql;
import com.huawei.fit.jober.taskcenter.util.sql.SqlBuilder;
import com.huawei.fit.jober.taskcenter.validation.TaskValidator;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.model.RangedResultSet;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.LazyLoader;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import lombok.RequiredArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 为操作记录管理提供实现
 *
 * @author 姚江
 * @since 2023-11-17 14:14
 */
@Component
@RequiredArgsConstructor
public class OperationRecordServiceImpl implements OperationRecordService {
    private static final Logger log = Logger.get(OperationRecordServiceImpl.class);

    private final DynamicSqlExecutor executor;

    private final TaskValidator taskValidator;

    private final TaskService taskService;

    /**
     * 存储操作记录
     *
     * @param declaration 操作记录的声明 {@link OperationRecordDeclaration}
     * @param context 操作上下文 {@link OperationContext}。
     * @return 操作记录实体 {@link OperationRecordEntity}。
     */
    @Override
    public OperationRecordEntity create(OperationRecordDeclaration declaration, OperationContext context) {
        this.validateOperationRecordDeclaration(declaration);
        String id = Entities.generateId();
        LocalDateTime operatedTime = LocalDateTime.now();
        String objectType = declaration.getObjectType().get();
        String objectId = declaration.getObjectId().get();
        String operator = context.operator();
        String operate = declaration.getOperate().get();
        String message = declaration.getMessage().get();

        InsertSql sql = InsertSql.custom().into(TABLE_NAME);
        sql.value(TABLE_FIELD_ID, id);
        sql.value(TABLE_FIELD_OPERATED_TIME, operatedTime);
        sql.value(TABLE_FIELD_OBJECT_TYPE, objectType);
        sql.value(TABLE_FIELD_OBJECT_ID, objectId);
        sql.value(TABLE_FIELD_OPERATOR, operator);
        sql.value(TABLE_FIELD_OPERATE, operate);
        sql.value(TABLE_FIELD_MESSAGE, message);

        if (sql.execute(executor) != 1) {
            throw new ServerInternalException("Failed to insert operation record into database.");
        }
        return new OperationRecordEntity(id, objectType, objectId, operator, message, operatedTime, operate, null, null,
                null);
    }

    /**
     * 查询操作记录
     *
     * @param filter 查询过滤器 {@link OperationRecordFilter}。
     * @param offset 偏移量，64位整数。
     * @param limit 查询条数，32位整数。
     * @param context 操作上下文 {@link OperationContext}。
     * @return 查询结果集 {@link RangedResultSet}{@code <}{@link OperationRecordEntity}{@code >}。
     */
    @Override
    public RangedResultSet<OperationRecordEntity> list(OperationRecordFilter filter, long offset, int limit,
            OperationContext context) {
        taskValidator.validatePagination(offset, limit);

        List<Object> args = new ArrayList<>();
        String conditions = splicingConditions(filter, args);
        String sqlSuffix = SqlBuilder.custom()
                .append(" FROM ").append(TABLE_NAME)
                .append(" WHERE ").append("1=1")
                .append(conditions)
                .toString();

        String countSql = SqlBuilder.custom().append("SELECT COUNT(*)").append(sqlSuffix).toString();
        long total = (ObjectUtils.<Number>cast(executor.executeScalar(countSql, args))).longValue();

        if (total == 0) {
            return RangedResultSet.create(Collections.emptyList(), (int) offset, limit, (int) total);
        }

        String querySql = SqlBuilder.custom()
                .append("SELECT ").append(splicingColumns()).append(sqlSuffix)
                .append(" ORDER BY ").append(TABLE_FIELD_OPERATED_TIME).append(" DESC ")
                .append(" OFFSET ? LIMIT ?")
                .toString();
        args.add(offset);
        args.add(limit);

        List<Map<String, Object>> rows = executor.executeQuery(querySql, args);

        // 做一个懒加载，在后续查询instance的taskEntity时使用，如果同一个taskEntity，则不需要被查询多次。
        Map<String, LazyLoader<TaskEntity>> taskRetrieves = rows.stream()
                .map(row -> ObjectUtils.<String>cast(row.get(TABLE_FIELD_OBJECT_ID)))
                .distinct()
                .collect(Collectors.toMap(Function.identity(),
                        (id) -> new LazyLoader<>(() -> getTaskEntityByInstanceId(id))));
        List<OperationRecordEntity> entities = rows.stream()
                .map(row -> this.convertRowMapToOperationRecordEntity(row, taskRetrieves))
                .collect(Collectors.toList());

        return RangedResultSet.create(entities, (int) offset, limit, (int) total);
    }

    private OperationRecordEntity convertRowMapToOperationRecordEntity(Map<String, Object> row,
            Map<String, LazyLoader<TaskEntity>> taskRetrieves) {
        OperationRecordEntity entity = new OperationRecordEntity();
        entity.setId(ObjectUtils.cast(row.get(TABLE_FIELD_ID)));
        entity.setOperate(ObjectUtils.cast(row.get(TABLE_FIELD_OPERATE)));
        entity.setOperator(ObjectUtils.cast(row.get(TABLE_FIELD_OPERATOR)));
        entity.setMessage(ObjectUtils.cast(row.get(TABLE_FIELD_MESSAGE)));
        entity.setOperatedTime(ObjectUtils.<Timestamp>cast(row.get(TABLE_FIELD_OPERATED_TIME)).toLocalDateTime());
        entity.setObjectId(ObjectUtils.cast(row.get(TABLE_FIELD_OBJECT_ID)));
        entity.setObjectType(ObjectUtils.cast(row.get(TABLE_FIELD_OBJECT_TYPE)));

        if (StringUtils.equalsIgnoreCase("instance", entity.getObjectType())) {
            this.convertInstanceView(entity, taskRetrieves.get(entity.getObjectId()));
        }
        return entity;
    }

    private void convertInstanceView(OperationRecordEntity entity, LazyLoader<TaskEntity> taskRetrieve) {
        String message = entity.getMessage();
        if (StringUtils.isEmpty(message)) {
            return;
        }

        JSONObject messageJson = JSON.parseObject(message);
        JSONObject declaration = messageJson.getJSONObject("declaration");
        String title = messageJson.getString("title");
        String detail = messageJson.getString("detail");

        entity.setTitle(title);
        if (StringUtils.equalsIgnoreCase("reladd", entity.getOperate().trim()) || StringUtils.equalsIgnoreCase("reldel",
                entity.getOperate().trim())) {
            Map<String, Object> content = new HashMap<>();
            content.put("title", declaration.getString("message"));
            entity.setContent(content);
            return;
        }
        TaskEntity retrieve = taskRetrieve.get();
        if (retrieve == null) {
            return;
        }
        // 将任务定义的名称写入entity
        entity.setInstanceTask(retrieve.getName());
        if (StringUtils.equalsIgnoreCase("created", entity.getOperate())) {
            entity.setTitle(" 创建了" + retrieve.getName() + "实例");
            Map<String, Object> content = new HashMap<>();
            content.put("title", "创建了标题为【" + detail + "】的 " + retrieve.getName() + " 实例");
            content.put("id", entity.getObjectId());
            entity.setContent(content);
            return;
        }

        if (StringUtils.equalsIgnoreCase("updated", entity.getOperate())) {
            Map<String, Object> content = this.generateUpdatedContent(entity, retrieve, detail, declaration);
            entity.setContent(content);
            return;
        }

        if (StringUtils.equalsIgnoreCase("deleted", entity.getOperate())) {
            entity.setTitle(" 删除了" + retrieve.getName() + "实例, id是 " + detail);
        }
    }

    private Map<String, Object> generateUpdatedContent(OperationRecordEntity entity, TaskEntity retrieve, String detail,
            JSONObject declaration) {
        entity.setTitle(" 更新了" + retrieve.getName() + "实例, id是 " + detail);

        Map<String, Object> content = new HashMap<>();
        List<String> updates = new ArrayList<>();
        content.put("updates", updates);
        JSONObject info = declaration.getJSONObject("info");
        Map<String, Map<String, Object>> keyAppearanceMap = retrieve.getProperties()
                .stream()
                .collect(Collectors.toMap(TaskProperty::name, TaskProperty::appearance));

        for (String key : info.keySet()) {
            if (equalsAnyIgnoreCase(key, "created_by", "created_date", "modified_by", "modified_date")) {
                continue;
            }
            Map<String, Object> appearance = keyAppearanceMap.getOrDefault(key, Collections.emptyMap());
            String propertyName = ObjectUtils.cast(appearance.getOrDefault("name", key));
            Object propertyContext = info.get(key);
            if (equalsAnyIgnoreCase(ObjectUtils.cast(appearance.get("displayType")),
                    "select", "radio", "selectModal")) {
                Map<String, String> options = this.parsingTheOptions(appearance.get("options"));
                propertyContext = options.getOrDefault(ObjectUtils.<String>cast(propertyContext),
                        ObjectUtils.cast(propertyContext));
            }
            String updateContent = "更新 " + propertyName + " 为 【" + propertyContext + "】";
            updates.add(updateContent);
        }
        return content;
    }

    private static boolean equalsAnyIgnoreCase(String str1, String... strings) {
        return Arrays.stream(strings).anyMatch(str2 -> StringUtils.equalsIgnoreCase(str1, str2));
    }

    private Map<String, String> parsingTheOptions(Object options) {
        if (Objects.isNull(options)) {
            return Collections.emptyMap();
        }
        return ObjectUtils.<List<Object>>cast(options).stream()
                .map(ObjectUtils::<Map<String, String>>cast)
                .collect(Collectors.toMap(i -> i.get("value"), i -> i.get("text")));
    }

    private TaskEntity getTaskEntityByInstanceId(String instanceId) {
        String sql = "select task_id from task_instance_wide where id = ?";
        List<String> args = Collections.singletonList(instanceId);

        Object taskId = this.executor.executeScalar(sql, args);
        if (Objects.isNull(taskId)) {
            log.warn("The task_id in task_instance_wide is null. Query sql is {}, arg is {}.", sql, instanceId);
            return null;
        }

        return taskService.retrieve(taskId.toString(), OperationContext.empty());
    }

    private void validateOperationRecordDeclaration(OperationRecordDeclaration declaration) {
        if (Objects.isNull(declaration)) {
            throw new BadRequestException(ErrorCodes.OPERATION_RECORD_DECLARATION_IS_NULL);
        }

        if (Objects.isNull(declaration.getMessage()) || Objects.isNull(declaration.getMessage().get())) {
            throw new BadRequestException(ErrorCodes.OPERATION_RECORD_DECLARATION_FIELD_IS_NULL, "message");
        }
        if (Objects.isNull(declaration.getObjectId()) || Objects.isNull(declaration.getObjectId().get())) {
            throw new BadRequestException(ErrorCodes.OPERATION_RECORD_DECLARATION_FIELD_IS_NULL, "objectId");
        }
        if (Objects.isNull(declaration.getObjectType()) || Objects.isNull(declaration.getObjectType().get())) {
            throw new BadRequestException(ErrorCodes.OPERATION_RECORD_DECLARATION_FIELD_IS_NULL, "objectType");
        }
        if (Objects.isNull(declaration.getOperate()) || Objects.isNull(declaration.getOperate().get())) {
            throw new BadRequestException(ErrorCodes.OPERATION_RECORD_DECLARATION_FIELD_IS_NULL, "operate");
        }
    }

    private String splicingConditions(OperationRecordFilter filter, List<Object> args) {
        if (Objects.isNull(filter)) {
            throw new BadRequestException(ErrorCodes.FILTER_IS_EMPTY);
        }

        if (Objects.isNull(filter.getObjectTypes()) || CollectionUtils.isEmpty(filter.getObjectTypes().get())) {
            throw new BadRequestException(ErrorCodes.OPERATION_RECORD_LIST_FILTER_FIELD_IS_EMPTY, "objectTypes");
        }
        if (Objects.isNull(filter.getObjectIds()) || CollectionUtils.isEmpty(filter.getObjectIds().get())) {
            throw new BadRequestException(ErrorCodes.OPERATION_RECORD_LIST_FILTER_FIELD_IS_EMPTY, "objectIds");
        }

        SqlBuilder sql = SqlBuilder.custom();

        this.splicingInConditions(filter.getObjectTypes(), args, sql, TABLE_FIELD_OBJECT_TYPE);
        this.splicingInConditions(filter.getObjectIds(), args, sql, TABLE_FIELD_OBJECT_ID);

        return sql.toString();
    }

    private void splicingInConditions(UndefinableValue<List<String>> conditions, List<Object> args, SqlBuilder sql,
            String fieldName) {
        sql.append(" AND ").append(fieldName).append(" IN (");
        conditions.ifDefined(conditionList -> {
            sql.appendRepeatedly("?, ", conditionList.size());
            args.addAll(conditionList);
        });
        sql.backspace(2);
        sql.append(")");
    }

    private String splicingColumns() {
        return TABLE_FIELD_ID + "," + TABLE_FIELD_OBJECT_TYPE + "," + TABLE_FIELD_OBJECT_ID + "," + TABLE_FIELD_MESSAGE
                + "," + TABLE_FIELD_OPERATOR + "," + TABLE_FIELD_OPERATED_TIME + "," + TABLE_FIELD_OPERATE;
    }
}
