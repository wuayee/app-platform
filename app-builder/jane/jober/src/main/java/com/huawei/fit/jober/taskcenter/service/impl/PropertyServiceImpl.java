/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.service.impl;

import static modelengine.fitframework.util.ObjectUtils.nullIf;

import com.huawei.fit.jane.task.domain.DomainObject;
import com.huawei.fit.jane.task.domain.PropertyCategory;
import com.huawei.fit.jane.task.domain.PropertyCategoryDeclaration;
import com.huawei.fit.jane.task.domain.PropertyDataType;
import com.huawei.fit.jane.task.domain.PropertyScope;
import com.huawei.fit.jane.task.domain.TaskProperty;
import com.huawei.fit.jane.task.util.Entities;
import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jane.task.util.UndefinableValue;
import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.exceptions.BadRequestException;
import com.huawei.fit.jober.common.exceptions.ConflictException;
import com.huawei.fit.jober.taskcenter.dao.po.TaskPropertyObject;
import com.huawei.fit.jober.taskcenter.declaration.TaskPropertiesDeclaration;
import com.huawei.fit.jober.taskcenter.domain.TaskTemplate;
import com.huawei.fit.jober.taskcenter.domain.TaskTemplateProperty;
import com.huawei.fit.jober.taskcenter.event.TaskPropertyDeletingEvent;
import com.huawei.fit.jober.taskcenter.event.TaskPropertyModifiedEvent;
import com.huawei.fit.jober.taskcenter.event.TaskPropertyModifyingEvent;
import com.huawei.fit.jober.taskcenter.service.CategoryService;
import com.huawei.fit.jober.taskcenter.service.PropertyService;
import com.huawei.fit.jober.taskcenter.util.DynamicSqlExecutor;
import com.huawei.fit.jober.taskcenter.util.Enums;
import com.huawei.fit.jober.taskcenter.util.ExecutableSql;
import com.huawei.fit.jober.taskcenter.util.Maps;
import com.huawei.fit.jober.taskcenter.util.SequenceUtils;
import com.huawei.fit.jober.taskcenter.validation.PropertyValidator;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.plugin.Plugin;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.fitframework.transaction.Transactional;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.LazyLoader;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.StringUtils;
import modelengine.fitframework.util.TypeUtils;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 任务定义之属性服务实现类
 *
 * @author 董建华
 * @since 2023-08-08
 **/
@Component
public class PropertyServiceImpl implements PropertyService {
    private final DynamicSqlExecutor dynamicSqlExecutor;

    private final ObjectSerializer objectSerializer;

    private final CategoryService categoryService;

    private final PropertyValidator validator;

    private final Plugin plugin;

    public PropertyServiceImpl(DynamicSqlExecutor dynamicSqlExecutor,
            @Fit(alias = "json") ObjectSerializer objectSerializer, CategoryService categoryService,
            PropertyValidator validator, Plugin plugin) {
        this.dynamicSqlExecutor = dynamicSqlExecutor;
        this.objectSerializer = objectSerializer;
        this.categoryService = categoryService;
        this.validator = validator;
        this.plugin = plugin;
    }

