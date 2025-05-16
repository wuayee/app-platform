/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.service.impl;

import static modelengine.fit.jober.common.ErrorCodes.INPUT_PARAM_IS_EMPTY;
import static modelengine.fitframework.util.ObjectUtils.cast;
import static modelengine.fitframework.util.ObjectUtils.nullIf;

import modelengine.fit.jane.task.domain.TaskProperty;
import modelengine.fit.jane.task.domain.Tenant;
import modelengine.fit.jane.task.util.Entities;
import modelengine.fit.jane.task.util.OperationContext;
import modelengine.fit.jane.task.util.UndefinableValue;
import modelengine.fit.jober.common.aop.ObjectTypeEnum;
import modelengine.fit.jober.common.aop.OperateEnum;
import modelengine.fit.jober.common.aop.OperationRecord;
import modelengine.fit.jober.common.aop.TenantAuthentication;
import modelengine.fit.jober.common.enums.JaneCategory;
import modelengine.fit.jober.common.util.ParamUtils;
import modelengine.fit.jober.taskcenter.dao.TaskMapper;
import modelengine.fit.jober.taskcenter.dao.po.TaskObject;
import modelengine.fit.jober.taskcenter.declaration.TaskCategoryTriggerDeclaration;
import modelengine.fit.jober.taskcenter.declaration.TaskDeclaration;
import modelengine.fit.jober.taskcenter.declaration.TaskPropertiesDeclaration;
import modelengine.fit.jober.taskcenter.domain.CategoryEntity;
import modelengine.fit.jober.taskcenter.domain.Index;
import modelengine.fit.jober.taskcenter.domain.TaskCategoryTriggerEntity;
import modelengine.fit.jober.taskcenter.domain.TaskEntity;
import modelengine.fit.jober.taskcenter.domain.TaskTemplate;
import modelengine.fit.jober.taskcenter.domain.TaskTemplateProperty;
import modelengine.fit.jober.taskcenter.domain.TaskType;
import modelengine.fit.jober.taskcenter.filter.TaskFilter;
import modelengine.fit.jober.taskcenter.service.CategoryService;
import modelengine.fit.jober.taskcenter.service.PropertyService;
import modelengine.fit.jober.taskcenter.service.TaskService;
import modelengine.fit.jober.taskcenter.util.DynamicSqlExecutor;
import modelengine.fit.jober.taskcenter.util.MapSerializer;
import modelengine.fit.jober.taskcenter.util.Sqls;
import modelengine.fit.jober.taskcenter.util.sql.OrderBy;
import modelengine.fit.jober.taskcenter.util.sql.SqlBuilder;
import modelengine.fit.jober.taskcenter.validation.RelationshipValidator;
import modelengine.fit.jober.taskcenter.validation.TaskValidator;

import lombok.RequiredArgsConstructor;
import modelengine.fit.jane.meta.multiversion.definition.MetaFilter;
import modelengine.fit.jober.common.ErrorCodes;
import modelengine.fit.jober.common.exceptions.BadRequestException;
import modelengine.fit.jober.common.exceptions.ConflictException;
import modelengine.fit.jober.common.exceptions.JobberParamException;
import modelengine.fit.jober.common.exceptions.NotFoundException;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.model.RangedResultSet;
import modelengine.fitframework.transaction.Transactional;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.MapUtils;
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
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * {@link TaskService} 的默认实现类。
 *
 * @author 梁致强
 * @since 2023-08-08
 */
