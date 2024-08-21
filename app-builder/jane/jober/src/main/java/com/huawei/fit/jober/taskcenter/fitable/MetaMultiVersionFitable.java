/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.fitable;

import com.huawei.fit.jane.Undefinable;
import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jane.meta.multiversion.MetaService;
import com.huawei.fit.jane.meta.multiversion.definition.Meta;
import com.huawei.fit.jane.meta.multiversion.definition.MetaDeclarationInfo;
import com.huawei.fit.jane.meta.multiversion.definition.MetaFilter;
import com.huawei.fit.jane.meta.property.MetaPropertyDeclarationInfo;
import com.huawei.fit.jane.task.util.UndefinableValue;
import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.RangedResultSet;
import com.huawei.fit.jober.common.exceptions.BadRequestException;
import com.huawei.fit.jober.common.exceptions.ConflictException;
import com.huawei.fit.jober.common.util.ParamUtils;
import com.huawei.fit.jober.entity.task.TaskProperty;
import com.huawei.fit.jober.taskcenter.declaration.TaskDeclaration;
import com.huawei.fit.jober.taskcenter.domain.TaskEntity;
import com.huawei.fit.jober.taskcenter.domain.TaskTemplate;
import com.huawei.fit.jober.taskcenter.domain.TaskType;
import com.huawei.fit.jober.taskcenter.eventhandler.converter.MetaConverter;
import com.huawei.fit.jober.taskcenter.eventhandler.converter.MetaPropertyConverter;
import com.huawei.fit.jober.taskcenter.service.TaskService;
import modelengine.fitframework.annotation.Alias;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.transaction.Transactional;
import modelengine.fitframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Meta多版本 {@link MetaService} 的默认实现。
 *
 * @author 陈镕希
 * @since 2024-02-20
 */
@Alias("Jane-MultiVersionMeta")
@Component
public class MetaMultiVersionFitable implements MetaService {
    private final TaskService taskService;

    private final com.huawei.fit.jane.task.domain.TaskProperty.Repo taskPropertyRepo;

    private final MetaConverter metaConverter;

    private final MetaPropertyConverter metaPropertyConverter;

    private final TaskType.Repo taskTypeRepo;

    private final TaskTemplate.Repo taskTemplateRepo;

    public MetaMultiVersionFitable(TaskService taskService,
            com.huawei.fit.jane.task.domain.TaskProperty.Repo taskPropertyRepo, MetaConverter metaConverter,
            MetaPropertyConverter metaPropertyConverter, TaskType.Repo taskTypeRepo,
            TaskTemplate.Repo taskTemplateRepo) {
        this.taskService = taskService;
        this.taskPropertyRepo = taskPropertyRepo;
        this.metaConverter = metaConverter;
        this.metaPropertyConverter = metaPropertyConverter;
        this.taskTypeRepo = taskTypeRepo;
        this.taskTemplateRepo = taskTemplateRepo;
    }

    @Override
    @Fitable(id = "7db0481945c94f8d9d7dd3c72f15efac")
    @Transactional
    public Meta create(MetaDeclarationInfo declaration, OperationContext context) {
        Validation.notNull(declaration,
                () -> new BadRequestException(ErrorCodes.INPUT_PARAM_IS_EMPTY, "metaDeclarationInfo"));
        com.huawei.fit.jane.task.util.OperationContext actualContext =
                ParamUtils.convertToInternalOperationContext(context);
        // 检查名称和版本信息
        Undefinable<String> name = declaration.getName(); // 必填
        Undefinable<String> version = declaration.getVersion(); // 必填
        if (!name.getDefined() || !version.getDefined()) {
            throw new BadRequestException(ErrorCodes.INPUT_PARAM_IS_EMPTY, "name or version");
        }
        declaration.setName(Undefinable.defined(name.getValue() + "|" + version.getValue()));
        TaskDeclaration taskDeclaration = metaConverter.convertMultiVersionDeclaration(declaration);

        // 模板设置
        String templateId = this.queryTemplateIdOrCreateTemplate(context,
                name.getValue(),
                declaration.getBasicMetaTemplateId(),
                actualContext);
        taskDeclaration.setTemplateId(UndefinableValue.defined(templateId));

        // call taskService save entity
        TaskEntity taskEntity = taskService.create(taskDeclaration, actualContext);
        String metaId = taskEntity.getId();
        String taskName = taskEntity.getName();
        taskTypeRepo.create(metaId, this.getDefaultTaskType(taskName), actualContext);
        return metaConverter.convert2MultiVersionMeta(taskEntity, actualContext);
    }

    private TaskType.Declaration getDefaultTaskType(String name) {
        return TaskType.Declaration.custom().name(name).build();
    }

