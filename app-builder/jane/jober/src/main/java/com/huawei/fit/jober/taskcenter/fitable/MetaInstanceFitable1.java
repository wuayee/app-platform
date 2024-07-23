/*
 *  Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.fitable;

import static com.huawei.fit.jober.common.util.ParamUtils.convertToInternalOperationContext;

import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jane.meta.MetaInstanceService;
import com.huawei.fit.jane.meta.instance.Instance;
import com.huawei.fit.jane.meta.instance.InstanceDeclarationInfo;
import com.huawei.fit.jane.meta.instance.MetaInstanceFilter;
import com.huawei.fit.jane.task.util.PagedResultSet;
import com.huawei.fit.jane.task.util.Pagination;
import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.RangedResultSet;
import com.huawei.fit.jober.taskcenter.domain.TaskEntity;
import com.huawei.fit.jober.taskcenter.domain.TaskInstance;
import com.huawei.fit.jober.taskcenter.domain.ViewMode;
import com.huawei.fit.jober.taskcenter.eventhandler.converter.MetaInstanceConverter;
import com.huawei.fit.jober.taskcenter.service.TaskService;
import com.huawei.fit.jober.taskcenter.util.sql.OrderBy;
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
// FIXME: 2024/3/29 0029 本类暂用于aipp未上线前，之后需要删除
@Alias("Jane-MetaInstance1")
@Component
public class MetaInstanceFitable1 implements MetaInstanceService {
    private final TaskService taskService;

    private final TaskInstance.Repo repo;

    private final MetaInstanceConverter converter;

    public MetaInstanceFitable1(TaskService taskService, TaskInstance.Repo repo, MetaInstanceConverter converter) {
        this.taskService = taskService;
        this.repo = repo;
        this.converter = converter;
    }

    @Override
    @Fitable(id = "373a02c4fbd24edcaf6aa9fe69105dcc")
    public Instance createMetaInstance(String metaId, InstanceDeclarationInfo instanceDeclarationInfo,
            OperationContext context) {
        Validation.notNull(instanceDeclarationInfo,
                () -> new com.huawei.fit.jober.common.exceptions.BadRequestException(ErrorCodes.INPUT_PARAM_IS_EMPTY,
                        "instanceDeclarationInfo"));
        Validation.notNull(metaId,
                () -> new com.huawei.fit.jober.common.exceptions.BadRequestException(ErrorCodes.TASK_ID_INVALID));

        com.huawei.fit.jane.task.util.OperationContext actualContext = convertToInternalOperationContext(context);
        TaskInstance.Declaration declaration = this.converter.convert(instanceDeclarationInfo);
        TaskEntity meta = taskService.retrieve(metaId, actualContext);
        String typeId = meta.getTypes().get(0).id();
        declaration = declaration.copy().type(typeId).build();
        TaskInstance instance = this.repo.create(meta, declaration, actualContext);
        return this.converter.convert1(meta, instance);
    }

    @Override
    @Fitable(id = "634740e4b0c444cea5a7bdb5a134fc31")
    public void patchMetaInstance(String metaId, String instanceId, InstanceDeclarationInfo instanceDeclarationInfo,
            OperationContext context) {
        Validation.notNull(instanceDeclarationInfo,
                () -> new com.huawei.fit.jober.common.exceptions.BadRequestException(ErrorCodes.INPUT_PARAM_IS_EMPTY,
                        "instanceDeclarationInfo"));
        Validation.notNull(instanceId,
                () -> new com.huawei.fit.jober.common.exceptions.BadRequestException(ErrorCodes.INPUT_PARAM_IS_EMPTY,
                        "instanceId"));
        Validation.notNull(metaId,
                () -> new com.huawei.fit.jober.common.exceptions.BadRequestException(ErrorCodes.TASK_ID_INVALID));

        com.huawei.fit.jane.task.util.OperationContext actualContext = convertToInternalOperationContext(context);
        TaskEntity meta = this.taskService.retrieve(metaId, actualContext);
        TaskInstance.Declaration declaration = converter.convert(instanceDeclarationInfo);
        this.repo.patch(meta, instanceId, declaration, actualContext);
    }

    @Override
    @Fitable(id = "72a83a3855064eaca82bf74d65649e70")
    public void deleteMetaInstance(String metaId, String instanceId, OperationContext context) {
        Validation.notNull(instanceId,
                () -> new com.huawei.fit.jober.common.exceptions.BadRequestException(ErrorCodes.INPUT_PARAM_IS_EMPTY,
                        "instanceId"));
        Validation.notNull(metaId,
                () -> new com.huawei.fit.jober.common.exceptions.BadRequestException(ErrorCodes.TASK_ID_INVALID));

        TaskEntity meta = this.taskService.retrieve(metaId, convertToInternalOperationContext(context));
        this.repo.delete(meta, instanceId, convertToInternalOperationContext(context));
    }

    @Override
    @Fitable(id = "47631a35ba1d49cba6502412a5bf3973")
    public RangedResultSet<Instance> list(String metaId, MetaInstanceFilter filter, long offset, int limit,
            OperationContext context) {
        Validation.notNull(filter,
                () -> new com.huawei.fit.jober.common.exceptions.BadRequestException(ErrorCodes.INPUT_PARAM_IS_EMPTY,
                        "filter"));
        Validation.notNull(metaId,
                () -> new com.huawei.fit.jober.common.exceptions.BadRequestException(ErrorCodes.TASK_ID_INVALID));

        com.huawei.fit.jane.task.util.OperationContext actualContext = convertToInternalOperationContext(context);
        TaskEntity meta = taskService.retrieve(metaId, actualContext);
        List<OrderBy> orderBys = this.converter.convertOrderBys(filter.getOrderBy());
        PagedResultSet<TaskInstance> instances = this.repo.list(meta, this.converter.convert(filter),
                Pagination.create(offset, limit), orderBys, ViewMode.LIST, actualContext);
        return RangedResultSet.create(this.convert(meta, instances.results()),
                instances.pagination().offset(), instances.pagination().limit(), instances.pagination().total());
    }

    private List<Instance> convert(TaskEntity task, List<TaskInstance> instances) {
        return instances.stream()
                .map(instance -> this.converter.convert1(task, instance))
                .collect(Collectors.toList());
    }
}