    @Override
    @Transactional
    public void batchSave(TaskPropertiesDeclaration declaration, OperationContext context) {
        List<String> deletingPropertyIds = new ArrayList<>();
        List<TaskPropertyObject> propertyObjects = this.selectByTaskId(declaration.getTaskId());
        Map<String, TaskProperty> oldProperties = this.toDomainObjects(propertyObjects).stream().collect(
                Collectors.toMap(property -> StringUtils.toLowerCase(property.name()), Function.identity()));
        if (propertyObjects.isEmpty()) {
            propertyObjects = this.dealNewTaskProperties(declaration.getTaskId(), declaration);
        } else {
            propertyObjects = this.dealExistTaskProperties(declaration, propertyObjects,
                    declaration.getTaskId(), deletingPropertyIds);
        }
        this.fillSequences(propertyObjects, declaration.getTemplate());
        deletingPropertyIds.forEach(propertyId -> {
            Map<String, TaskProperty> idIndexedOldProperties = oldProperties.values().stream()
                    .collect(Collectors.toMap(TaskProperty::id, Function.identity()));
            TaskProperty deletingProperty = idIndexedOldProperties.get(propertyId);
            TaskPropertyDeletingEvent event = new TaskPropertyDeletingEvent(this, deletingProperty);
            plugin.publisherOfEvents().publishEvent(event);
        });
        for (TaskProperty.Declaration propertyDeclaration : declaration.getProperties()) {
            TaskProperty property = oldProperties.get(StringUtils.toLowerCase(propertyDeclaration.name().get()));
            if (property != null) {
                TaskPropertyModifyingEvent event = new TaskPropertyModifyingEvent(this, property, propertyDeclaration);
                this.plugin.publisherOfEvents().publishEvent(event);
            }
        }
        this.save(propertyObjects);
        this.delete(deletingPropertyIds);
        this.categoryService.deleteByTaskIds(Collections.singletonList(declaration.getTaskId()));
        Map<String, List<PropertyCategoryDeclaration>> categories = new HashMap<>();
        for (TaskProperty.Declaration propertyDeclaration : declaration.getProperties()) {
            if (propertyDeclaration.categories().defined()) {
                categories.put(propertyDeclaration.name().get(), propertyDeclaration.categories().get());
            }
        }
        Map<String, List<PropertyCategoryDeclaration>> propertyCategories = new HashMap<>(propertyObjects.size());
        for (TaskPropertyObject property : propertyObjects) {
            propertyCategories.put(property.getId(), categories.get(property.getName()));
        }
        Map<String, List<PropertyCategory>> savedCategories = this.categoryService.saveMatchers(propertyCategories);
        Map<String, TaskProperty> newProperties = toDomainObjects(propertyObjects, savedCategories).stream()
                .collect(Collectors.toMap(property -> StringUtils.toLowerCase(property.name()), Function.identity(),
                        Maps.throwingMerger()));
        Set<String> modifiedPropertyNames = new LinkedHashSet<>(newProperties.keySet());
        modifiedPropertyNames.retainAll(oldProperties.keySet());
        modifiedPropertyNames.forEach(propertyName -> {
            TaskProperty oldProperty = oldProperties.get(propertyName);
            TaskProperty newProperty = newProperties.get(propertyName);
            plugin.publisherOfEvents().publishEvent(new TaskPropertyModifiedEvent(this, newProperty, oldProperty));
        });
    }

    private List<TaskProperty> toDomainObjects(List<TaskPropertyObject> objects) {
        return this.toDomainObjects(objects, null);
    }

    private List<TaskProperty> toDomainObjects(List<TaskPropertyObject> objects,
            Map<String, List<PropertyCategory>> categories) {
        List<String> propertyIds = objects.stream().map(TaskPropertyObject::getId).collect(Collectors.toList());
        Map<String, List<PropertyCategory>> actualCategories;
        if (categories == null) {
            actualCategories = this.categoryService.matchers(propertyIds);
        } else {
            actualCategories = categories;
        }
        return objects.stream().map(object -> this.toDomainObject(object, actualCategories.get(object.getId())))
                .collect(Collectors.toList());
    }

    private TaskProperty toDomainObject(TaskPropertyObject object, List<PropertyCategory> categories) {
        byte[] bytes = object.getAppearance().getBytes(StandardCharsets.UTF_8);
        Map<String, Object> appearance = this.objectSerializer.deserialize(bytes, StandardCharsets.UTF_8,
                TypeUtils.parameterized(Map.class, new Type[] {String.class, Object.class}));
        return TaskProperty.custom()
                .id(object.getId())
                .name(object.getName())
                .dataType(Enums.parse(PropertyDataType.class, object.getDataType(), PropertyDataType.DEFAULT,
                        ErrorCodes.TASK_PROPERTY_DATA_TYPE_INVALID))
                .sequence(object.getSequence())
                .description(object.getDescription())
                .isRequired(object.getIsRequired())
                .isIdentifiable(object.getIsIdentifiable())
                .scope(Enums.parse(PropertyScope.class, object.getScope(), PropertyScope.PUBLIC,
                        ErrorCodes.TASK_PROPERTY_SCOPE_INVALID))
                .appearance(appearance)
                .categories(nullIf(categories, Collections.emptyList()))
                .build();
    }

