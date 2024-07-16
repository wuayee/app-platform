/*
 *  Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.fitable;

import static com.huawei.fit.jober.common.util.ParamUtils.convertToInternalOperationContext;

import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jane.meta.multiversion.MetaInstanceService;
import com.huawei.fit.jane.meta.multiversion.instance.Instance;
import com.huawei.fit.jane.meta.multiversion.instance.InstanceDeclarationInfo;
import com.huawei.fit.jane.meta.multiversion.instance.MetaInstanceFilter;
import com.huawei.fit.jane.task.util.PagedResultSet;
import com.huawei.fit.jane.task.util.Pagination;
import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.RangedResultSet;
import com.huawei.fit.jober.taskcenter.domain.TaskEntity;
import com.huawei.fit.jober.taskcenter.domain.TaskInstance;
import com.huawei.fit.jober.taskcenter.domain.ViewMode;
import com.huawei.fit.jober.taskcenter.eventhandler.converter.MetaInstanceConverter;
import com.huawei.fit.jober.taskcenter.service.TaskService;
import com.huawei.fitframework.annotation.Alias;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.fitframework.inspection.Validation;

import java.util.List;
import java.util.stream.Collectors;

/**
 * {@link MetaInstanceService}的实现类。
 *
 * @author 孙怡菲 s00664640
 * @since 2023-12-12
 */
@Alias("Jane-MetaInstance")
@Component
public class MetaInstanceFitable implements MetaInstanceService {
    private final TaskService taskService;

    private final TaskInstance.Repo repo;

    private final MetaInstanceConverter converter;

    public MetaInstanceFitable(TaskService taskService, TaskInstance.Repo repo, MetaInstanceConverter converter) {
        this.taskService = taskService;
        this.repo = repo;
        this.converter = converter;
    }

    @Override
    @Fitable(id = "373a02c4fbd24edcaf6aa9fe69105dcc")
    public Instance createMetaInstance(String metaId, InstanceDeclarationInfo instanceDeclarationInfo,
            OperationContext context) {
        Validation.notNull(metaId,
                () -> new com.huawei.fit.jober.common.exceptions.BadRequestException(ErrorCodes.TASK_ID_INVALID));
        Validation.notNull(instanceDeclarationInfo,
                () -> new com.huawei.fit.jober.common.exceptions.BadRequestException(ErrorCodes.INPUT_PARAM_IS_EMPTY,
                        "instanceDeclarationInfo"));
        com.huawei.fit.jane.task.util.OperationContext actualContext = convertToInternalOperationContext(context);
        TaskEntity meta = taskService.retrieve(metaId, actualContext);
        String typeId = meta.getTypes().get(0).id();
        TaskInstance.Declaration declaration = this.converter.convert(instanceDeclarationInfo);
        declaration = declaration.copy().type(typeId).build();
        TaskInstance instance = this.repo.create(meta, declaration, actualContext);
        return this.converter.convert(meta, instance);
    }

    @Override
    @Fitable(id = "634740e4b0c444cea5a7bdb5a134fc31")
    public void patchMetaInstance(String metaId, String instanceId, InstanceDeclarationInfo instanceDeclarationInfo,
            OperationContext context) {
        Validation.notNull(metaId,
                () -> new com.huawei.fit.jober.common.exceptions.BadRequestException(ErrorCodes.TASK_ID_INVALID));
        Validation.notNull(instanceId,
                () -> new com.huawei.fit.jober.common.exceptions.BadRequestException(ErrorCodes.INPUT_PARAM_IS_EMPTY,
                        "instanceId"));
        Validation.notNull(instanceDeclarationInfo,
                () -> new com.huawei.fit.jober.common.exceptions.BadRequestException(ErrorCodes.INPUT_PARAM_IS_EMPTY,
                        "instanceDeclarationInfo"));
        com.huawei.fit.jane.task.util.OperationContext actualContext = convertToInternalOperationContext(context);
        TaskEntity meta = this.taskService.retrieve(metaId, actualContext);
        TaskInstance.Declaration declaration = converter.convert(instanceDeclarationInfo);
        this.repo.patch(meta, instanceId, declaration, actualContext);
    }

    @Override
    @Fitable(id = "72a83a3855064eaca82bf74d65649e70")
    public void deleteMetaInstance(String metaId, String instanceId, OperationContext context) {
        Validation.notNull(metaId,
                () -> new com.huawei.fit.jober.common.exceptions.BadRequestException(ErrorCodes.TASK_ID_INVALID));
        Validation.notNull(instanceId,
                () -> new com.huawei.fit.jober.common.exceptions.BadRequestException(ErrorCodes.INPUT_PARAM_IS_EMPTY,
                        "instanceId"));
        com.huawei.fit.jane.task.util.OperationContext actualContext = convertToInternalOperationContext(context);
        TaskEntity meta = this.taskService.retrieve(metaId, actualContext);
        this.repo.delete(meta, instanceId, actualContext);
    }

    @Override
    @Fitable(id = "47631a35ba1d49cba6502412a5bf3973")
    public RangedResultSet<Instance> list(String metaId, MetaInstanceFilter filter, long offset, int limit,
            OperationContext context) {
        Validation.notNull(metaId,
                () -> new com.huawei.fit.jober.common.exceptions.BadRequestException(ErrorCodes.TASK_ID_INVALID));
        Validation.notNull(filter,
                () -> new com.huawei.fit.jober.common.exceptions.BadRequestException(ErrorCodes.INPUT_PARAM_IS_EMPTY,
                        "filter"));
        com.huawei.fit.jane.task.util.OperationContext actualContext = convertToInternalOperationContext(context);
        TaskEntity meta = taskService.retrieve(metaId, actualContext);
        PagedResultSet<TaskInstance> instances = this.repo.list(meta, this.converter.convert(filter),
                Pagination.create(offset, limit), this.converter.convertOrderBys(filter.getOrderBy()),
                ViewMode.LIST, actualContext);
        return RangedResultSet.create(this.convert(meta, instances.results()),
                instances.pagination().offset(), instances.pagination().limit(), instances.pagination().total());
    }

    @Override
    @Fitable(id = "47631a35ba1d49cba6502412a5bf3972")
    public String getMetaVersionId(String id) {
        return this.repo.getMetaId(id);
    }

    private List<Instance> convert(TaskEntity task, List<TaskInstance> instances) {
        return instances.stream().map(instance -> this.converter.convert(task, instance)).collect(Collectors.toList());
    }
}
