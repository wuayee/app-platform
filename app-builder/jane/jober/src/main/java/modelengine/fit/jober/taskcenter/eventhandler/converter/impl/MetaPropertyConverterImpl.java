/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.eventhandler.converter.impl;

import modelengine.fit.jane.meta.property.MetaPropertyDeclarationInfo;
import modelengine.fit.jane.task.domain.PropertyCategory;
import modelengine.fit.jane.task.domain.TaskProperty;
import modelengine.fit.jober.entity.task.TaskPropertyCategory;
import modelengine.fit.jober.taskcenter.eventhandler.converter.MetaPropertyConverter;
import modelengine.fitframework.annotation.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * {@link MetaPropertyConverter}的默认实现。
 *
 * @author 陈镕希
 * @since 2023-12-26
 */
@Component
public class MetaPropertyConverterImpl implements MetaPropertyConverter {
    @Override
    public TaskProperty.Declaration convert(MetaPropertyDeclarationInfo metaPropertyDeclarationInfo) {
        modelengine.fit.jane.task.domain.TaskProperty.Declaration.Builder builder =
                modelengine.fit.jane.task.domain.TaskProperty.Declaration.custom();
        if (metaPropertyDeclarationInfo.getName().getDefined()) {
            builder.name(metaPropertyDeclarationInfo.getName().getValue());
        }
        if (metaPropertyDeclarationInfo.getDataType().getDefined()) {
            builder.dataType(metaPropertyDeclarationInfo.getDataType().getValue());
        }
        if (metaPropertyDeclarationInfo.getDescription().getDefined()) {
            builder.description(metaPropertyDeclarationInfo.getDescription().getValue());
        }
        if (metaPropertyDeclarationInfo.getRequired().getDefined()) {
            builder.isRequired(metaPropertyDeclarationInfo.getRequired().getValue());
        }
        if (metaPropertyDeclarationInfo.getIdentifiable().getDefined()) {
            builder.isIdentifiable(metaPropertyDeclarationInfo.getIdentifiable().getValue());
        }
        if (metaPropertyDeclarationInfo.getScope().getDefined()) {
            builder.scope(metaPropertyDeclarationInfo.getScope().getValue());
        }
        if (metaPropertyDeclarationInfo.getAttribute().getDefined()) {
            builder.appearance(metaPropertyDeclarationInfo.getAttribute().getValue());
        }
        return builder.build();
    }

    @Override
    public modelengine.fit.jober.entity.task.TaskProperty convert(TaskProperty taskProperty) {
        return new modelengine.fit.jober.entity.task.TaskProperty(taskProperty.id(), taskProperty.name(),
                taskProperty.dataType().name(), taskProperty.sequence(), taskProperty.description(),
                taskProperty.required(), taskProperty.identifiable(), taskProperty.scope().name(),
                taskProperty.appearance(), this.convert(taskProperty.categories()));
    }

    private List<TaskPropertyCategory> convert(List<PropertyCategory> categories) {
        return categories.stream()
                .map(category -> new TaskPropertyCategory(category.getValue(), category.getCategory()))
                .collect(Collectors.toList());
    }
}
