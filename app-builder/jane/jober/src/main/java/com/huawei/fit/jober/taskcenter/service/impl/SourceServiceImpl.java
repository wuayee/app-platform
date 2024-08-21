/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.service.impl;

import static com.huawei.fit.jober.common.ErrorCodes.INPUT_PARAM_IS_EMPTY;
import static com.huawei.fit.jober.taskcenter.util.Sqls.longValue;

import com.huawei.fit.jane.task.util.Entities;
import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jane.task.util.UndefinableValue;
import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.ServerInternalException;
import com.huawei.fit.jober.common.aop.ObjectTypeEnum;
import com.huawei.fit.jober.common.aop.OperateEnum;
import com.huawei.fit.jober.common.aop.OperationRecord;
import com.huawei.fit.jober.common.aop.TenantAuthentication;
import com.huawei.fit.jober.common.event.CommonSourceEvent;
import com.huawei.fit.jober.common.event.ScheduleSourceEvent;
import com.huawei.fit.jober.common.event.entity.SourceMetaData;
import com.huawei.fit.jober.common.exceptions.BadRequestException;
import com.huawei.fit.jober.common.exceptions.JobberParamException;
import com.huawei.fit.jober.common.exceptions.NotFoundException;
import com.huawei.fit.jober.taskcenter.dao.SourceMapper;
import com.huawei.fit.jober.taskcenter.dao.po.SourceObject;
import com.huawei.fit.jober.taskcenter.declaration.InstanceEventDeclaration;
import com.huawei.fit.jober.taskcenter.declaration.SourceDeclaration;
import com.huawei.fit.jober.taskcenter.declaration.SourceTriggersDeclaration;
import com.huawei.fit.jober.taskcenter.domain.InstanceEvent;
import com.huawei.fit.jober.taskcenter.domain.SourceEntity;
import com.huawei.fit.jober.taskcenter.domain.SourceType;
import com.huawei.fit.jober.taskcenter.domain.TaskEntity;
import com.huawei.fit.jober.taskcenter.domain.TaskInstance;
import com.huawei.fit.jober.taskcenter.domain.TriggerEntity;
import com.huawei.fit.jober.taskcenter.filter.TriggerFilter;
import com.huawei.fit.jober.taskcenter.service.InstanceEventService;
import com.huawei.fit.jober.taskcenter.service.SourceService;
import com.huawei.fit.jober.taskcenter.service.TaskService;
import com.huawei.fit.jober.taskcenter.service.TriggerService;
import com.huawei.fit.jober.taskcenter.service.adapter.SourceAdapter;
import com.huawei.fit.jober.taskcenter.util.DynamicSqlExecutor;
import com.huawei.fit.jober.taskcenter.util.Enums;
import com.huawei.fit.jober.taskcenter.util.Sqls;
import com.huawei.fit.jober.taskcenter.util.sql.InsertSql;
import com.huawei.fit.jober.taskcenter.validation.RelationshipValidator;
import com.huawei.fit.jober.taskcenter.validation.SourceValidator;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.model.RangedResultSet;
import modelengine.fitframework.plugin.Plugin;
import modelengine.fitframework.transaction.Transactional;
import modelengine.fitframework.util.LazyLoader;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * {@link SourceService} 的默认实现类。
 *
 * @author 陈镕希
 * @since 2023-08-08
 */
@Component
public class SourceServiceImpl implements SourceService {
    private static final Logger log = Logger.get(SourceServiceImpl.class);

    private static final String DELETE_EVENT_TYPE = "delete";

    private static final String CREATE_EVENT_TYPE = "create";

    private static final String UPDATE_EVENT_TYPE = "update";

    private final SourceValidator sourceValidator;

    private final TriggerService triggerService;

    private final SourceMapper sourceMapper;

    private final DynamicSqlExecutor executor;

    private final EventPublishServiceImpl eventPublishService;

    private final Map<SourceType, SourceAdapter> adapters;

    private final InstanceEventService instanceEventService;

