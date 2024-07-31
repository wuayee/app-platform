/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.common.aop;

import static com.huawei.fit.jober.common.aop.OperateEnum.CREATED;
import static com.huawei.fit.jober.common.aop.OperateEnum.DELETED;
import static com.huawei.fit.jober.common.aop.OperateEnum.RELADD;
import static com.huawei.fit.jober.common.aop.OperateEnum.RELDEL;
import static com.huawei.fit.jober.common.aop.OperateEnum.UPDATED;

import com.huawei.fit.jane.task.domain.TaskProperty;
import com.huawei.fit.jane.task.domain.TaskRelation;
import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jane.task.util.UndefinableValue;
import com.huawei.fit.jober.common.ServerInternalException;
import com.huawei.fit.jober.taskcenter.declaration.InstanceEventDeclaration;
import com.huawei.fit.jober.taskcenter.declaration.OperationRecordDeclaration;
import com.huawei.fit.jober.taskcenter.declaration.SourceDeclaration;
import com.huawei.fit.jober.taskcenter.declaration.TaskCategoryTriggerDeclaration;
import com.huawei.fit.jober.taskcenter.declaration.TaskDeclaration;
import com.huawei.fit.jober.taskcenter.declaration.TriggerDeclaration;
import com.huawei.fit.jober.taskcenter.domain.TaskEntity;
import com.huawei.fit.jober.taskcenter.domain.TaskInstance;
import com.huawei.fit.jober.taskcenter.domain.TaskType;
import com.huawei.fit.jober.taskcenter.service.OperationRecordService;
import com.huawei.fit.jober.taskcenter.service.TaskService;
import com.huawei.fit.jober.taskcenter.util.DynamicSqlExecutor;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Order;
import com.huawei.fitframework.aop.ProceedingJoinPoint;
import com.huawei.fitframework.aop.annotation.Around;
import com.huawei.fitframework.aop.annotation.Aspect;
import com.huawei.fitframework.aop.annotation.Pointcut;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;

import lombok.RequiredArgsConstructor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 为数据操作（增删改）提供操作记录功能
 *
 * @author yWX1299574
 * @since 2023-11-16 15:13
 */
@Aspect
@Component
@RequiredArgsConstructor
public class OperationRecordAspect {
    private static final Logger log = Logger.get(OperationRecordAspect.class);

    private final OperationRecordService service;

    private final DynamicSqlExecutor executor;

    private final TaskService taskService;

    private final TaskInstance.Repo repo;

    private final TaskRelation.Repo relationRepo;

    @Pointcut("@annotation(com.huawei.fit.jober.common.aop.OperationRecord)")
    private void operationRecordPointCut() {
    }

    /**
     * 记录操作
     *
     * @param pjp 处理点
     * @return 操作对象
     * @throws Throwable 当注解id不对时抛出异常
     */
    @Around("operationRecordPointCut()")
    @Order(200)
    public Object recordOperation(ProceedingJoinPoint pjp) throws Throwable {
        OperationRecord annotation = pjp.getMethod().getAnnotation(OperationRecord.class);
        this.validateAnnotation(annotation);

        OperationContext context = this.getOperationContext(annotation.context(), pjp.getArgs());
        TaskRelation taskRelation = annotation.operate().equals(RELDEL) ? this.relationRepo.retrieve(
                ObjectUtils.cast(pjp.getArgs()[0]), context) : null;

        Object result = pjp.proceed();

        String objectId;
        if (annotation.objectId() == -2) {
            assert taskRelation != null;
            objectId = taskRelation.objectId1();
        } else if (annotation.objectId() >= 0 && pjp.getArgs().length > annotation.objectId()) {
            objectId = ObjectUtils.cast(pjp.getArgs()[annotation.objectId()]) ;
        } else if (annotation.objectId() == -1) {
            objectId = this.getObjectIdByMethod(result, annotation.objectIdGetMethodName());
        } else {
            throw new ServerInternalException("Method args length less than id index.");
        }

        String message = generateMessage(pjp.getArgs(), annotation, taskRelation, objectId);

        OperationRecordDeclaration declaration = new OperationRecordDeclaration();
        declaration.setOperate(UndefinableValue.defined(annotation.operate().name()));
        declaration.setObjectId(UndefinableValue.defined(objectId));
        declaration.setObjectType(UndefinableValue.defined(annotation.objectType().name()));
        declaration.setMessage(UndefinableValue.defined(message));
        service.create(declaration, context);
        return result;
    }