@Component
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private static final Logger log = Logger.get(TaskServiceImpl.class);

    private static final String SQL_MODULE = "task";

    private static final List<String> SPEC_WORD_LIST = new ArrayList<>(
            Arrays.asList("creator", "asc(creator)", "desc(creator)"));

    private final PropertyService propertyService;

    private final TaskProperty.Repo taskPropertyRepo;

    private final TaskType.Repo taskTypeRepo;

    private final TaskMapper taskMapper;

    private final DynamicSqlExecutor executor;

    private final TaskValidator taskValidator;

    private final RelationshipValidator relationshipValidator;

    private final CategoryService categoryService;

    private final MapSerializer serializer;

    private final Tenant.Repo tenantRepo;

    private final TaskTemplate.Repo taskTemplateRepo;

    private final Index.Repo indexRepo;

    /**
     * 创建任务定义
     *
     * @param declaration 表示任务声明的 {@link TaskDeclaration}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 表示新创建的任务定义的 {@link TaskEntity}。
     */
    @Override
    @Transactional
    @TenantAuthentication
    @OperationRecord(objectId = -1, objectIdGetMethodName = "getId", objectType = ObjectTypeEnum.TASK,
            operate = OperateEnum.CREATED, declaration = 0)
    public TaskEntity create(TaskDeclaration declaration, OperationContext context) {
        OperationContext actualContext = this.validateOperationContext(context);
        String taskId = Entities.generateId();
        TaskObject taskObject = this.toTaskObject(taskId, declaration, actualContext);
        Long taskCount = this.obtainTenantOfTask(taskObject.getName(), taskObject.getTenantId());
        if (taskCount > 0L) {
            log.error("A task with the same name already exists in the current tenant. [taskName={}]",
                    taskObject.getName());
            throw new ConflictException(ErrorCodes.TASK_EXIST_IN_CURRENT_TENANT);
        }
        taskMapper.insert(taskObject);
        TaskTemplate template = this.retrieveTemplate(taskObject.getTemplateId(), context).orElse(null);
        this.saveDeclarations(taskId, declaration, template, actualContext);
        this.saveCategoryTriggers(taskId, declaration.getCategoryTriggers());
        TaskEntity task = toTaskEntity(taskObject, actualContext);
        List<TaskEntity> tasks = Collections.singletonList(task);
        fillCategoryTriggers(tasks);
        declaration.getIndexes().ifDefined(indexes -> this.indexRepo.save(task, indexes, context));
        return task;
    }

    private Optional<TaskTemplate> retrieveTemplate(String templateId, OperationContext context) {
        if (Entities.emptyId().equals(templateId)) {
            return Optional.empty();
        }
        return Optional.ofNullable(taskTemplateRepo.retrieve(templateId, context));
    }

    private Long obtainTenantOfTask(String taskName, String tenantId) {
        String sql = "SELECT count(1) FROM task WHERE name = ? and tenant_id = ?";
        List<Object> args = new ArrayList<>();
        args.add(taskName);
        args.add(tenantId);
        return cast(this.executor.executeScalar(sql, args));
    }

    private void validateNameWhenPatch(String taskId, String tenantId, String name) {
        String sql = "SELECT id, tenant_id FROM task WHERE name = ? AND id <> ?";
        List<Object> args = Arrays.asList(name, taskId);
        Map<String, Object> row = this.executor.executeQuery(sql, args).stream().findAny().orElse(null);
        if (row == null) {
            return;
        }
        String existingTaskId = cast(row.get("id"));
        String existingTenantId = cast(row.get("tenant_id"));
        if (Objects.equals(existingTenantId, tenantId)) {
            log.error("A task with the same name already exists in the current tenant. [taskId={}]", existingTaskId);
            throw new ConflictException(ErrorCodes.TASK_EXIST_IN_CURRENT_TENANT);
        }
    }

    private Optional<TaskTemplate> getTemplateIdWhenPatch(String taskId, String templateId, OperationContext context) {
        String sqlForSelectTemplateId = "SELECT template_id FROM task WHERE id = ?";
        List<String> args = Collections.singletonList(taskId);
        String currentTemplateId = ObjectUtils.cast(this.executor.executeScalar(sqlForSelectTemplateId, args));
        if (Objects.equals(Entities.emptyId(), currentTemplateId)) {
            // 当前使用的ID为 00000000000000000000000000000000
            return Optional.empty();
        }
        if (Objects.nonNull(templateId)) {
            // 未输入templateId， 则认为不修改templateId
            String actualTemplateId = Entities.validateId(templateId,
                    () -> new BadRequestException(ErrorCodes.TASK_TEMPLATE_ID_INVALID));
            if (!StringUtils.equalsIgnoreCase(actualTemplateId, currentTemplateId)) {
                // 任务模板在patch的时候不允许修改，如果修改抛出异常
                throw new BadRequestException(ErrorCodes.CHANGE_TASK_TEMPLATE_IS_INVALID);
            }
        }
        return retrieveTemplate(currentTemplateId, context);
    }

    /**
     * 更新任务定义
     *
     * @param tId 表示待更新的任务定义的唯一标识的 {@link String}。
     * @param declaration 表示任务声明的 {@link TaskDeclaration}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     */
    @Override
    @Transactional
    @TenantAuthentication
    @OperationRecord(objectType = ObjectTypeEnum.TASK, operate = OperateEnum.UPDATED, declaration = 1)
    public void patch(String tId, TaskDeclaration declaration, OperationContext context) {
        OperationContext actualContext = this.validateOperationContext(context);
        String taskId = taskValidator.validateTaskId(tId, actualContext);
        relationshipValidator.validateTaskExistInTenant(taskId, actualContext.tenantId());
        StringBuilder sqlSb = new StringBuilder();
        List<Object> args = new ArrayList<>();
        if (declaration.getName().defined()) {
            String name = declaration.getName().get();
            this.validateNameWhenPatch(taskId, actualContext.tenantId(), name);
            sqlSb.append(" name = ?,");
            args.add(name);
        }
        if (declaration.getAttributes().defined()) {
            sqlSb.append(" attributes = ?::JSON,");
            args.add(this.serializer.serialize(declaration.getAttributes().get()));
        }
        if (!actualContext.operator().isEmpty()) {
            sqlSb.append(" updated_by = ?,");
            args.add(actualContext.operator());
        }
        if (!actualContext.tenantId().isEmpty()) {
            sqlSb.append(" tenant_id = ?,");
            args.add(actualContext.tenantId());
        }
        sqlSb.append(" updated_at = ?,");
        args.add(LocalDateTime.now());
        sqlSb.deleteCharAt(sqlSb.length() - 1);
        args.add(taskId);
        executor.executeUpdate("update task set" + sqlSb + " where id = ?", args);
        String templateId = Optional.of(declaration)
                .map(TaskDeclaration::getTemplateId)
                .map(UndefinableValue::get)
                .orElse(null);
        TaskTemplate templateIdWhenPatch = this.getTemplateIdWhenPatch(taskId, templateId, context).orElse(null);
        this.saveDeclarations(taskId, declaration, templateIdWhenPatch, actualContext);
        this.saveCategoryTriggers(taskId, declaration.getCategoryTriggers());
    }

    /**
     * 删除任务定义
     *
     * @param tId 表示待删除的任务定义的唯一标识的 {@link String}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     */
    @Override
    @Transactional
    @TenantAuthentication
    @OperationRecord(objectType = ObjectTypeEnum.TASK, operate = OperateEnum.DELETED)
    public void delete(String tId, OperationContext context) {
        String taskId = taskValidator.validateTaskId(tId, context);
        relationshipValidator.validateTaskExistInTenant(taskId, context.tenantId());
        if (this.taskTypeRepo.exists(taskId)) {
            throw new ConflictException(ErrorCodes.TREE_DELETING_HAS_NODES);
        }
        this.taskPropertyRepo.deleteByTask(taskId, context);
        this.deleteCategoryTriggers(taskId);
        taskMapper.delete(taskId);
    }

    /**
     * 查询任务定义
     *
     * @param tId 表示待检索的任务定义的唯一标识的 {@link String}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 表示查询到的任务定义的 {@link TaskEntity}。
     */
    @Override
    public TaskEntity retrieve(String tId, OperationContext context) {
        String taskId = taskValidator.validateTaskId(tId, context);
        TaskObject taskObject = taskMapper.selectById(taskId);
        if (taskObject == null) {
            throw new NotFoundException(ErrorCodes.TASK_NOT_FOUND, ParamUtils.convertOperationContext(context), taskId);
        }
        TaskEntity task = toTaskEntity(taskObject, context);
        List<TaskEntity> tasks = Collections.singletonList(task);
        fillCategoryTriggers(tasks);
        this.fillTypes(tasks, context);
        this.fillIndexes(tasks, context);
        return task;
    }

    /**
     * 检索任务定义集合。
     *
     * @param taskIds 表示待检索的任务定义的唯一标识的 {@link List<String> }。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 表示查询到的任务定义的 {@link List<TaskEntity>}。
     */
    @Override
    public List<TaskEntity> listTaskEntities(List<String> taskIds, OperationContext context) {
        List<TaskObject> taskObjects = taskMapper.selectByIds(taskIds);
        if (taskObjects == null) {
            throw new NotFoundException(ErrorCodes.TASK_NOT_FOUND, ParamUtils.convertOperationContext(context),
                    taskIds);
        }
        List<TaskEntity> taskList = new ArrayList<>();
        taskObjects.forEach(taskObject -> {
            TaskEntity task = toTaskEntity(taskObject, context);
            List<TaskEntity> tasks = Collections.singletonList(task);
            fillCategoryTriggers(tasks);
            this.fillTypes(tasks, context);
            this.fillIndexes(tasks, context);
            taskList.add(task);
        });
        return taskList;
    }

    /**
     * 查询任务定义列表
     *
     * @param filter 表示任务过滤器的 {@link TaskFilter}。
     * @param offset 表示查询到的任务定义的结果集在全量结果集中的偏移量的 64 位整数。
     * @param limit 表示查询到的任务定义的结果集中的最大数量的 32 位整数。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 表示查询到的结果集的 {@link RangedResultSet}{@code <}{@link TaskEntity}{@code >}。
     */
    @Override
    public RangedResultSet<TaskEntity> list(TaskFilter filter, long offset, int limit, OperationContext context) {
        OperationContext actualContext = this.validateOperationContext(context);
        taskValidator.validatePagination(offset, limit);
        SqlBuilder sql = SqlBuilder.custom()
                .append("WITH \"task_ins\" AS (SELECT \"id\", \"name\", \"template_id\", \"category\", \"tenant_id\", ")
                .append("\"attributes\", \"created_by\", \"created_at\", \"updated_by\", \"updated_at\"  FROM ")
                .appendIdentifier("task")
                .append(" WHERE ")
                .appendIdentifier("tenant_id")
                .append(" = ?");
        List<Object> args = new LinkedList<>();
        args.add(actualContext.tenantId());

        return listTaskEntities(filter, offset, limit, sql, args, actualContext);
    }

    /**
     * 查询任务定义。
     *
     * @param filter 表示任务过滤器的 {@link TaskFilter}。
     * @param offset 表示查询到的任务定义的结果集在全量结果集中的偏移量的 64 位整数。
     * @param limit 表示查询到的任务定义的结果集中的最大数量的 32 位整数。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 表示查询到的结果集的 {@link RangedResultSet}{@code <}{@link TaskEntity}{@code >}。
     */
    @Override
    public RangedResultSet<TaskEntity> listForApplication(TaskFilter filter, long offset, int limit,
            OperationContext context) {
        OperationContext actualContext = this.validateOperationContext(context);
        taskValidator.validatePagination(offset, limit);
        SqlBuilder sql = SqlBuilder.custom()
                .append("WITH \"task_ins\" AS (SELECT \"id\", \"name\", \"template_id\", \"category\", \"tenant_id\", ")
                .append("\"attributes\", \"created_by\", \"created_at\", \"updated_by\", \"updated_at\" FROM ")
                .appendIdentifier("task")
                .append(" WHERE 1 = 1 ");
        return listTaskEntities(filter, offset, limit, sql, new LinkedList<>(), actualContext);
    }

    @Override
    public RangedResultSet<TaskEntity> listMeta(MetaFilter filter, boolean isLatestOnly, long offset, int limit,
            OperationContext context) {
        OperationContext actualContext = this.validateOperationContext(context);
        taskValidator.validatePagination(offset, limit);
        if (filter.getOrderBys().stream().noneMatch(orderBy -> orderBy.contains("created_at"))) {
            List<String> orderBys = new ArrayList<>(filter.getOrderBys());
            orderBys.add("desc(created_at)");
            filter.setOrderBys(orderBys);
        }

        // from task where tenant_id = ? and id in (?,?) and ( name like ? or name like ?)
        SqlBuilder whereSql = generateWhereSql();
        List<Object> args = new ArrayList<>();
        args.add(actualContext.tenantId());
        SqlBuilder orderBySql = SqlBuilder.custom();
        if (!buildMetaWhereSql(filter, whereSql, args) || !whereOrderBys(UndefinableValue.defined(filter.getOrderBys()),
                orderBySql)) {
            return Entities.emptyRangedResultSet(offset, limit);
        }
        String offsetSql = " OFFSET ? LIMIT ?";
        String countSql = createCountSql(isLatestOnly, whereSql, orderBySql);
        long total = (ObjectUtils.<Number>cast(executor.executeScalar(countSql, args))).longValue();
        String selectSql = createSelectSql(isLatestOnly, whereSql, orderBySql, offsetSql);
        args.add(offset);
        args.add(limit);
        List<Map<String, Object>> rows = executor.executeQuery(selectSql, args);
        List<TaskEntity> taskEntityList = rows.stream().map(this::convertRowToTaskEntity).collect(Collectors.toList());
        this.fillProperties(taskEntityList, actualContext);
        return RangedResultSet.create(taskEntityList, (int) offset, limit, (int) total);
    }

    private static SqlBuilder generateWhereSql() {
        return SqlBuilder.custom()
                .append("FROM ")
                .appendIdentifier("task")
                .append(" WHERE ")
                .appendIdentifier("tenant_id")
                .append(" = ?");
    }

    private static String createCountSql(boolean isLastOnly, SqlBuilder whereSql, SqlBuilder orderBySql) {
        if (isLastOnly) {
            return "SELECT count(1) " + " FROM ( SELECT *, row_number() over (PARTITION by \"template_id\" "
                    + orderBySql + ") AS group_idx " + whereSql + ") s WHERE s.group_idx = 1";
        } else {
            return "WITH \"task_ins\" AS (SELECT * " + whereSql + orderBySql + ") SELECT COUNT(1) FROM \"task_ins\"";
        }
    }

    private static String createSelectSql(boolean isLastOnly, SqlBuilder where, SqlBuilder order, String offset) {
        if (isLastOnly) {
            return "SELECT s.\"id\", s.\"name\", s.\"template_id\", s.\"category\", s.\"tenant_id\", "
                    + "s.\"attributes\", s.\"created_by\", s.\"created_at\", s.\"updated_by\", s.\"updated_at\""
                    + " FROM ( SELECT *, row_number() over (PARTITION by \"template_id\"" + order + " ) AS group_idx "
                    + where + ") s WHERE s.group_idx = 1" + offset;
        } else {
            return "SELECT \"id\", \"name\", \"template_id\", \"category\", \"tenant_id\", \"attributes\", "
                    + "\"created_by\", \"created_at\", \"updated_by\", \"updated_at\" " + where + order + offset;
        }
    }

    private static boolean buildMetaWhereSql(MetaFilter filter, SqlBuilder sql, List<Object> args) {
        boolean isBuildSuccess = true;
        if (CollectionUtils.isNotEmpty(filter.getVersionIds())) {
            isBuildSuccess = whereIn(UndefinableValue.defined(filter.getVersionIds()),
                    stream -> stream.filter(Entities::isId).map(Entities::canonicalizeId), sql, args, "id");
        }
        if (CollectionUtils.isNotEmpty(filter.getMetaIds())) {
            isBuildSuccess = isBuildSuccess && whereIn(UndefinableValue.defined(filter.getMetaIds()),
                    stream -> stream.filter(Entities::isId).map(Entities::canonicalizeId), sql, args, "template_id");
        }
        if (CollectionUtils.isNotEmpty(filter.getCategories())) {
            isBuildSuccess = isBuildSuccess && whereIn(UndefinableValue.defined(filter.getCategories()),
                    stream -> stream.filter(StringUtils::isNotEmpty), sql, args, "category");
        }
        if (MapUtils.isNotEmpty(filter.getAttributes())) {
            isBuildSuccess = isBuildSuccess && whereAttributesIn(filter.getAttributes(), sql, args);
        }
        if (CollectionUtils.isNotEmpty(filter.getCreators())) {
            isBuildSuccess = isBuildSuccess && whereIn(UndefinableValue.defined(filter.getCreators()),
                    stream -> stream.filter(StringUtils::isNotEmpty), sql, args, "created_by");
        }
        if (CollectionUtils.isNotEmpty(filter.getNames())) {
            isBuildSuccess = isBuildSuccess && whereNames(UndefinableValue.defined(filter.getNames()),
                    Sqls::escapeLikeValue, sql, args);
        }
        if (CollectionUtils.isNotEmpty(filter.getVersions())) {
            isBuildSuccess = isBuildSuccess && whereNames(UndefinableValue.defined(filter.getVersions()),
                    (s) -> Sqls.escapeLeftLikeValue("|" + s), sql, args);
        }
        return isBuildSuccess;
    }

    private static boolean whereAttributesIn(Map<String, List<String>> attributes, SqlBuilder sql, List<Object> args) {
        for (Map.Entry<String, List<String>> entry : attributes.entrySet()) {
            if (CollectionUtils.isEmpty(entry.getValue())) {
                continue;
            }
            sql.append(" AND ")
                    .appendIdentifier("attributes")
                    .append("->>")
                    .append("'" + entry.getKey() + "'")
                    .append(" IN (")
                    .appendRepeatedly("?, ", entry.getValue().size())
                    .backspace(2)
                    .append(")");
            args.addAll(entry.getValue());
        }

        return true;
    }

    private void addDefaultOrderBy(TaskFilter filter) {
        List<String> orderBys = filter.getOrderBys().withDefault(new ArrayList<>());
        List<String> orderByReal = new ArrayList<>(orderBys);
        orderByReal.add("created_at");
        filter.setOrderBys(UndefinableValue.defined(orderByReal));
    }

    private RangedResultSet<TaskEntity> listTaskEntities(TaskFilter filter, long offset, int limit, SqlBuilder sql,
            List<Object> args, OperationContext actualContext) {
        addDefaultOrderBy(filter);
        if (!buildWhereSql(filter, sql, args)) {
            return Entities.emptyRangedResultSet(offset, limit);
        }
        sql.append(") ");
        String suffix = sql.toString();
        long total = countTaskEntityNumber(suffix, args);
        SqlBuilder querySql = createTaskQuerySql(suffix);
        List<Object> queryArgs = new ArrayList<>(args);
        queryArgs.addAll(Arrays.asList(offset, limit));
        List<Map<String, Object>> rows = this.executor.executeQuery(querySql.toString(), queryArgs);
        List<TaskEntity> entities = rows.stream().map(this::convertRowToTaskEntity).collect(Collectors.toList());
        this.fillProperties(entities, actualContext);
        this.fillTypes(entities, actualContext);
        this.fillIndexes(entities, actualContext);
        this.fillCategoryTriggers(entities);
        return RangedResultSet.create(entities, (int) offset, limit, (int) total);
    }

    private long countTaskEntityNumber(String suffix, List<Object> args) {
        return (ObjectUtils.<Number>cast(this.executor.executeScalar(suffix + "SELECT COUNT(1) FROM \"task_ins\"",
                args))).longValue();
    }

    private static SqlBuilder createTaskQuerySql(String suffix) {
        return SqlBuilder.custom().append(suffix).append("SELECT * FROM \"task_ins\" OFFSET ? LIMIT ?");
    }

    private TaskEntity convertRowToTaskEntity(Map<String, Object> row) {
        TaskEntity entity = new TaskEntity();
        entity.setId(ObjectUtils.cast(row.get("id")));
        entity.setName(ObjectUtils.cast(row.get("name")));
        entity.setTemplateId(ObjectUtils.cast(row.get("template_id")));
        entity.setCategory(JaneCategory.valueOf(ObjectUtils.cast(row.get("category"))));
        entity.setAttributes(this.serializer.deserialize(Objects.toString(row.get("attributes"))));
        entity.setCreator(ObjectUtils.cast(row.get("created_by")));
        entity.setCreationTime((ObjectUtils.<Timestamp>cast(row.get("created_at"))).toLocalDateTime());
        entity.setLastModifier(ObjectUtils.cast(row.get("updated_by")));
        entity.setLastModificationTime((ObjectUtils.<Timestamp>cast(row.get("updated_at"))).toLocalDateTime());
        return entity;
    }

    private OperationContext validateOperationContext(OperationContext context) {
        try {
            Tenant tenant = this.tenantRepo.retrieve(context.tenantId(), context);
            return OperationContext.custom()
                    .tenantId(tenant.id())
                    .operator(context.operator())
                    .operatorIp(context.operatorIp())
                    .build();
        } catch (NotFoundException ignored) {
            return context;
        }
    }

    private static boolean buildWhereSql(TaskFilter filter, SqlBuilder sql, List<Object> args) {
        return whereIds(filter, sql, args) && whereNames(filter.getNames(), Sqls::escapeLikeValue, sql, args)
                && whereTemplateIds(filter, sql, args) && whereCategories(filter, sql, args) && whereCreators(filter,
                sql, args) && whereOrderBys(filter.getOrderBys(), sql);
    }

    private static boolean whereIds(TaskFilter filter, SqlBuilder sql, List<Object> args) {
        return whereIn(filter.getIds(), stream -> stream.filter(Entities::isId).map(Entities::canonicalizeId), sql,
                args, "id");
    }

    private static boolean whereNames(UndefinableValue<List<String>> nameFilter, Function<String, String> argMapper,
            SqlBuilder sql, List<Object> args) {
        if (nameFilter == null || !nameFilter.defined()) {
            return true;
        }
        List<String> names = Optional.ofNullable(nameFilter.get())
                .map(Collection::stream)
                .orElseGet(Stream::empty)
                .map(StringUtils::trim)
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.toList());
        if (names.isEmpty()) {
            return false;
        }
        sql.append(" AND (");
        for (int i = 0; i < names.size(); i++) {
            sql.appendIdentifier("name").append(" LIKE ? ESCAPE '\\' OR ");
        }
        sql.backspace(4).append(")");
        args.addAll(names.stream().map(argMapper).collect(Collectors.toList()));
        return true;
    }

    private static boolean whereTemplateIds(TaskFilter filter, SqlBuilder sql, List<Object> args) {
        return whereIn(filter.getTemplateIds(), stream -> stream.filter(StringUtils::isNotEmpty), sql, args,
                "template_id");
    }

    private static boolean whereIn(UndefinableValue<List<String>> columnFilter,
            Function<Stream<String>, Stream<String>> valueFilter, SqlBuilder sql, List<Object> sqlArgs,
            String columnName) {
        if (columnFilter == null || !columnFilter.defined()) {
            return true;
        }
        List<String> queryValueList = valueFilter.apply(Optional.ofNullable(columnFilter.get())
                .map(Collection::stream)
                .orElseGet(Stream::empty)
                .map(StringUtils::trim)).collect(Collectors.toList());
        if (queryValueList.isEmpty()) {
            return false;
        }
        sql.append(" AND ")
                .appendIdentifier(columnName)
                .append(" IN (")
                .appendRepeatedly("?, ", queryValueList.size())
                .backspace(2)
                .append(")");
        sqlArgs.addAll(queryValueList);
        return true;
    }

    private static boolean whereCategories(TaskFilter filter, SqlBuilder sql, List<Object> args) {
        if (filter.getCategories() != null && filter.getCategories().defined()) {
            List<String> categories = Optional.ofNullable(filter.getCategories().get())
                    .map(Collection::stream)
                    .orElseGet(Stream::empty)
                    .map(StringUtils::trim)
                    .filter(StringUtils::isNotEmpty)
                    .collect(Collectors.toList());
            if (categories.isEmpty()) {
                return false;
            }
            sql.append(" AND ")
                    .appendIdentifier("category")
                    .append(" IN (")
                    .appendRepeatedly("?, ", categories.size())
                    .backspace(2)
                    .append(")");
            args.addAll(categories);
        }
        return true;
    }

    private static boolean whereCreators(TaskFilter filter, SqlBuilder sql, List<Object> args) {
        return whereIn(filter.getCreators(), stream -> stream.filter(StringUtils::isNotEmpty), sql, args, "created_by");
    }

    private static boolean whereOrderBys(UndefinableValue<List<String>> orderByValue, SqlBuilder sql) {
        if (orderByValue == null || !orderByValue.defined()) {
            return true;
        }
        List<OrderBy> orderBys = Optional.ofNullable(orderByValue.get())
                .map(Collection::stream)
                .orElseGet(Stream::empty)
                .map(StringUtils::trim)
                .filter(StringUtils::isNotBlank)
                .map(TaskServiceImpl::parseSpecWord)
                .map(OrderBy::parse)
                .collect(Collectors.toList());
        if (orderBys.isEmpty()) {
            return false;
        }
        Sqls.orderBy(sql, orderBys);
        return true;
    }

    private static String parseSpecWord(String orderBy) {
        if (SPEC_WORD_LIST.stream().anyMatch(word -> word.equalsIgnoreCase(orderBy))) {
            return orderBy.replace("creator", "created_by");
        }
        return orderBy;
    }

    /**
     * 创建任务定义的ORM数据对象。
     *
     * @param tId 表示待创建的任务数据实体的唯一标识的 {@link String}。
     * @param declaration 表示任务声明的 {@link TaskDeclaration}。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @return 表示用任务标识，任务声明和上下文创建的任务ORM实体 {@link TaskObject}.
     */
    private TaskObject toTaskObject(String tId, TaskDeclaration declaration, OperationContext context) {
        String taskId = taskValidator.validateTaskId(tId, context);
        String name = taskValidator.validateName(declaration.getName().get(), context);
        modelengine.fit.jober.entity.OperationContext context1 = ParamUtils.convertOperationContext(context);
        Validation.notBlank(context.operator(),
                () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, context1, "operator"));
        TaskObject taskObject = new TaskObject();
        taskObject.setId(taskId);
        taskObject.setName(name);
        taskObject.setCategory(JaneCategory.valueOf(declaration.getCategory().withDefault("TASK")).name());
        taskObject.setTenantId(context.tenantId());
        taskObject.setAttributes(this.serializer.serialize(
                UndefinableValue.withDefault(declaration.getAttributes(), Collections.emptyMap())));
        taskObject.setCreatedBy(context.operator());
        taskObject.setCreatedAt(LocalDateTime.now());
        taskObject.setUpdatedBy(context.operator());
        taskObject.setUpdatedAt(LocalDateTime.now());

        // 设置task使用的模板，默认为空-32位0
        if (Objects.nonNull(declaration.getTemplateId()) && declaration.getTemplateId().defined()) {
            String templateId = declaration.getTemplateId().get();

            if (!taskTemplateRepo.exist(templateId)) {
                throw new NotFoundException(ErrorCodes.TASK_TEMPLATE_NOT_FOUND);
            }

            taskObject.setTemplateId(Entities.canonicalizeId(templateId));
        } else {
            taskObject.setTemplateId(taskTemplateRepo.defaultTemplateId());
        }
        return taskObject;
    }

    /**
     * 将taskObject转换为TaskEntity
     *
     * @param taskObject 表示存储数据库一行数据的具体实例
     * @param context context
     * @return 表示将数据实例转换为业务逻辑层存储数据的实例
     */
    private TaskEntity toTaskEntity(TaskObject taskObject, OperationContext context) {
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setId(taskObject.getId());
        taskEntity.setName(taskObject.getName());
        taskEntity.setTenantId(taskObject.getTenantId());
        taskEntity.setTemplateId(taskObject.getTemplateId());
        taskEntity.setCategory(JaneCategory.valueOf(taskObject.getCategory()));
        taskEntity.setAttributes(this.serializer.deserialize(taskObject.getAttributes()));
        taskEntity.setCreator(taskObject.getCreatedBy());
        taskEntity.setCreationTime(taskObject.getCreatedAt());
        taskEntity.setLastModifier(taskObject.getUpdatedBy());
        taskEntity.setLastModificationTime(taskObject.getUpdatedAt());
        taskEntity.setProperties(
                nullIf(this.taskPropertyRepo.list(taskObject.getId(), context), Collections.emptyList()));
        return taskEntity;
    }

    /**
     * 获取TaskPropertiesDeclarations
     *
     * @param taskId 任务的taskId
     * @param template 任务模板
     * @param propertyDeclarations 任务属性的声明集合
     * @return 表示用taskId和任务属性声明集合构建的TaskPropertiesDeclarations
     */
    private TaskPropertiesDeclaration getTaskPropertiesDeclarations(String taskId, TaskTemplate template,
            List<TaskProperty.Declaration> propertyDeclarations) {
        TaskPropertiesDeclaration taskPropertiesDeclaration = new TaskPropertiesDeclaration();
        taskPropertiesDeclaration.setTaskId(taskId);
        taskPropertiesDeclaration.setProperties(this.addDefaultTemplateIntoDeclaration(template, propertyDeclarations));

        taskPropertiesDeclaration.setTemplate(template);

        return taskPropertiesDeclaration;
    }

    private List<TaskProperty.Declaration> addDefaultTemplateIntoDeclaration(TaskTemplate template,
            List<TaskProperty.Declaration> propertyDeclarations) {
        if (Objects.isNull(template) || CollectionUtils.isEmpty(template.properties())) {
            return propertyDeclarations;
        }
        return propertyDeclarations.stream().map(pd -> {
            if (!pd.name().defined()) {
                return pd;
            }
            TaskTemplateProperty property = template.property(pd.name().get());
            if (Objects.isNull(property)) {
                return pd;
            }
            // 如果输入了模板ID，但是根据名称却找到了另外的模板，则认为发生冲突
            if (pd.templateId().defined() && !StringUtils.equalsIgnoreCase(pd.templateId().get(), property.id())) {
                throw new ConflictException(ErrorCodes.PROPERTY_TEMPLATE_EXCEPT_NOT_EQUALS_ACTUAL);
            }
            if (pd.templateId().defined()) {
                return pd;
            }
            TaskProperty.Declaration.Builder builder = TaskProperty.Declaration.custom()
                    .templateId(property.id())
                    .name(pd.name().get());
            pd.description().ifDefined(builder::description);
            pd.scope().ifDefined(builder::scope);
            pd.required().ifDefined(builder::isRequired);
            pd.identifiable().ifDefined(builder::isIdentifiable);
            pd.dataType().ifDefined(builder::dataType);
            pd.categories().ifDefined(builder::categories);
            pd.appearance().ifDefined(builder::appearance);
            return builder.build();
        }).collect(Collectors.toList());
    }

    /**
     * 用任务属性和任务资源的声明集合保存和该任务相关的属性和资源
     *
     * @param taskId 表示任务定义的唯一标识的 {@link String}。
     * @param declaration 表示任务声明的 {@link TaskDeclaration}。
     * @param template 任务模板
     * @param context 表示操作上下文的 {@link OperationContext}。
     */
    private void saveDeclarations(String taskId, TaskDeclaration declaration, TaskTemplate template,
            OperationContext context) {
        if (!declaration.getProperties().defined()) {
            return;
        }
        TaskPropertiesDeclaration taskPropertiesDeclaration = this.getTaskPropertiesDeclarations(taskId,
                template, declaration.getProperties().get());
        propertyService.batchSave(taskPropertiesDeclaration, context);
    }

    private void saveCategoryTriggers(String taskId, UndefinableValue<List<TaskCategoryTriggerDeclaration>> triggers) {
        if (triggers == null || !triggers.defined()) {
            return;
        }
        Map<String, List<String>> actualTriggers = this.defineCategoryTriggers(triggers);
        int count = actualTriggers.values().stream().mapToInt(List::size).sum();
        List<String> ids = Collections.emptyList();
        if (count > 0) {
            Map<String, String> categoryIds = this.categoryService.listByNames(actualTriggers.keySet())
                    .stream()
                    .collect(Collectors.toMap(CategoryEntity::getName, CategoryEntity::getId));
            StringBuilder sql = new StringBuilder();
            sql.append(Sqls.script(SQL_MODULE, "insert-trigger-prefix"));
            List<Object> args = new ArrayList<>(count << 2);
            for (Map.Entry<String, List<String>> entry : actualTriggers.entrySet()) {
                String categoryId = categoryIds.get(entry.getKey());
                for (String fitableId : entry.getValue()) {
                    sql.append(Sqls.script(SQL_MODULE, "insert-trigger-values")).append(", ");
                    args.addAll(Arrays.asList(Entities.generateId(), taskId, categoryId, fitableId));
                }
            }
            sql.setLength(sql.length() - 2);
            sql.append('\n').append(Sqls.script(SQL_MODULE, "insert-trigger-conflict"));
            sql.append(" DO NOTHING RETURNING id");
            List<Map<String, Object>> rows = this.executor.executeQuery(sql.toString(), args);
            ids = rows.stream().map(row -> ObjectUtils.<String>cast(row.get("id"))).collect(Collectors.toList());
        }
        SqlBuilder sqlBuilder = SqlBuilder.custom().append(Sqls.script(SQL_MODULE, "delete-trigger-by-task"));
        SqlBuilder sql = ids.size() > 0 ? sqlBuilder
                .append(" AND ").appendIdentifier("id").append(" NOT IN (").appendRepeatedly("?, ", ids.size())
                .backspace(2).append(')') : sqlBuilder;
        List<Object> args = new ArrayList<>(ids.size() + 1);
        args.add(taskId);
        args.addAll(ids);
        this.executor.executeUpdate(sql.toString(), args);
    }

    private void deleteCategoryTriggers(String taskId) {
        String sql = Sqls.script(SQL_MODULE, "delete-trigger-by-task");
        List<Object> args = Collections.singletonList(taskId);
        this.executor.executeUpdate(sql, args);
    }

    private Map<String, List<String>> defineCategoryTriggers(
            UndefinableValue<List<TaskCategoryTriggerDeclaration>> categoryTriggers) {
        Map<String, List<String>> results = new HashMap<>();
        List<TaskCategoryTriggerDeclaration> triggers = categoryTriggers.withDefault(Collections.emptyList());
        for (TaskCategoryTriggerDeclaration trigger : triggers) {
            String category = UndefinableValue.require(trigger.getCategory(),
                    () -> new BadRequestException(ErrorCodes.TASK_CATEGORY_TRIGGER_CATEGORY_REQUIRED));
            List<String> fitableIds = UndefinableValue.withDefault(trigger.getFitableIds(), Collections.emptyList());
            fitableIds = fitableIds.stream()
                    .filter(Objects::nonNull)
                    .map(String::trim)
                    .filter(fitableId -> !fitableId.isEmpty())
                    .collect(Collectors.toList());
            results.computeIfAbsent(category, key -> new ArrayList<>()).addAll(fitableIds);
        }
        return results;
    }

    private void fillCategoryTriggers(List<TaskEntity> tasks) {
        if (tasks.isEmpty()) {
            return;
        }
        Map<String, TaskEntity> taskMap = tasks.stream()
                .collect(Collectors.toMap(TaskEntity::getId, Function.identity()));
        SqlBuilder sql = SqlBuilder.custom().append(Sqls.script(SQL_MODULE, "select-trigger")).append("WHERE ")
                .appendIdentifier("task_id").append(" IN (").appendRepeatedly("?, ", taskMap.size()).backspace(2)
                .append(')');
        List<Object> args = new ArrayList<>(taskMap.keySet());
        List<Map<String, Object>> rows = this.executor.executeQuery(sql.toString(), args);
        Set<String> categoryIds =
                rows.stream().map(row -> ObjectUtils.<String>cast(row.get("category_id"))).collect(Collectors.toSet());
        List<CategoryEntity> categories = this.categoryService.listByIds(categoryIds);
        Map<String, String> categoryNames = categories.stream()
                .collect(Collectors.toMap(CategoryEntity::getId, CategoryEntity::getName));
        Map<String, Map<String, List<String>>> group = new HashMap<>();
        for (Map<String, Object> row : rows) {
            String categoryId = ObjectUtils.cast(row.get("category_id"));
            String categoryName = categoryNames.get(categoryId);
            if (categoryName == null) {
                continue;
            }
            String taskId = ObjectUtils.cast(row.get("task_id"));
            String fitableId = ObjectUtils.cast(row.get("fitable_id"));
            group.computeIfAbsent(taskId, key -> new HashMap<>())
                    .computeIfAbsent(categoryName, key -> new ArrayList<>())
                    .add(fitableId);
        }
        for (TaskEntity task : tasks) {
            Map<String, List<String>> taskCategoryTriggers = group.get(task.getId());
            if (taskCategoryTriggers == null) {
                task.setCategoryTriggers(Collections.emptyList());
            } else {
                task.setCategoryTriggers(taskCategoryTriggers.entrySet().stream().map(entry -> {
                    TaskCategoryTriggerEntity trigger = new TaskCategoryTriggerEntity();
                    trigger.setCategory(entry.getKey());
                    trigger.setFitableIds(entry.getValue());
                    return trigger;
                }).collect(Collectors.toList()));
            }
        }
    }

    private void fillTypes(List<TaskEntity> tasks, OperationContext context) {
        List<String> taskIds = tasks.stream().map(TaskEntity::getId).collect(Collectors.toList());
        Map<String, List<TaskType>> taskTypes = this.taskTypeRepo.list(taskIds, context);
        for (TaskEntity task : tasks) {
            List<TaskType> types = taskTypes.get(task.getId());
            types = nullIf(types, Collections.emptyList());
            task.setTypes(types);
        }
    }

    private void fillProperties(List<TaskEntity> tasks, OperationContext context) {
        if (tasks.isEmpty()) {
            return;
        }
        List<String> taskIds = tasks.stream().map(TaskEntity::getId).collect(Collectors.toList());
        Map<String, List<TaskProperty>> taskProperties = this.taskPropertyRepo.list(taskIds, context);
        for (TaskEntity task : tasks) {
            List<TaskProperty> properties = taskProperties.get(task.getId());
            properties = nullIf(properties, Collections.emptyList());
            task.setProperties(properties);
        }
    }

    private void fillIndexes(List<TaskEntity> tasks, OperationContext context) {
        List<Index> indexes = this.indexRepo.list(tasks, context);
        Map<TaskEntity, List<Index>> groups = indexes.stream().collect(Collectors.groupingBy(Index::task));
        for (Map.Entry<TaskEntity, List<Index>> entry : groups.entrySet()) {
            entry.getKey().setIndexes(entry.getValue());
        }
    }
}