    private final RelationshipValidator relationshipValidator;

    private final LazyLoader<TaskInstance.Repo> instanceServiceLazyLoader;

    private final LazyLoader<TaskService> taskServiceLazyLoader;

    public SourceServiceImpl(SourceValidator sourceValidator, TriggerService triggerService, SourceMapper sourceMapper,
            DynamicSqlExecutor executor, EventPublishServiceImpl eventPublishService, List<SourceAdapter> adapterList,
            InstanceEventService instanceEventService, RelationshipValidator relationshipValidator, Plugin plugin) {
        this.sourceValidator = sourceValidator;
        this.triggerService = triggerService;
        this.sourceMapper = sourceMapper;
        this.executor = executor;
        this.eventPublishService = eventPublishService;
        this.adapters = adapterList.stream().collect(Collectors.toMap(SourceAdapter::getType, Function.identity()));
        this.instanceEventService = instanceEventService;
        this.relationshipValidator = relationshipValidator;
        this.instanceServiceLazyLoader = new LazyLoader<>(
                () -> plugin.container().beans().get(TaskInstance.Repo.class));
        this.taskServiceLazyLoader = new LazyLoader<>(() -> plugin.container().beans().get(TaskService.class));
    }

    // 需要删除对 TaskService 的引用。
    //  当前实现引用 TaskService 的原因，是需要在调用 TaskInstance.Repo 接口时需要传入 TaskEntity，需要从 TaskService 中查询。
    //  后续需要对当前实现进行重构，重构后的接口方法中应直接接收一个 TaskEntity 实例而非 taskId，不需要从 TaskService 查询。
    //  对 TaskEntity 的查询交给调用方来进行。
    private TaskService taskService() {
        return this.taskServiceLazyLoader.get();
    }

    @Override
    @Transactional
    @TenantAuthentication
    @OperationRecord(objectId = -1, objectIdGetMethodName = "getId", objectType = ObjectTypeEnum.SOURCE,
            operate = OperateEnum.CREATED, declaration = 2)
    public SourceEntity create(String taskId, String typeId, SourceDeclaration declaration, OperationContext context) {
        String actualTaskId = sourceValidator.validateTaskId(taskId);
        relationshipValidator.validateTaskExistInTenant(actualTaskId, context.tenantId());
        relationshipValidator.validateTaskTypeExistInTask(typeId, actualTaskId);
        // 兼容逻辑，暂时可不传入所属任务类型的唯一标识，并根据名称自动获取，后续要求必须传入。
        String actualTypeId;
        if (typeId == null) {
            String sourceName = UndefinableValue.require(declaration.getName(),
                    () -> new BadRequestException(ErrorCodes.SOURCE_NAME_REQUIRED));
            actualTypeId = this.getTypeId(actualTaskId, sourceName);
        } else {
            actualTypeId = Entities.validateId(typeId, () -> new BadRequestException(ErrorCodes.TYPE_ID_INVALID));
            String sourceName = this.getTypeName(actualTypeId);
            if (sourceName == null) {
                throw new NotFoundException(ErrorCodes.TYPE_NOT_FOUND);
            } else {
                declaration.setName(UndefinableValue.defined(sourceName));
            }
        }

        SourceObject sourceObject = this.convert(actualTaskId, declaration);
        this.saveTaskTypeRelation(actualTypeId, sourceObject.getId(), context);
        this.sourceMapper.insert(sourceObject);
        SourceEntity sourceEntity = this.adapterOf(sourceObject.getType())
                .createExtension(sourceObject, declaration, context);

        declaration.getTriggers().ifDefined((triggers -> {
            SourceTriggersDeclaration sourceTriggersDeclaration = new SourceTriggersDeclaration();
            sourceTriggersDeclaration.setSourceId(sourceObject.getId());
            sourceTriggersDeclaration.setTriggers(triggers);
            triggerService.batchSave(actualTaskId, Collections.singletonList(sourceTriggersDeclaration), context);
        }));
        this.instanceEventService.save(Collections.singletonMap(sourceEntity.getId(),
                UndefinableValue.withDefault(declaration.getEvents(), Collections.emptyList())), context);
        this.sendScheduleSourceEvent(actualTypeId, sourceObject, CREATE_EVENT_TYPE);
        this.fillTriggers(Collections.singletonList(sourceEntity), context);
        this.fillInstanceEvents(Collections.singletonList(sourceEntity));
        return sourceEntity;
    }