    private String generateMessage(Object[] args, OperationRecord annotation, TaskRelation taskRelation,
            String objectId) {
        String message;
        if (annotation.operate().equals(RELDEL)) {
            JSONObject json = new JSONObject();
            json.put("detail", objectId);
            json.put("title", annotation.operate().getDescription() + annotation.objectType().getObjectTypeName());
            Map<String, Object> delResult = new HashMap<>();
            String sql = "select task_id from task_instance_wide where id = ?";
            String deleteMessage = "取消" + Objects.requireNonNull(getTaskEntity(sql, taskRelation.objectId1()))
                    .getName()
                    + "实例" + "【" + Objects.requireNonNull(getTaskInstance(sql, taskRelation.objectId1()))
                    .info()
                    .get("title") + "】" + "对" + Objects.requireNonNull(getTaskEntity(sql, taskRelation.objectId2()))
                    .getName() + "实例" + "【" + Objects.requireNonNull(getTaskInstance(sql, taskRelation.objectId2()))
                    .info()
                    .get("title") + "】" + "的关联";
            delResult.put("message", deleteMessage);
            json.put("declaration", delResult);
            message = json.toJSONString();
        } else {
            message = generateCreatedMessage(annotation, args, annotation.declaration(), objectId);
        }
        return message;
    }

    private static String getNameFromArg(OperationRecord annotation, Object arg) {
        if (Objects.isNull(arg)) {
            return "";
        }
        String detail;
        if (ObjectTypeEnum.INSTANCE.equals(annotation.objectType()) && arg instanceof TaskInstance.Declaration) {
            TaskInstance.Declaration declaration = (TaskInstance.Declaration) arg;
            detail = ObjectUtils.cast(declaration.info().get().get("title"));
        } else if (ObjectTypeEnum.TASK.equals(annotation.objectType()) && arg instanceof TaskDeclaration) {
            TaskDeclaration declaration = (TaskDeclaration) arg;
            detail = declaration.getName().get();
        } else if (ObjectTypeEnum.TASK_TYPE.equals(annotation.objectType()) && arg instanceof TaskType.Declaration) {
            TaskType.Declaration declaration = (TaskType.Declaration) arg;
            detail = declaration.name().get();
        } else if (ObjectTypeEnum.SOURCE.equals(annotation.objectType()) && arg instanceof SourceDeclaration) {
            SourceDeclaration declaration = (SourceDeclaration) arg;
            detail = declaration.getName().get();
        } else {
            throw new ServerInternalException("Can't convert arg to object type declaration.");
        }
        return detail;
    }

    private void validateAnnotation(OperationRecord annotation) {
        if (Objects.isNull(annotation.objectType())) {
            throw new ServerInternalException("Annotation field objectType is null.");
        }

        if (Objects.isNull(annotation.operate())) {
            throw new ServerInternalException("Annotation field operate is null.");
        }

        if (annotation.objectId() == -1 && StringUtils.isEmpty(annotation.objectIdGetMethodName())) {
            throw new ServerInternalException("Annotation field objectIdGetMethodName is empty.");
        }
    }

    private String generateCreatedMessage(OperationRecord annotation, Object[] args, int declarationIndex,
            String objectId) {
        JSONObject json = new JSONObject();

        String title = annotation.operate().getDescription() + annotation.objectType().getObjectTypeName();
        Object declaration = declarationIndex < 0 ? null : args[declarationIndex];
        if (CREATED.equals(annotation.operate())) {
            String detail = getNameFromArg(annotation, declaration);
            json.put("detail", detail);
        } else if (UPDATED.equals(annotation.operate()) || DELETED.equals(annotation.operate())
                || RELADD.equals(annotation.operate())) {
            json.put("detail", objectId);
        } else {
            throw new ServerInternalException("No such operate : " + annotation.operate().name());
        }
        json.put("title", title);
        try {
            json.put("declaration", declarationConvertToNoUndefineValue(annotation, declaration));
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new ServerInternalException("Convert declaration UndefinableValue failed: " + e.getMessage());
        }
        return json.toJSONString();
    }

