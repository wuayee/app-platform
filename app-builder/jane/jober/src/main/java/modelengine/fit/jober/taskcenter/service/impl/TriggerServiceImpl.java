/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.service.impl;

import modelengine.fit.jane.task.util.Entities;
import modelengine.fit.jane.task.util.OperationContext;
import modelengine.fit.jober.taskcenter.dao.TriggerMapper;
import modelengine.fit.jober.taskcenter.dao.po.TriggerObject;
import modelengine.fit.jober.taskcenter.declaration.SourceTriggersDeclaration;
import modelengine.fit.jober.taskcenter.declaration.TriggerDeclaration;
import modelengine.fit.jober.taskcenter.domain.TriggerEntity;
import modelengine.fit.jober.taskcenter.filter.TriggerFilter;
import modelengine.fit.jober.taskcenter.service.TriggerService;
import modelengine.fit.jober.taskcenter.validation.TriggerValidator;

import lombok.RequiredArgsConstructor;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.transaction.Transactional;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 任务属性触发器Service管理。
 *
 * @author 王伟
 * @since 2023-08-08
 */
@Component
@RequiredArgsConstructor
public class TriggerServiceImpl implements TriggerService {
    private final TriggerMapper triggerMapper;

    private final TriggerValidator triggerValidator;

    @Override
    @Transactional
    public TriggerEntity create(String taskId, String sourceId, TriggerDeclaration declaration,
            OperationContext context) {
        String propertyName = triggerValidator.validatePropertyName(declaration.getPropertyName().get(), context);
        String actualTaskId = triggerValidator.validateTaskId(taskId, context);
        TriggerObject triggerObject = triggerMapper.selectTriggerByTaskId(actualTaskId, sourceId, propertyName);
        if (triggerObject == null) {
            return null;
        }
        triggerObject.setId(Entities.generateId());
        triggerMapper.create(triggerObject);
        return toTriggerEntity(triggerObject);
    }

    @Override
    @Transactional
    public void patch(String taskId, String sourceId, String triggerId, TriggerDeclaration declaration,
            OperationContext context) {

    }

    @Override
    @Transactional
    public void delete(String taskId, TriggerFilter filter, OperationContext context) {
        if (filter == null || isDefinedAnyFilter(filter)) {
            return;
        }
        triggerMapper.delete(filter.getIds().withDefault(Collections.emptyList()),
                filter.getSourceIds().withDefault(Collections.emptyList()),
                filter.getPropertyIds().withDefault(Collections.emptyList()),
                filter.getFitableIds().withDefault(Collections.emptyList()));
    }

    private static boolean isDefinedAnyFilter(TriggerFilter filter) {
        return !filter.getIds().defined() && !filter.getFitableIds().defined() && !filter.getPropertyIds().defined()
                && !filter.getSourceIds().defined();
    }

    @Override
    public TriggerEntity retrieve(String taskId, String sourceId, String triggerId, OperationContext context) {
        String actualTriggerId = triggerValidator.validateTriggerId(triggerId, context);
        return triggerMapper.retrieve(actualTriggerId);
    }

    @Override
    public Map<String, List<TriggerEntity>> list(TriggerFilter filter, OperationContext context) {
        List<TriggerObject> triggerObjects = triggerMapper.list(filter.getIds().withDefault(Collections.emptyList()),
                filter.getSourceIds().withDefault(Collections.emptyList()),
                filter.getPropertyIds().withDefault(Collections.emptyList()),
                filter.getFitableIds().withDefault(Collections.emptyList()));
        if (CollectionUtils.isEmpty(triggerObjects)) {
            return Collections.emptyMap();
        }
        return triggerObjects.stream()
                .collect(Collectors.groupingBy(TriggerObject::getTaskSourceId,
                        Collectors.mapping(this::toTriggerEntity, Collectors.toList())));
    }

    @Override
    @Transactional
    public void batchSave(String taskId, List<SourceTriggersDeclaration> declarations, OperationContext context) {
        String actualTaskId = triggerValidator.validateTaskId(taskId, context);
        if (CollectionUtils.isEmpty(declarations)) {
            return;
        }
        List<TriggerObject> triggerObjects = new ArrayList<>();
        for (SourceTriggersDeclaration declaration : declarations) {
            if (CollectionUtils.isEmpty(declaration.getTriggers())) {
                continue;
            }
            for (TriggerDeclaration triggerDeclaration : declaration.getTriggers()) {
                String taskPropertyId = triggerMapper.selectTaskPropertyIdByTaskIdAndName(actualTaskId,
                        triggerDeclaration.getPropertyName().get());
                if (StringUtils.isEmpty(taskPropertyId)) {
                    continue;
                }
                triggerObjects.add(TriggerObject.builder()
                        .id(Entities.generateId())
                        .fitableId(triggerDeclaration.getFitableId().get())
                        .taskSourceId(declaration.getSourceId())
                        .taskPropertyId(taskPropertyId)
                        .build());
            }
        }
        if (!triggerObjects.isEmpty()) {
            List<String> idList = triggerMapper.batchSave(triggerObjects);
            List<String> sourceIdList = declarations.stream()
                    .map(SourceTriggersDeclaration::getSourceId)
                    .collect(Collectors.toList());
            triggerMapper.batchDelete(sourceIdList, idList);
        }
    }

    /**
     * 转换成PropertyEntity对象
     *
     * @param triggerObject 触发对象
     * @return the property entity
     */
    private TriggerEntity toTriggerEntity(TriggerObject triggerObject) {
        TriggerEntity triggerEntity = new TriggerEntity();
        triggerEntity.setId(triggerObject.getId());
        triggerEntity.setFitableId(triggerObject.getFitableId());
        triggerEntity.setPropertyId(triggerObject.getTaskPropertyId());
        return triggerEntity;
    }
}