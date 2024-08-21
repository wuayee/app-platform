/*
 *  Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.eventhandler.converter.impl;

import static modelengine.fitframework.util.ObjectUtils.cast;

import com.huawei.fit.jane.Undefinable;
import com.huawei.fit.jane.meta.definition.Meta;
import com.huawei.fit.jane.meta.definition.MetaDeclarationInfo;
import com.huawei.fit.jane.meta.definition.MetaFilter;
import com.huawei.fit.jane.meta.property.MetaPropertyDeclarationInfo;
import com.huawei.fit.jane.task.domain.TaskProperty;
import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jane.task.util.UndefinableValue;
import com.huawei.fit.jober.taskcenter.declaration.TaskDeclaration;
import com.huawei.fit.jober.taskcenter.domain.TaskEntity;
import com.huawei.fit.jober.taskcenter.eventhandler.converter.MetaConverter;
import com.huawei.fit.jober.taskcenter.eventhandler.converter.MetaPropertyConverter;
import com.huawei.fit.jober.taskcenter.eventhandler.converter.TaskConverter;
import com.huawei.fit.jober.taskcenter.filter.TaskFilter;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * {@link MetaConverter}的默认实现。
 *
 * @author 孙怡菲
 * @since 2023-12-12
 */
@Component
public class MetaConverterImpl implements MetaConverter {
    private final TaskConverter taskConverter;

    private final MetaPropertyConverter metaPropertyConverter;

    public MetaConverterImpl(TaskConverter taskConverter, MetaPropertyConverter metaPropertyConverter) {
        this.taskConverter = taskConverter;
        this.metaPropertyConverter = metaPropertyConverter;
    }

    @Override
    public TaskDeclaration convert(MetaDeclarationInfo metaDeclarationInfo) {
        TaskDeclaration taskDeclaration = new TaskDeclaration();
        taskDeclaration.setName(valueOf(metaDeclarationInfo.getName()));
        taskDeclaration.setCategory(valueOf(metaDeclarationInfo.getCategory()));
        taskDeclaration.setAttributes(valueOf(metaDeclarationInfo.getAttributes()));
        taskDeclaration.setCategoryTriggers(UndefinableValue.undefined());
        taskDeclaration.setProperties(metaDeclarationInfo.getProperties().getDefined()
                ? UndefinableValue.defined(convertDeclaration(metaDeclarationInfo.getProperties()))
                : UndefinableValue.undefined());
        return taskDeclaration;
    }

    @Override
    public TaskDeclaration convertMultiVersionDeclaration(
            com.huawei.fit.jane.meta.multiversion.definition.MetaDeclarationInfo metaDeclarationInfo) {
        TaskDeclaration taskDeclaration = new TaskDeclaration();
        taskDeclaration.setName(valueOf(metaDeclarationInfo.getName()));
        taskDeclaration.setAttributes(valueOf(metaDeclarationInfo.getAttributes()));
        taskDeclaration.setCategory(valueOf(metaDeclarationInfo.getCategory()));
        taskDeclaration.setProperties(metaDeclarationInfo.getProperties().getDefined()
                ? UndefinableValue.defined(convertDeclaration(metaDeclarationInfo.getProperties()))
                : UndefinableValue.undefined());
        taskDeclaration.setCategoryTriggers(UndefinableValue.undefined());
        return taskDeclaration;
    }

    private List<TaskProperty.Declaration> convertDeclaration(
            Undefinable<List<MetaPropertyDeclarationInfo>> properties) {
        return properties
                .getValue()
                .stream()
                .map(metaPropertyConverter::convert)
                .collect(Collectors.toList());
    }

    @Override
    public Meta convert(TaskEntity task, OperationContext context) {
        Meta meta = new Meta();
        meta.setId(task.getId());
        meta.setName(task.getName());
        meta.setCategory(task.getCategory().name());
        meta.setProperties(Optional.ofNullable(task.getProperties())
                .map(properties -> properties.stream().map(taskConverter::convert).collect(Collectors.toList()))
                .orElse(Collections.emptyList()));
        meta.setCreator(task.getCreator());
        meta.setCreationTime(task.getCreationTime());
        meta.setLastModifier(task.getLastModifier());
        meta.setLastModificationTime(task.getLastModificationTime());
        meta.setTenant(context.tenantId());
        meta.setAttributes(task.getAttributes());
        return meta;
    }

    @Override
    public com.huawei.fit.jane.meta.multiversion.definition.Meta convert2MultiVersionMeta(TaskEntity task,
            OperationContext context) {
        com.huawei.fit.jane.meta.multiversion.definition.Meta meta =
                new com.huawei.fit.jane.meta.multiversion.definition.Meta();
        meta.setVersionId(task.getId());
        meta.setId(task.getTemplateId());
        meta.setCreationTime(task.getCreationTime());
        meta.setCategory(task.getCategory().name());
        meta.setProperties(Optional.ofNullable(task.getProperties())
                .map(properties -> properties.stream().map(taskConverter::convert).collect(Collectors.toList()))
                .orElse(Collections.emptyList()));
        meta.setCreator(task.getCreator());
        meta.setLastModifier(task.getLastModifier());
        meta.setLastModificationTime(task.getLastModificationTime());
        meta.setTenant(task.getTenantId());
        meta.setAttributes(task.getAttributes());
        meta.setName(task.getName());
        meta.setVersion("1.0.0"); // 2024/4/2 0002 兼容逻辑，如果不存在|则认为是旧数据，默认1.0.0版本
        if (task.getName().contains("|")) {
            String[] nameAndVersion = task.getName().split("\\|");
            meta.setVersion(nameAndVersion[1]);
            meta.setName(nameAndVersion[0]);
        }
        return meta;
    }

    @Override
    public TaskFilter convert(MetaFilter metaFilter) {
        TaskFilter taskFilter = new TaskFilter();
        taskFilter.setIds(valueOf(metaFilter.getIds(), CollectionUtils::isEmpty));
        taskFilter.setNames(valueOf(metaFilter.getNames(), CollectionUtils::isEmpty));
        taskFilter.setCategories(valueOf(metaFilter.getCategories(), CollectionUtils::isEmpty));
        taskFilter.setCreators(valueOf(metaFilter.getCreators(), CollectionUtils::isEmpty));
        taskFilter.setOrderBys(valueOf(metaFilter.getOrderBys(), CollectionUtils::isEmpty));
        return taskFilter;
    }

    /**
     * 将{@link Undefinable}对象转换为{@link UndefinableValue}对象。
     *
     * @param attribute 待转换的{@link Undefinable}对象
     * @param <T> 待转换对象的类型
     * @return 转换后的{@link UndefinableValue}对象
     */
    public static <T> UndefinableValue<T> valueOf(Undefinable<T> attribute) {
        if (!attribute.getDefined()) {
            return UndefinableValue.undefined();
        }
        T value = cast(attribute.getValue());
        return UndefinableValue.defined(value);
    }

    /**
     * 将给定的对象转换为{@link UndefinableValue}对象。
     *
     * @param attribute 待转换的对象
     * @param emptyPredicate 用于判断对象是否为空的断言函数
     * @param <T> 待转换对象的类型
     * @return 转换后的{@link UndefinableValue}对象
     */
    public static <T> UndefinableValue<T> valueOf(T attribute, Predicate<T> emptyPredicate) {
        if (emptyPredicate.test(attribute)) {
            return UndefinableValue.undefined();
        }
        return UndefinableValue.defined(attribute);
    }
}