    private List<TaskPropertyObject> dealExistTaskProperties(TaskPropertiesDeclaration declaration,
            List<TaskPropertyObject> propertyObjects, String taskId, List<String> deletingPropertyIds) {
        Map<String, TaskTemplateProperty> templatePropertyMap = this.getIdTaskTemplatePropertyMap(
                declaration.getTemplate());
        Map<String, TaskPropertyObject> indexed = propertyObjects.stream()
                .collect(Collectors.toMap(TaskPropertyObject::getName, Function.identity()));
        LazyLoader<Boolean> hasInstances = new LazyLoader<>(() -> this.hasInstances(taskId) == 1);
        List<TaskPropertyObject> result = new ArrayList<>(propertyObjects);
        for (TaskProperty.Declaration propertyDeclaration : declaration.getProperties()) {
            String name = UndefinableValue.require(propertyDeclaration.name(),
                    () -> new BadRequestException(ErrorCodes.PROPERTY_NAME_REQUIRED));
            TaskPropertyObject existingObject = indexed.get(name);
            if (existingObject != null) {
                this.acceptPropertyModify(existingObject, propertyDeclaration, templatePropertyMap, hasInstances);
                indexed.remove(name);
                continue;
            }
            if (propertyDeclaration.templateId().defined()) {
                if (!templatePropertyMap.containsKey(propertyDeclaration.templateId().get())) {
                    throw new BadRequestException(ErrorCodes.TEMPLATE_PROPERTY_NOT_FOUND);
                }
                TaskTemplateProperty templateProperty = templatePropertyMap.get(
                        propertyDeclaration.templateId().get());
                existingObject = this.buildPropertyObjectByTemplate(taskId, propertyDeclaration,
                        templateProperty);
            } else {
                existingObject = this.buildPropertyObject(taskId, propertyDeclaration);
            }
            result.add(existingObject);
        }
        if (!indexed.isEmpty() && hasInstances.get()) {
            throw new ConflictException(ErrorCodes.PROPERTY_CANNOT_BE_DELETED_WITH_INSTANCES);
        } else {
            deletingPropertyIds.addAll(
                    indexed.values().stream().map(TaskPropertyObject::getId).collect(Collectors.toList()));
        }

        return result;
    }

    private Map<String, TaskTemplateProperty> getIdTaskTemplatePropertyMap(TaskTemplate taskTemplate) {
        if (Objects.isNull(taskTemplate) || CollectionUtils.isEmpty(taskTemplate.properties())) {
            return Collections.emptyMap();
        }
        return taskTemplate.properties()
                .stream()
                .collect(Collectors.toMap(TaskTemplateProperty::id, Function.identity()));
    }

    private List<TaskPropertyObject> dealNewTaskProperties(String taskId, TaskPropertiesDeclaration declaration) {
        TaskTemplate taskTemplate = declaration.getTemplate();
        if (Objects.isNull(taskTemplate) || CollectionUtils.isEmpty(taskTemplate.properties())) {
            // task未使用模板或者task的模拟中无模板属性，但是在taskProperty中期望使用模板属性
            if (declaration.getProperties()
                    .stream()
                    .anyMatch(pd -> Objects.nonNull(pd.templateId()) && pd.templateId().defined())) {
                throw new BadRequestException(ErrorCodes.TEMPLATE_PROPERTY_NOT_FOUND);
            }
            return declaration.getProperties()
                    .stream()
                    .map(pd -> this.buildPropertyObject(taskId, pd))
                    .collect(Collectors.toList());
        }
        Map<String, TaskTemplateProperty> templatePropertyMap = taskTemplate.properties()
                .stream()
                .collect(Collectors.toMap(DomainObject::id, Function.identity()));
        List<TaskPropertyObject> propertyObjects = new ArrayList<>(declaration.getProperties().size());
        for (TaskProperty.Declaration propertyDeclaration : declaration.getProperties()) {
            // 如果声明中不存在templateId，表示不使用模板
            if (!propertyDeclaration.templateId().defined()) {
                TaskPropertyObject propertyObject = this.buildPropertyObject(taskId, propertyDeclaration);
                propertyObjects.add(propertyObject);
                continue;
            }
            // 如果声明中的模板Id不存在于任务使用的模板属性列表中，则抛出异常
            if (!templatePropertyMap.containsKey(propertyDeclaration.templateId().get())) {
                throw new BadRequestException(ErrorCodes.TEMPLATE_PROPERTY_NOT_FOUND);
            }
            // 各种符合，则使用模板创建属性：
            TaskTemplateProperty templateProperty = templatePropertyMap.get(propertyDeclaration.templateId().get());
            TaskPropertyObject object = buildPropertyObjectByTemplate(taskId, propertyDeclaration, templateProperty);
            propertyObjects.add(object);
        }
        return propertyObjects;
    }