    private String getTypeName(String typeId) {
        String sql = "SELECT name FROM task_type WHERE id = ?";
        List<Object> args = Collections.singletonList(typeId);
        return ObjectUtils.cast(this.executor.executeScalar(sql, args));
    }

    private SourceAdapter adapterOf(String type) {
        SourceType sourceType = Enums.parse(SourceType.class, type);
        return this.adapters.get(sourceType);
    }

    private String getTypeId(SourceObject sourceObject) {
        return this.getTypeId(sourceObject.getTaskId(), sourceObject.getName());
    }

    private String getTypeId(String taskId, String sourceName) {
        List<Map<String, Object>> rows = executor.executeQuery(
                "SELECT id, name FROM task_type WHERE tree_id = (SELECT tree_id FROM task_tree_task WHERE task_id = ?)",
                Collections.singletonList(taskId));
        Optional<Map<String, Object>> optionalMap = rows.stream()
                .filter(row -> StringUtils.equals(ObjectUtils.cast(row.get("name")), sourceName))
                .findAny();
        if (!optionalMap.isPresent()) {
            log.error("Cannot find specify type by task_id {} and node_name {}", taskId, sourceName);
            throw new NotFoundException(ErrorCodes.NODE_NOT_FOUND);
        }
        return ObjectUtils.cast(optionalMap.get().get("id"));
    }

    private void saveTaskTypeRelation(String typeId, String sourceId, OperationContext context) {
        InsertSql.custom()
                .into("task_node_source")
                .value("id", Entities.generateId())
                .value("node_id", typeId)
                .value("source_id", sourceId)
                .value("created_by", context.operator())
                .value("created_at", LocalDateTime.now())
                .execute(this.executor);
    }

    private void deleteTaskTypeRelation(String sourceId) {
        String sql = "DELETE FROM \"task_node_source\" WHERE source_id = ?";
        List<Object> args = Collections.singletonList(sourceId);
        this.executor.executeUpdate(sql, args);
    }