    private String queryTemplateIdOrCreateTemplate(OperationContext context, String name,
            Undefinable<String> basicMetaTemplateId, com.huawei.fit.jane.task.util.OperationContext actualContext) {
        MetaFilter filter = new MetaFilter();
        filter.setNames(new ArrayList<String>() {
            {
                add(name + "|");
            }
        });

        RangedResultSet<Meta> list = list(filter, true, 0, 10, context);
        if (CollectionUtils.isNotEmpty(list.getResults())) {
            return list.getResults().get(0).getId();
        }
        TaskTemplate.Declaration.Builder builder = TaskTemplate.Declaration.custom().name(name);
        if (basicMetaTemplateId.getDefined()) {
            builder.parentTemplateId(basicMetaTemplateId.getValue());
        }
        TaskTemplate taskTemplate = taskTemplateRepo.create(builder.build(), actualContext);
        return taskTemplate.id();
    }

    @Override
    @Fitable(id = "af2e245a4cb74a6696e43c1e6df9a893")
    public void patch(String versionId, MetaDeclarationInfo declaration, OperationContext context) {
        // 已发布的不允许修改
        Validation.notNull(declaration,
                () -> new BadRequestException(ErrorCodes.INPUT_PARAM_IS_EMPTY, "metaDeclarationInfo"));
        TaskDeclaration taskDeclaration = metaConverter.convertMultiVersionDeclaration(declaration);
        // 对输入的name和version进行校验，如果需要修改，则必须二者同时传入，否则不能传入
        // 是否添加校验检查name的修改情况？name是不允许修改的
        if (declaration.getVersion().getDefined() && declaration.getName().getDefined()) {
            String name = declaration.getName().getValue() + "|" + declaration.getVersion().getValue();
            taskDeclaration.setName(UndefinableValue.defined(name));
        } else if (declaration.getVersion().getDefined()) {
            throw new BadRequestException(ErrorCodes.INPUT_PARAM_IS_EMPTY, "metaDeclarationInfo.name");
        } else if (declaration.getName().getDefined()) {
            throw new BadRequestException(ErrorCodes.INPUT_PARAM_IS_EMPTY, "metaDeclarationInfo.version");
        } else {
            taskDeclaration.setName(UndefinableValue.undefined());
        }
        taskService.patch(versionId, taskDeclaration, ParamUtils.convertToInternalOperationContext(context));
    }

    @Override
    @Fitable(id = "0d260d17c4994f6696414f59cfcc05b3")
    public void publish(String versionId, OperationContext context) {
        // 发布：修改attributes，添加一个字段
        com.huawei.fit.jane.task.util.OperationContext operationContext =
                ParamUtils.convertToInternalOperationContext(context);
        Map<String, Object> attributes = taskService.retrieve(versionId, operationContext).getAttributes();
        attributes.put("meta_status", "active");
        TaskDeclaration taskDeclaration = new TaskDeclaration();
        taskDeclaration.setAttributes(UndefinableValue.defined(attributes));
        taskService.patch(versionId, taskDeclaration, operationContext);
    }

    @Override
    @Fitable(id = "eae080e19c7541228f8ec018c9723c0d")
    @Transactional
    public void delete(String versionId, OperationContext context) {
        // 删除一个版本，但删除后需要查询该templateId是否被其他版本使用，若未被任何版本使用，删除模板
        com.huawei.fit.jane.task.util.OperationContext operationContext =
                ParamUtils.convertToInternalOperationContext(context);
        TaskEntity task = taskService.retrieve(versionId, operationContext);
        taskTypeRepo.deleteByTasks(versionId, operationContext);
        taskService.delete(versionId, operationContext);
        // 这里直接删除并吃掉异常：如果有其他版本在使用，则不删除，且不影响前面的任务删除
        try {
            taskTemplateRepo.delete(task.getTemplateId(), operationContext);
        } catch (ConflictException e) {
            if (e.getCode() != ErrorCodes.TASK_TEMPLATE_USED.getErrorCode()) {
                throw e;
            }
        }
    }

    @Override
    @Fitable(id = "340f4d3a399240c8bb60b2e3adbf0988")
    public RangedResultSet<Meta> list(MetaFilter filter, boolean isLatestOnly, long offset, int limit,
            OperationContext context) {
        return this.list(filter, isLatestOnly, offset, limit, context, null);
    }