    private void save(List<TaskPropertyObject> objects) {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO \"task_property\"(\"id\", \"task_id\", \"template_id\", \"name\", \"required\", "
                + "\"description\", \"scope\", \"data_type\", \"sequence\", \"appearance\", \"identifiable\") VALUES");
        String rowHolders = "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?::JSON, ?)";
        sql.append(rowHolders);
        for (int i = 1; i < objects.size(); i++) {
            sql.append(", ").append(rowHolders);
        }
        sql.append(" ON CONFLICT (\"id\") DO UPDATE SET \"task_id\" = EXCLUDED.\"task_id\", "
                + "\"template_id\" = EXCLUDED.\"template_id\"," + "\"name\" = EXCLUDED.\"name\", "
                + "\"required\" = EXCLUDED.\"required\", " + "\"description\" = EXCLUDED.\"description\", "
                + "\"scope\" = EXCLUDED.\"scope\", " + "\"data_type\" = EXCLUDED.\"data_type\", "
                + "\"sequence\" = EXCLUDED.\"sequence\", " + "\"appearance\" = EXCLUDED.\"appearance\", "
                + "\"identifiable\" = EXCLUDED.\"identifiable\"");
        List<Object> args = objects.stream()
                .map(object -> Arrays.asList(object.getId(), object.getTaskId(), object.getTemplateId(),
                        object.getName(), object.getIsRequired(), object.getDescription(), object.getScope(),
                        object.getDataType(), object.getSequence(), object.getAppearance(), object.getIsIdentifiable()))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        this.dynamicSqlExecutor.executeUpdate(sql.toString(), args);
    }

    private void delete(List<String> propertyIds) {
        if (propertyIds.isEmpty()) {
            return;
        }
        String sql = "DELETE FROM \"task_property\" WHERE \"id\" IN (${propertyIds})";
        ExecutableSql.resolve(sql, Collections.singletonMap("propertyIds", propertyIds))
                .executeUpdate(this.dynamicSqlExecutor);
    }

    private TaskPropertyObject buildPropertyObject(String taskId, TaskProperty.Declaration declaration) {
        TaskPropertyObject object = new TaskPropertyObject();
        object.setId(Entities.generateId());
        object.setTaskId(taskId);
        object.setName(this.name(declaration.name()));
        object.setIsRequired(this.required(declaration.required()));
        object.setDescription(this.description(declaration.description()));
        object.setScope(this.scope(declaration.scope()));
        object.setDataType(this.dataType(declaration.dataType()));
        object.setAppearance(this.appearance(declaration.appearance()));
        object.setIsIdentifiable(this.identifiable(declaration.identifiable()));
        object.setTemplateId(Entities.emptyId());
        return object;
    }

    private TaskPropertyObject buildPropertyObjectByTemplate(String taskId,
            TaskProperty.Declaration propertyDeclaration,
            TaskTemplateProperty templateProperty) {
        TaskPropertyObject object = new TaskPropertyObject();
        object.setId(Entities.generateId());
        object.setName(templateProperty.name());
        object.setTaskId(taskId);
        object.setTemplateId(templateProperty.id());
        object.setIsRequired(this.required(propertyDeclaration.required()));
        object.setDescription(this.description(propertyDeclaration.description()));
        object.setDataType(Enums.toString(templateProperty.dataType()));
        object.setScope(this.scope(propertyDeclaration.scope()));
        object.setAppearance(this.appearance(propertyDeclaration.appearance()));
        object.setIsIdentifiable(this.identifiable(propertyDeclaration.identifiable()));
        object.setSequence(templateProperty.sequence());
        return object;
    }

    private void acceptPropertyModify(TaskPropertyObject object, TaskProperty.Declaration declaration,
            Map<String, TaskTemplateProperty> templatePropertyMap, LazyLoader<Boolean> hasInstances) {
        boolean isRequired = this.required(declaration.required());
        if (isRequired && !nullIf(object.getIsRequired(), false) && hasInstances.get()) {
            throw new ConflictException(ErrorCodes.PROPERTY_CANNOT_BE_MODIFIED_WITH_INSTANCES);
        } else {
            object.setIsRequired(isRequired);
        }
        object.setDescription(this.description(declaration.description()));
        object.setScope(this.scope(declaration.scope()));
        // 如果使用了模板，则不能修改dataType，必须和模板保持一致，且不能修改模板
        this.acceptDataType(object, declaration, templatePropertyMap, hasInstances);

        object.setAppearance(this.appearance(declaration.appearance()));
        boolean canIdentifiable = this.identifiable(declaration.identifiable());
        if (canIdentifiable != nullIf(object.getIsIdentifiable(), false) && hasInstances.get()) {
            throw new ConflictException(ErrorCodes.PROPERTY_CANNOT_BE_MODIFIED_WITH_INSTANCES);
        } else {
            object.setIsIdentifiable(canIdentifiable);
        }
    }

    private void acceptDataType(TaskPropertyObject originProperty, TaskProperty.Declaration declaration,
            Map<String, TaskTemplateProperty> templatePropertyMap, LazyLoader<Boolean> hasInstances) {
        boolean hasUsedTemplate = !StringUtils.equals(originProperty.getTemplateId(), Entities.emptyId());
        boolean hasModifyTemplate = declaration.templateId().defined();
        if (hasUsedTemplate && hasModifyTemplate) {
            // 已经使用模板了，不能修改模板
            if (!StringUtils.equalsIgnoreCase(declaration.templateId().get(), originProperty.getTemplateId())) {
                throw new ConflictException(ErrorCodes.PROPERTY_HAS_USED_TEMPLATE);
            }
        } else if (!hasUsedTemplate && hasModifyTemplate) { // 过去未使用模板，新增模板
            if (hasInstances.get()) {
                throw new ConflictException(ErrorCodes.PROPERTY_CANNOT_BE_MODIFIED_WITH_INSTANCES);
            }
            TaskTemplateProperty templateProperty = templatePropertyMap.get(declaration.templateId().get());
            if (Objects.isNull(templateProperty)) {
                throw new BadRequestException(ErrorCodes.TEMPLATE_PROPERTY_NOT_FOUND);
            }
            originProperty.setDataType(Enums.toString(templateProperty.dataType()));
            originProperty.setSequence(templateProperty.sequence());
        } else {
            String dataType = this.dataType(declaration.dataType());
            if (!Objects.equals(originProperty.getDataType(), dataType)) {
                if (hasInstances.get()) {
                    throw new ConflictException(ErrorCodes.PROPERTY_CANNOT_BE_MODIFIED_WITH_INSTANCES);
                }
                if (hasUsedTemplate) {
                    // 已经使用模板了，不能修改dataType
                    throw new ConflictException(ErrorCodes.PROPERTY_HAS_USED_TEMPLATE);
                }
                originProperty.setDataType(dataType);
                originProperty.setSequence(null);
            }
        }
    }

    private void fillSequences(List<TaskPropertyObject> objects, TaskTemplate taskTemplate) {
        Map<String, List<TaskPropertyObject>> grouped = objects.stream()
                .collect(Collectors.groupingBy(TaskPropertyObject::getDataType));
        Map<String, List<TaskTemplateProperty>> templateProperties = this.groupTemplatePropertiesByDataType(
                taskTemplate);
        for (Map.Entry<String, List<TaskPropertyObject>> entry : grouped.entrySet()) {
            List<TaskPropertyObject> properties = entry.getValue();
            Set<Integer> sequencesSet = properties.stream()
                    .map(TaskPropertyObject::getSequence)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toCollection(HashSet::new));
            List<TaskTemplateProperty> taskTemplateProperties = templateProperties.get(entry.getKey());
            if (CollectionUtils.isNotEmpty(taskTemplateProperties)) {
                List<Integer> templateSequences = taskTemplateProperties.stream()
                        .map(TaskTemplateProperty::sequence)
                        .collect(Collectors.toList());
                sequencesSet.addAll(templateSequences);
            }
            List<Integer> sequences = new ArrayList<>(sequencesSet);
            Collections.sort(sequences);
            for (TaskPropertyObject property : properties) {
                PropertyDataType dataType = Enums.parse(PropertyDataType.class, property.getDataType());
                if (dataType.listable()) {
                    property.setSequence(0);
                    continue;
                }
                if (property.getSequence() == null) {
                    int sequence = SequenceUtils.getSequenceFromList(sequences);
                    property.setSequence(sequence);
                    sequences.add(sequence - 1, sequence);
                }
            }
        }
    }

    private Map<String, List<TaskTemplateProperty>> groupTemplatePropertiesByDataType(TaskTemplate taskTemplate) {
        if (Objects.isNull(taskTemplate) || Objects.isNull(taskTemplate.properties())) {
            return Collections.emptyMap();
        }
        return taskTemplate.properties()
                .stream()
                .collect(Collectors.groupingBy((tp) -> Enums.toString(tp.dataType())));
    }

    private long hasInstances(String taskId) {
        String sql = "SELECT 1 FROM \"task_instance_wide\" WHERE \"task_id\" = ? LIMIT 1";
        Object count = this.dynamicSqlExecutor.executeScalar(sql, Collections.singletonList(taskId));
        return Objects.isNull(count) ? 0 : 1;
    }

    private String name(UndefinableValue<String> name) {
        return this.validator.validateName(
                UndefinableValue.require(name, () -> new BadRequestException(ErrorCodes.PROPERTY_NAME_REQUIRED)),
                OperationContext.empty());
    }

    private Boolean required(UndefinableValue<Boolean> required) {
        return UndefinableValue.withDefault(required, false);
    }

    private String description(UndefinableValue<String> description) {
        return this.validator.validateDescription(UndefinableValue.withDefault(description, StringUtils.EMPTY),
                OperationContext.empty());
    }

    private String scope(UndefinableValue<String> scope) {
        return Enums.toString(this.validator.validateScope(UndefinableValue.withDefault(scope, StringUtils.EMPTY),
                OperationContext.empty()));
    }

    private String dataType(UndefinableValue<String> dataType) {
        return Enums.toString(this.validator.validateDataType(UndefinableValue.withDefault(dataType, StringUtils.EMPTY),
                OperationContext.empty()));
    }

    private String appearance(UndefinableValue<Map<String, Object>> appearance) {
        Map<String, Object> origin = UndefinableValue.withDefault(appearance, Collections.emptyMap());
        byte[] bytes = this.objectSerializer.serialize(origin, StandardCharsets.UTF_8);
        String value = new String(bytes, StandardCharsets.UTF_8);
        return this.validator.validateAppearance(value);
    }

    private Boolean identifiable(UndefinableValue<Boolean> identifiable) {
        return UndefinableValue.withDefault(identifiable, false);
    }

    private List<TaskPropertyObject> selectByTaskId(String taskId) {
        String sql = "SELECT \"id\", \"task_id\", \"template_id\", \"name\", \"required\", \"description\", "
                + "\"scope\", \"data_type\", \"sequence\", \"appearance\", \"identifiable\" FROM \"task_property\" "
                + "WHERE \"task_id\" = ${taskId}";
        List<Map<String, Object>> rows = ExecutableSql.resolve(sql, Collections.singletonMap("taskId", taskId))
                .executeQuery(this.dynamicSqlExecutor);
        return rows.stream().map(row -> {
            TaskPropertyObject object = new TaskPropertyObject();
            object.setId(ObjectUtils.cast(row.get("id")));
            object.setTaskId(ObjectUtils.cast(row.get("task_id")));
            object.setTemplateId(ObjectUtils.cast(row.get("template_id")));
            object.setName(ObjectUtils.cast(row.get("name")));
            object.setIsRequired(Boolean.TRUE.equals(row.get("required")));
            object.setDescription(ObjectUtils.cast(row.get("description")));
            object.setScope(ObjectUtils.cast(row.get("scope")));
            object.setDataType(ObjectUtils.cast(row.get("data_type")));
            object.setSequence(ObjectUtils.<Number>cast(row.get("sequence")).intValue());
            object.setAppearance(Optional.ofNullable(row.get("appearance")).map(Object::toString).orElse("{}"));
            object.setIsIdentifiable(Boolean.TRUE.equals(row.get("identifiable")));
            return object;
        }).collect(Collectors.toList());
    }
}