    private OperationContext getOperationContext(int index, Object[] args) {
        int contextIndex = index;
        if (contextIndex == -1) {
            contextIndex = args.length - 1;
        }

        if (args.length > contextIndex) {
            return ObjectUtils.cast(args[contextIndex]) ;
        } else {
            throw new ServerInternalException("Method args length less than id index.");
        }
    }

    private String getObjectIdByMethod(Object result, String getIdMethodName)
            throws IllegalAccessException, InvocationTargetException {
        Method[] methods = result.getClass().getMethods();
        for (Method method : methods) {
            if (Objects.equals(method.getName(), getIdMethodName) || Objects.equals(method.getName(), "getId")) {
                return ObjectUtils.cast(method.invoke(result)) ;
            }
        }
        throw new ServerInternalException("Couldn't get object id from result.");
    }

    private Object declarationConvertToNoUndefineValue(OperationRecord annotation, Object declaration)
            throws InvocationTargetException, IllegalAccessException {
        if (declaration == null) {
            return null;
        }

        Map<String, Object> result = new HashMap<>();
        if (ObjectTypeEnum.TASK.equals(annotation.objectType()) && declaration instanceof TaskDeclaration) {
            TaskDeclaration task = (TaskDeclaration) declaration;
            task.getName().ifDefined((name) -> result.put("name", name));
            task.getAttributes().ifDefined((attributes) -> result.put("attributes", attributes));
            task.getCategoryTriggers().ifDefined(
                    (triggers) -> result.put("categoryTriggers", this.convertCategoryTriggersToMap(triggers)));
            task.getProperties().ifDefined(
                    (properties) -> result.put("properties", this.convertTaskPropertyDeclaration(properties)));
        } else if (ObjectTypeEnum.TASK_TYPE.equals(annotation.objectType())
                && declaration instanceof TaskType.Declaration) {
            TaskType.Declaration taskType = (TaskType.Declaration) declaration;
            taskType.name().ifDefined((name) -> result.put("name", name));
            taskType.parentId().ifDefined((parentId) -> result.put("parentId", parentId));
        } else if (ObjectTypeEnum.SOURCE.equals(annotation.objectType()) && declaration instanceof SourceDeclaration) {
            SourceDeclaration source = (SourceDeclaration) declaration;
            source.getEvents()
                    .ifDefined((events) -> result.put("events", this.convertInstanceEventDeclaration(events)));
            source.getApp().ifDefined((app) -> result.put("app", app));
            source.getType().ifDefined((type) -> result.put("type", type));
            source.getTriggers().ifDefined(
                    (triggers) -> result.put("triggers", this.convertTriggerDeclaration(triggers)));
            source.getName().ifDefined((name) -> result.put("name", name));
            source.getFilter().ifDefined((filter) -> result.put("filter", filter));
            source.getInterval().ifDefined((interval) -> result.put("interval", interval));
            source.getFitableId().ifDefined((fitableId) -> result.put("fitableId", fitableId));
        } else if (ObjectTypeEnum.INSTANCE.equals(annotation.objectType())
                && declaration instanceof TaskInstance.Declaration) {
            TaskInstance.Declaration instance = (TaskInstance.Declaration) declaration;
            instance.info().ifDefined((info) -> result.put("info", info));
            instance.typeId().ifDefined((typeId) -> result.put("typeId", typeId));
            instance.tags().ifDefined((tags) -> result.put("tags", tags));
            instance.sourceId().ifDefined((sourceId) -> result.put("sourceId", sourceId));
        } else if (ObjectTypeEnum.INSTANCE.equals(annotation.objectType())
                && declaration instanceof TaskRelation.Declaration) {
            putMessageForReladd((TaskRelation.Declaration) declaration, result);
        } else {
            throw new ServerInternalException("Can't convert arg to object type declaration.");
        }
        return result;
    }