    @Override
    @Transactional
    @TenantAuthentication
    @OperationRecord(objectId = 2, objectType = ObjectTypeEnum.SOURCE, operate = OperateEnum.UPDATED, declaration = 3)
    public void patch(String taskId, String typeId, String sourceId, SourceDeclaration declaration,
            OperationContext context) {
        String actualTaskId = sourceValidator.validateTaskId(taskId);
        String actualSourceId = sourceValidator.validateSourceId(sourceId);
        this.validateIdRelation(typeId, actualTaskId, actualSourceId, context);
        List<Object> parameterList = new LinkedList<>();
        StringBuilder builder = new StringBuilder("UPDATE task_source SET ");
        List<String> patchColumnList = new LinkedList<>();
        declaration.getApp().ifDefined(app -> {
            patchColumnList.add("app = ?");
            parameterList.add(app);
        });
        declaration.getType().ifDefined(type -> {
            patchColumnList.add("type = ?");
            parameterList.add(type);
        });
        if (!patchColumnList.isEmpty()) {
            builder.append(String.join(",", patchColumnList)).append(" WHERE task_id = ? AND id = ?");
            parameterList.add(actualTaskId);
            parameterList.add(actualSourceId);
            if (executor.executeUpdate(builder.toString(), parameterList) != 1) {
                throw new ServerInternalException("Failed to patch source of database.");
            }
        }
        // 兼容逻辑，暂时可不传入所属任务类型的唯一标识，并根据名称自动获取，后续要求必须传入。
        SourceObject sourceObject = sourceMapper.select(actualSourceId);
        String actualTypeId = typeId == null
                ? this.getTypeId(sourceObject)
                : Entities.validateId(typeId, () -> new BadRequestException(ErrorCodes.TYPE_ID_INVALID));
        if (declaration.getType().defined()) {
            // 若declaration中有type，说明变化了，此时需要删除旧的，新建新的
            SourceObject sourceObjectAfterPatch = sourceMapper.select(actualSourceId);
            adapters.get(Enums.parse(SourceType.class, sourceObject.getType()))
                    .deleteExtension(actualSourceId, context);
            this.sendScheduleSourceEvent(actualTypeId, sourceObject, DELETE_EVENT_TYPE);
            adapters.get(Enums.parse(SourceType.class, declaration.getType().get()))
                    .createExtension(sourceObjectAfterPatch, declaration, context);
            this.sendScheduleSourceEvent(actualTypeId, sourceObjectAfterPatch, CREATE_EVENT_TYPE);
        } else {
            // declaration中没有type，直接更新即可
            adapters.get(Enums.parse(SourceType.class, sourceObject.getType()))
                    .patchExtension(sourceObject, declaration, context);
            this.sendScheduleSourceEvent(actualTypeId, sourceObject, UPDATE_EVENT_TYPE);
        }
        declaration.getTriggers().ifDefined(triggers -> {
            SourceTriggersDeclaration sourceTriggersDeclaration = new SourceTriggersDeclaration();
            sourceTriggersDeclaration.setSourceId(actualSourceId);
            sourceTriggersDeclaration.setTriggers(triggers);
            triggerService.batchSave(actualTaskId, Collections.singletonList(sourceTriggersDeclaration), context);
        });
        if (declaration.getEvents() != null && declaration.getEvents().defined()) {
            List<InstanceEventDeclaration> events = declaration.getEvents().withDefault(Collections.emptyList());
            this.instanceEventService.save(Collections.singletonMap(actualSourceId, events), context);
        }
    }

    private void validateIdRelation(String typeId, String actualTaskId, String actualSourceId,
            OperationContext context) {
        relationshipValidator.validateTaskExistInTenant(actualTaskId, context.tenantId());
        relationshipValidator.validateTaskTypeExistInTask(typeId, actualTaskId);
        relationshipValidator.validateSourceExistInTaskType(actualSourceId, typeId);
    }

    private void sendScheduleSourceEvent(String typeId, SourceObject sourceObject, String eventType) {
        eventPublishService.sendEvent(new ScheduleSourceEvent(eventPublishService, SourceMetaData.builder()
                .taskId(sourceObject.getTaskId())
                .typeId(typeId)
                .taskSourceId(sourceObject.getId())
                .type(sourceObject.getType())
                .build(), eventType));
    }

    private void sendCommonSourceEvent(String typeId, SourceObject sourceObject, String eventType) {
        eventPublishService.sendEvent(new CommonSourceEvent(eventPublishService, SourceMetaData.builder()
                .taskId(sourceObject.getTaskId())
                .typeId(typeId)
                .taskSourceId(sourceObject.getId())
                .type(sourceObject.getType())
                .build(), eventType));
    }

