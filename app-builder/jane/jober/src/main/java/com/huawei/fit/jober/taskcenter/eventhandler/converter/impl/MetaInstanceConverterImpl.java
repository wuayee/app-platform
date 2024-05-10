/*
 *  Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.eventhandler.converter.impl;

import static com.huawei.fit.jane.Undefinables.whenDefined;
import static com.huawei.fitframework.util.ObjectUtils.nullIf;

import com.huawei.fit.jane.Undefinable;
import com.huawei.fit.jane.meta.multiversion.instance.Instance;
import com.huawei.fit.jane.meta.multiversion.instance.InstanceDeclarationInfo;
import com.huawei.fit.jane.meta.multiversion.instance.MetaInstanceFilter;
import com.huawei.fit.jane.task.util.UndefinableValue;
import com.huawei.fit.jober.taskcenter.domain.TaskEntity;
import com.huawei.fit.jober.taskcenter.domain.TaskInstance;
import com.huawei.fit.jober.taskcenter.eventhandler.converter.MetaInstanceConverter;
import com.huawei.fit.jober.taskcenter.util.sql.OrderBy;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.util.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * {@link MetaInstanceConverter}的默认实现。
 *
 * @author 孙怡菲 s00664640
 * @since 2023-12-12
 */
@Component
public class MetaInstanceConverterImpl implements MetaInstanceConverter {
    @Override
    public TaskInstance.Declaration convert(InstanceDeclarationInfo declaration) {
        TaskInstance.Declaration.Builder builder = TaskInstance.Declaration.custom();
        whenDefined(declaration.getInfo(), builder::info);
        whenDefined(declaration.getTags(), builder::tags);
        return builder.build();
    }

    @Override
    public Instance convert(TaskEntity task, TaskInstance instance) {
        Instance result = new Instance();
        result.setId(instance.id());
        result.setInfo(instance.info().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> task.getProperties()
                        .stream()
                        .filter(property -> StringUtils.equals(property.name(), entry.getKey()))
                        .findFirst()
                        .map(p -> p.dataType().toString(entry.getValue()))
                        .orElse(""))));
        result.setTags(instance.tags());
        return result;
    }

    @Override
    public TaskInstance.Filter convert(MetaInstanceFilter filter) {
        return TaskInstance.Filter.custom()
                .ids(nullIf(filter.getIds(), Collections.emptyList()))
                .infos(nullIf(filter.getInfos(), Collections.emptyMap()))
                .tags(nullIf(filter.getTags(), Collections.emptyList()))
                .build();
    }

    @Override
    public List<OrderBy> convertOrderBys(List<String> orderBys) {
        return Optional.ofNullable(orderBys).map(Collection::stream).orElseGet(Stream::empty)
                .map(StringUtils::trim).filter(StringUtils::isNotEmpty).map(OrderBy::parse)
                .collect(Collectors.toList());
    }

    private static <T> UndefinableValue<T> valueOf(Undefinable<T> attribute) {
        return MetaConverterImpl.valueOf(attribute);
    }

    private static <T> UndefinableValue<T> valueOf(T attribute, Predicate<T> emptyPredicate) {
        return MetaConverterImpl.valueOf(attribute, emptyPredicate);
    }

    // FIXME: 2024/3/29 0029 以下方法暂用，待删除
    public TaskInstance.Declaration convert(
            com.huawei.fit.jane.meta.instance.InstanceDeclarationInfo instanceDeclarationInfo) {
        TaskInstance.Declaration.Builder builder = TaskInstance.Declaration.custom();
        whenDefined(instanceDeclarationInfo.getInfo(), builder::info);
        whenDefined(instanceDeclarationInfo.getTags(), builder::tags);
        return builder.build();
    }

    // FIXME: 2024/3/29 0029 以下方法暂用，待删除
    public com.huawei.fit.jane.meta.instance.Instance convert1(TaskEntity task, TaskInstance instance) {
        com.huawei.fit.jane.meta.instance.Instance result = new com.huawei.fit.jane.meta.instance.Instance();
        result.setInfo(instance.info().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> task.getProperties()
                        .stream()
                        .filter(property -> StringUtils.equals(property.name(), entry.getKey()))
                        .findFirst()
                        .map(p -> p.dataType().toString(entry.getValue()))
                        .orElse(""))));
        result.setId(instance.id());
        result.setTags(instance.tags());
        return result;
    }

    // FIXME: 2024/3/29 0029 以下方法暂用，待删除
    public TaskInstance.Filter convert(com.huawei.fit.jane.meta.instance.MetaInstanceFilter metaInstanceFilter) {
        return TaskInstance.Filter.custom()
                .ids(nullIf(metaInstanceFilter.getIds(), Collections.emptyList()))
                .infos(nullIf(metaInstanceFilter.getInfos(), Collections.emptyMap()))
                .tags(nullIf(metaInstanceFilter.getTags(), Collections.emptyList()))
                .build();
    }
}