    private void putMessageForReladd(TaskRelation.Declaration declaration, Map<String, Object> result) {
        String sql = "select task_id from task_instance_wide where id = ?";
        String createMessage = Objects.requireNonNull(getTaskEntity(sql, declaration.objectId1().get())).getName()
                + "实例" + "【" + Objects.requireNonNull(getTaskInstance(sql, declaration.objectId1().get()))
                .info()
                .get("title") + "】" + "关联了" + Objects.requireNonNull(
                        getTaskEntity(sql, declaration.objectId2().get()))
                .getName() + "实例" + "【" + Objects.requireNonNull(getTaskInstance(sql, declaration.objectId2().get()))
                .info()
                .get("title") + "】";
        result.put("message", createMessage);
    }

    private TaskInstance getTaskInstance(String sql, String objectId) {
        TaskEntity taskEntity = getTaskEntity(sql, objectId);
        if (taskEntity == null) {
            return null;
        }
        return repo.retrieve(taskEntity, objectId, false, OperationContext.empty());
    }

    private TaskEntity getTaskEntity(String sql, String objectId) {
        List<String> args = Collections.singletonList(objectId);
        Object taskId = this.executor.executeScalar(sql, args);
        if (Objects.isNull(taskId)) {
            log.warn("The task_id in task_instance_wide is null. Query sql is {}, arg is {}.", sql, objectId);
            return null;
        }
        return taskService.retrieve(taskId.toString(), OperationContext.empty());
    }

    private List<Map<String, Object>> convertCategoryTriggersToMap(List<TaskCategoryTriggerDeclaration> declarations) {
        return declarations.stream().filter(Objects::nonNull).map((declaration) -> {
            Map<String, Object> result = new HashMap<>();
            declaration.getCategory().ifDefined((c) -> result.put("category", c));
            declaration.getFitableIds().ifDefined((f) -> result.put("fitableIds", f));
            return result;
        }).collect(Collectors.toList());
    }

    /**
     * convertTaskPropertyDeclaration
     *
     * @param declarations declarations
     * @return List<Map < String, Object>>
     */
    public List<Map<String, Object>> convertTaskPropertyDeclaration(List<TaskProperty.Declaration> declarations) {
        return declarations.stream().filter(Objects::nonNull).map((declaration) -> {
            Map<String, Object> result = new HashMap<>();
            declaration.appearance().ifDefined((appearance) -> result.put("appearance", appearance));
            declaration.categories().ifDefined((c) -> result.put("categories", c));
            declaration.dataType().ifDefined((d) -> result.put("dataType", d));
            declaration.name().ifDefined((n) -> result.put("name", n));
            declaration.description().ifDefined((d) -> result.put("description", d));
            declaration.identifiable().ifDefined((i) -> result.put("identifiable", i));
            declaration.required().ifDefined((r) -> result.put("required", r));
            declaration.scope().ifDefined((s) -> result.put("scope", s));
            return result;
        }).collect(Collectors.toList());
    }

    private List<Map<String, Object>> convertInstanceEventDeclaration(List<InstanceEventDeclaration> declarations) {
        return declarations.stream().filter(Objects::nonNull).map(declaration -> {
            Map<String, Object> result = new HashMap<>();
            declaration.fitableId().ifDefined((f) -> result.put("fitableId", f));
            declaration.type().ifDefined((t) -> result.put("type", t));
            return result;
        }).collect(Collectors.toList());
    }

    private List<Map<String, Object>> convertTriggerDeclaration(List<TriggerDeclaration> declarations) {
        return declarations.stream().filter(Objects::nonNull).map(declaration -> {
            Map<String, Object> result = new HashMap<>();
            declaration.getPropertyName().ifDefined((p) -> result.put("propertyName", p));
            declaration.getFitableId().ifDefined((f) -> result.put("fitableId", f));
            return result;
        }).collect(Collectors.toList());
    }
}