    @Override
    @Transactional
    @TenantAuthentication
    @OperationRecord(objectId = 2, objectType = ObjectTypeEnum.SOURCE, operate = OperateEnum.DELETED)
    public void delete(String taskId, String typeId, String sId, OperationContext context) {
        sourceValidator.validateTaskId(taskId);
        String actualSourceId = sourceValidator.validateSourceId(sId);
        Validation.notBlank(actualSourceId, () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, "sourceId"));
        this.validateIdRelation(typeId, taskId, actualSourceId, context);
        SourceObject sourceObject = sourceMapper.select(actualSourceId);
        // 先删除instance，再去删除source
        TaskEntity task = this.taskService().retrieve(taskId, context);
        instanceServiceLazyLoader.get().deleteBySource(task, sId, context);
        sourceMapper.delete(actualSourceId);
        this.adapterOf(sourceObject.getType()).deleteExtension(actualSourceId, context);
        // 兼容逻辑，暂时可不传入所属任务类型的唯一标识，并根据名称自动获取，后续要求必须传入。
        String actualTypeId;
        if (typeId == null) {
            actualTypeId = this.getTypeId(sourceObject);
        } else {
            actualTypeId = Entities.validateId(typeId, () -> new BadRequestException(ErrorCodes.TYPE_ID_INVALID));
        }
        this.sendScheduleSourceEvent(actualTypeId, sourceObject, DELETE_EVENT_TYPE);
        this.sendCommonSourceEvent(actualTypeId, sourceObject, DELETE_EVENT_TYPE);
        this.deleteTaskTypeRelation(actualSourceId);
        TriggerFilter filter = new TriggerFilter();
        filter.setIds(UndefinableValue.undefined());
        filter.setPropertyIds(UndefinableValue.undefined());
        filter.setFitableIds(UndefinableValue.undefined());
        filter.setSourceIds(UndefinableValue.defined(Collections.singletonList(actualSourceId)));
        triggerService.delete(taskId, filter, context);
        this.instanceEventService.deleteByTaskSources(Collections.singletonList(actualSourceId));
    }

    @Override
    public SourceEntity retrieve(String taskId, String typeId, String sourceId, OperationContext context) {
        sourceValidator.validateTaskId(taskId);
        // 兼容性逻辑，后续为必传
        if (typeId != null) {
            sourceValidator.validateTypeId(typeId);
        }
        String actualSourceId = sourceValidator.validateSourceId(sourceId);
        SourceObject sourceObject = sourceMapper.select(actualSourceId);
        SourceEntity sourceEntity = this.adapterOf(sourceObject.getType()).retrieveExtension(sourceObject, context);
        this.fillTriggers(Collections.singletonList(sourceEntity), context);
        this.fillInstanceEvents(Collections.singletonList(sourceEntity));
        return sourceEntity;
    }

    @Override
    @Transactional
    public List<SourceEntity> list(String taskId, String typeId, OperationContext context) {
        this.sourceValidator.validateTaskId(taskId);
        String actualTypeId = this.sourceValidator.validateTypeId(typeId);
        String sql = "SELECT \"ts\".\"id\", \"ts\".\"task_id\", \"ts\".\"app\", \"ts\".\"type\", \"ts\".\"name\", "
                + "\"tns\".\"node_id\" AS \"task_type_id\" FROM \"task_source\" AS \"ts\" INNER JOIN "
                + "\"task_node_source\" AS \"tns\" ON \"tns\".\"source_id\" = \"ts\".\"id\" WHERE "
                + "\"tns\".\"node_id\" = ?";
        List<Object> args = Collections.singletonList(actualTypeId);
        List<Map<String, Object>> rows = this.executor.executeQuery(sql, args);
        List<SourceObject> sourceObjects = new ArrayList<>(rows.size());
        for (Map<String, Object> row : rows) {
            SourceObject object = new SourceObject();
            object.setId(ObjectUtils.cast(row.get("id")));
            object.setTaskId(ObjectUtils.cast(row.get("task_id")));
            object.setName(ObjectUtils.cast(row.get("name")));
            object.setApp(ObjectUtils.cast(row.get("app")));
            object.setType(ObjectUtils.cast(row.get("type")));
            sourceObjects.add(object);
        }
        Map<String, List<SourceEntity>> sources = getSourceMap(context, sourceObjects);
        List<SourceEntity> results = sources.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
        this.fillTriggers(results, context);
        this.fillInstanceEvents(results);
        return results;
    }

    @Override
    public Map<String, List<SourceEntity>> list(List<String> tIds, OperationContext context) {
        List<String> taskIds = tIds.stream().map(sourceValidator::validateTaskId).collect(Collectors.toList());
        List<SourceObject> sourceObjects = sourceMapper.selectByTaskIds(taskIds);
        Map<String, List<SourceEntity>> sources = getSourceMap(context, sourceObjects);
        this.fillTriggers(sources.values().stream().flatMap(Collection::stream).collect(Collectors.toList()), context);
        this.fillInstanceEvents(sources.values().stream().flatMap(Collection::stream).collect(Collectors.toList()));
        return sources;
    }

    @Override
    public RangedResultSet<SourceEntity> listBySourceIds(List<String> sourceIds, long offset, int limit,
            OperationContext context) {
        List<String> actualSourceIds =
                sourceIds.stream().map(sourceValidator::validateSourceId).collect(Collectors.toList());
        List<SourceObject> sourceObjects = sourceMapper.selectBySourceIds(actualSourceIds, offset, limit);
        Map<String, List<SourceObject>> typeObjectMap = sourceObjects.stream()
                .collect(Collectors.groupingBy(SourceObject::getType));
        List<SourceEntity> sourceEntities = typeObjectMap.entrySet()
                .stream()
                .map(entry -> adapters.get(Enums.parse(SourceType.class, entry.getKey()))
                        .listExtension(entry.getValue(), context))
                .flatMap(map -> map.entrySet().stream())
                .map(Map.Entry::getValue)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        StringBuilder whereSql = new StringBuilder();
        whereSql.append(" WHERE 1 = 1");
        Sqls.andIn(whereSql, "id", actualSourceIds.size());
        List<Object> whereArgs = new ArrayList<>(actualSourceIds);
        String countSql = "SELECT COUNT(1) FROM task_source" + whereSql;
        long count = longValue(this.executor.executeScalar(countSql, whereArgs));
        this.fillTriggers(sourceEntities, context);
        this.fillInstanceEvents(sourceEntities);
        return RangedResultSet.create(sourceEntities, (int) offset, limit, (int) count);
    }

    private Map<String, List<SourceEntity>> getSourceMap(OperationContext context,
            List<SourceObject> sourceObjects) {
        Map<String, List<SourceObject>> typeObjectMap = sourceObjects.stream()
                .collect(Collectors.groupingBy(SourceObject::getType));
        return typeObjectMap.entrySet()
                .stream()
                .map(entry -> adapters.get(Enums.parse(SourceType.class, entry.getKey()))
                        .listExtension(entry.getValue(), context))
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (list1, list2) -> {
                    List<SourceEntity> mergedList1 = new ArrayList<>(list1);
                    mergedList1.addAll(list2);
                    return mergedList1;
                }));
    }

    private SourceObject convert(String taskId, SourceDeclaration declaration) {
        return SourceObject.builder()
                .id(Entities.generateId())
                .taskId(taskId)
                .name(sourceValidator.validateSourceName(declaration.getName().get()))
                .app(sourceValidator.validateSourceApp(declaration.getApp().get()))
                .type(Enums.toString(sourceValidator.validateSourceType(declaration.getType().get())))
                .build();
    }

    private void fillTriggers(List<SourceEntity> sources, OperationContext context) {
        TriggerFilter filter = new TriggerFilter();
        filter.setIds(UndefinableValue.undefined());
        filter.setPropertyIds(UndefinableValue.undefined());
        filter.setFitableIds(UndefinableValue.undefined());
        filter.setSourceIds(
                UndefinableValue.defined(sources.stream().map(SourceEntity::getId).collect(Collectors.toList())));
        Map<String, List<TriggerEntity>> triggers = this.triggerService.list(filter, context);
        for (SourceEntity source : sources) {
            source.setTriggers(triggers.getOrDefault(source.getId(), Collections.emptyList()));
        }
    }

    private void fillInstanceEvents(List<SourceEntity> sources) {
        List<String> sourceIds = sources.stream().map(SourceEntity::getId).collect(Collectors.toList());
        Map<String, List<InstanceEvent>> events = this.instanceEventService.lookupByTaskSources(sourceIds);
        for (SourceEntity source : sources) {
            source.setEvents(events.getOrDefault(source.getId(), Collections.emptyList()));
        }
    }
}