    /**
     * 查询Meta。
     *
     * @param filter 表示meta过滤器的 {@link MetaFilter}。
     * @param isLatestOnly 表示每个Meta是否只显示最新版本。
     * @param offset 表示查询到的meta定义的结果集在全量结果集中的偏移量的 64 位整数。
     * @param limit 表示查询到的meta定义的结果集中的最大数量的 32 位整数。
     * @param context 表示操作上下文的 {@link OperationContext}。
     * @param oldDataFilter 表示旧数据过滤器的 {@link MetaFilter}。
     * @return 表示查询到的结果集的 {@link RangedResultSet}{@code <}{@link Meta}{@code >}。
     */
    @Override
    @Fitable(id = "340f4d3a399240c8bb60b2e3adbf0989")
    public RangedResultSet<Meta> list(MetaFilter filter, boolean isLatestOnly, long offset, int limit,
            OperationContext context, MetaFilter oldDataFilter) {
        com.huawei.fit.jane.task.util.OperationContext operationContext =
                ParamUtils.convertToInternalOperationContext(context);
        // 查最新版本 created_time
        // 查询：需要根据attributes进行区分查询 已发布/未发布
        modelengine.fitframework.model.RangedResultSet<TaskEntity> taskEntityRangedResultSet =
                taskService.listMeta(filter, isLatestOnly, offset, limit, operationContext);
        List<Meta> metaList = taskEntityRangedResultSet.getResults()
                .stream()
                .map(task -> metaConverter.convert2MultiVersionMeta(task, operationContext))
                .collect(Collectors.toList());
        // 以下为兼容逻辑：存在部分数据的metaId是指task的Id
        if ((CollectionUtils.isEmpty(metaList) && CollectionUtils.isNotEmpty(filter.getMetaIds()))
                || oldDataFilter != null) { // 这边是否只需考虑 oldDataFilter？需要待确认
            if (oldDataFilter == null) {
                filter.setVersionIds(filter.getMetaIds());
                filter.setMetaIds(Collections.emptyList());
                filter.setVersions(Collections.emptyList());
            } else {
                filter = oldDataFilter;
            }
            modelengine.fitframework.model.RangedResultSet<TaskEntity> taskEntityRangedResultSet1 =
                    this.taskService.listMeta(filter, isLatestOnly, offset, limit, operationContext);
            metaList.addAll(taskEntityRangedResultSet1.getResults()
                    .stream()
                    .map(task -> this.metaConverter.convert2MultiVersionMeta(task, operationContext))
                    .collect(Collectors.toList()));
        }

        // 临时的兼容逻辑，处理 aipp 的 id 为 00000000000000000000000000000000 的场景；等数据库数据刷完后，可以去掉该逻辑
        this.handleOldMetaList(metaList);
        return RangedResultSet.create(metaList,
                taskEntityRangedResultSet.getRange(),
                taskEntityRangedResultSet.getRange().getTotal());
    }

    private void handleOldMetaList(List<Meta> metaList) {
        metaList.stream()
                .filter(meta -> Objects.equals(meta.getId(), "00000000000000000000000000000000"))
                .forEach(meta -> {
                    meta.setId(meta.getVersionId());
                    Map<String, Object> attributes = meta.getAttributes();
                    if (!attributes.containsKey("aipp_type")) {
                        attributes.put("aipp_type", "NORMAL");
                    }
                });
    }

    @Override
    @Fitable(id = "77485aa34e8f4aae8d97c862c02ee0cd")
    public Meta retrieve(String versionId, OperationContext context) {
        com.huawei.fit.jane.task.util.OperationContext operationContext =
                ParamUtils.convertToInternalOperationContext(context);
        TaskEntity entity = taskService.retrieve(versionId, operationContext);
        return metaConverter.convert2MultiVersionMeta(entity, operationContext);
    }

    @Override
    @Fitable(id = "b5e17deabed44edf816f4bf429fbc769")
    public TaskProperty createProperty(String versionId, MetaPropertyDeclarationInfo declaration,
            OperationContext context) {
        return metaPropertyConverter.convert(taskPropertyRepo.create(versionId,
                metaPropertyConverter.convert(declaration),
                ParamUtils.convertToInternalOperationContext(context)));
    }

    @Override
    @Fitable(id = "14bcbba9eacd467b9e4df67b78e07750")
    public void patchProperty(String versionId, String propertyId, MetaPropertyDeclarationInfo declaration,
            OperationContext context) {
        taskPropertyRepo.patch(versionId,
                propertyId,
                metaPropertyConverter.convert(declaration),
                ParamUtils.convertToInternalOperationContext(context));
    }

    @Override
    @Fitable(id = "0a1be42389c24e27af5a6e6cfb0bb961")
    public void deleteProperty(String versionId, String propertyId, OperationContext context) {
        taskPropertyRepo.delete(versionId, propertyId, ParamUtils.convertToInternalOperationContext(context));
    }
}
