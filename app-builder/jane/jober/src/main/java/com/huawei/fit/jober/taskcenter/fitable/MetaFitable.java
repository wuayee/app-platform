/*
 *  Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.fitable;

import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jane.meta.MetaService;
import com.huawei.fit.jane.meta.definition.Meta;
import com.huawei.fit.jane.meta.definition.MetaDeclarationInfo;
import com.huawei.fit.jane.meta.definition.MetaFilter;
import com.huawei.fit.jane.meta.property.MetaPropertyDeclarationInfo;
import com.huawei.fit.jane.task.util.Entities;
import com.huawei.fit.jane.task.util.UndefinableValue;
import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.RangedResultSet;
import com.huawei.fit.jober.common.exceptions.BadRequestException;
import com.huawei.fit.jober.common.util.ParamUtils;
import com.huawei.fit.jober.entity.task.TaskProperty;
import com.huawei.fit.jober.taskcenter.declaration.TaskDeclaration;
import com.huawei.fit.jober.taskcenter.domain.TaskEntity;
import com.huawei.fit.jober.taskcenter.domain.TaskType;
import com.huawei.fit.jober.taskcenter.eventhandler.converter.MetaConverter;
import com.huawei.fit.jober.taskcenter.eventhandler.converter.MetaPropertyConverter;

import modelengine.fitframework.annotation.Alias;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.transaction.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * {@link MetaService}的实现类。
 *
 * @author 孙怡菲
 * @since 2023-12-12
 */
@Alias("Jane-Meta")
@Component
public class MetaFitable implements MetaService {
    private final com.huawei.fit.jober.taskcenter.service.TaskService taskService;

    private final com.huawei.fit.jane.task.domain.TaskProperty.Repo taskPropertyRepo;

    private final MetaConverter metaConverter;

    private final MetaPropertyConverter metaPropertyConverter;

    private final TaskType.Repo taskType;

    public MetaFitable(com.huawei.fit.jober.taskcenter.service.TaskService taskService,
            com.huawei.fit.jane.task.domain.TaskProperty.Repo taskPropertyRepo, MetaConverter metaConverter,
            MetaPropertyConverter metaPropertyConverter, TaskType.Repo taskType) {
        this.taskService = taskService;
        this.taskPropertyRepo = taskPropertyRepo;
        this.metaConverter = metaConverter;
        this.metaPropertyConverter = metaPropertyConverter;
        this.taskType = taskType;
    }

    @Override
    @Fitable(id = "550e8400e29b41d4a716446655440000")
    @Transactional
    public Meta create(MetaDeclarationInfo declaration, OperationContext context) {
        Validation.notNull(declaration,
                () -> new BadRequestException(ErrorCodes.INPUT_PARAM_IS_EMPTY, "metaDeclarationInfo"));
        com.huawei.fit.jane.task.util.OperationContext operationContext = ParamUtils.convertToInternalOperationContext(
                context);
        TaskDeclaration taskDeclaration = metaConverter.convert(declaration);
        taskDeclaration.setTemplateId(UndefinableValue.defined(Entities.emptyId()));
        TaskEntity task = taskService.create(taskDeclaration, operationContext);
        String metaId = task.getId();
        String name = task.getName();
        taskType.create(metaId, this.getDefaultTaskType(name), operationContext);
        return metaConverter.convert(task, operationContext);
    }

    @Override
    @Fitable(id = "c1a65c3d3a244f2a95f56b2881539b12")
    public void patch(String metaId, MetaDeclarationInfo declaration, OperationContext context) {
        Validation.notNull(declaration,
                () -> new BadRequestException(ErrorCodes.INPUT_PARAM_IS_EMPTY, "metaDeclarationInfo"));
        TaskDeclaration taskDeclaration = metaConverter.convert(declaration);
        taskService.patch(metaId, taskDeclaration, ParamUtils.convertToInternalOperationContext(context));
    }

    @Override
    @Transactional
    @Fitable(id = "1be42d7e35a54a7c8d0e1779c5a6c220")
    public void delete(String metaId, OperationContext context) {
        com.huawei.fit.jane.task.util.OperationContext operationContext = ParamUtils.convertToInternalOperationContext(
                context);
        taskType.deleteByTasks(metaId, operationContext);
        taskService.delete(metaId, operationContext);
    }

    @Override
    @Fitable(id = "3610778319b9407892a0f4a62564df34")
    public RangedResultSet<Meta> list(MetaFilter filter, long offset, int limit, OperationContext context) {
        com.huawei.fit.jane.task.util.OperationContext operationContext = ParamUtils.convertToInternalOperationContext(
                context);
        modelengine.fitframework.model.RangedResultSet<TaskEntity>
                taskEntityRangedResultSet = taskService.list(metaConverter.convert(filter), offset, limit,
                operationContext);
        return RangedResultSet.create(this.convert(taskEntityRangedResultSet.getResults(), operationContext),
                taskEntityRangedResultSet.getRange().getOffset(), taskEntityRangedResultSet.getRange().getLimit(),
                taskEntityRangedResultSet.getRange().getTotal());
    }

    @Override
    @Fitable(id = "3fab66471eec49d3ab6cc01f23b2d7d8")
    public Meta retrieve(String metaId, OperationContext context) {
        com.huawei.fit.jane.task.util.OperationContext operationContext = ParamUtils.convertToInternalOperationContext(
                context);
        TaskEntity entity = taskService.retrieve(metaId, operationContext);
        return metaConverter.convert(entity, operationContext);
    }

    @Override
    @Fitable(id = "3fab66471eec49d3ab6cc01f23b2d7d8")
    public TaskProperty createProperty(String metaId, MetaPropertyDeclarationInfo declaration,
            OperationContext context) {
        return metaPropertyConverter.convert(taskPropertyRepo.create(metaId, metaPropertyConverter.convert(declaration),
                ParamUtils.convertToInternalOperationContext(context)));
    }

    @Override
    @Fitable(id = "3fab66471eec49d3ab6cc01f23b2d7d8")
    public void patchProperty(String metaId, String propertyId, MetaPropertyDeclarationInfo declaration,
            OperationContext context) {
        taskPropertyRepo.patch(metaId, propertyId, metaPropertyConverter.convert(declaration),
                ParamUtils.convertToInternalOperationContext(context));
    }

    @Override
    @Fitable(id = "3fab66471eec49d3ab6cc01f23b2d7d8")
    public void deleteProperty(String metaId, String propertyId, OperationContext context) {
        taskPropertyRepo.delete(metaId, propertyId, ParamUtils.convertToInternalOperationContext(context));
    }

    private List<Meta> convert(List<TaskEntity> entityList, com.huawei.fit.jane.task.util.OperationContext context) {
        return entityList.stream().map(entity -> metaConverter.convert(entity, context)).collect(Collectors.toList());
    }

    private TaskType.Declaration getDefaultTaskType(String name) {
        return TaskType.Declaration.custom().name(name).build();
    }
}
