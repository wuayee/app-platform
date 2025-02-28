/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.fitable;

import static modelengine.fit.jober.common.util.ParamUtils.convertToInternalOperationContext;

import modelengine.fit.jane.task.util.PagedResultSet;
import modelengine.fit.jane.task.util.Pagination;
import modelengine.fit.jober.taskcenter.domain.TaskEntity;
import modelengine.fit.jober.taskcenter.domain.TaskInstance;
import modelengine.fit.jober.taskcenter.domain.ViewMode;
import modelengine.fit.jober.taskcenter.eventhandler.converter.MetaInstanceConverter;
import modelengine.fit.jober.taskcenter.service.TaskService;
import modelengine.fit.jober.taskcenter.util.sql.OrderBy;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jane.meta.MetaInstanceService;
import modelengine.fit.jane.meta.instance.Instance;
import modelengine.fit.jane.meta.instance.InstanceDeclarationInfo;
import modelengine.fit.jane.meta.instance.MetaInstanceFilter;
import modelengine.fit.jober.common.ErrorCodes;
import modelengine.fit.jober.common.RangedResultSet;
import modelengine.fit.jober.common.exceptions.BadRequestException;
import modelengine.fitframework.annotation.Alias;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.inspection.Validation;

import java.util.List;
import java.util.stream.Collectors;

/**
 * {@link MetaInstanceService}的实现类。
 *
 * @author 孙怡菲
 * @since 2023-12-12
 */
// 本类暂用于aipp未上线前，之后需要删除
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
        Validation.notNull(instanceDeclarationInfo, () -> new BadRequestException(ErrorCodes.INPUT_PARAM_IS_EMPTY,
                        "instanceDeclarationInfo"));
        Validation.notNull(metaId, () -> new BadRequestException(ErrorCodes.TASK_ID_INVALID));

        modelengine.fit.jane.task.util.OperationContext actualContext = convertToInternalOperationContext(context);
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
        Validation.notNull(instanceDeclarationInfo, () -> new BadRequestException(ErrorCodes.INPUT_PARAM_IS_EMPTY,
                        "instanceDeclarationInfo"));
        Validation.notNull(instanceId, () -> new BadRequestException(ErrorCodes.INPUT_PARAM_IS_EMPTY,
                        "instanceId"));
        Validation.notNull(metaId, () -> new BadRequestException(ErrorCodes.TASK_ID_INVALID));

        modelengine.fit.jane.task.util.OperationContext actualContext = convertToInternalOperationContext(context);
        TaskEntity meta = this.taskService.retrieve(metaId, actualContext);
        TaskInstance.Declaration declaration = converter.convert(instanceDeclarationInfo);
        this.repo.patch(meta, instanceId, declaration, actualContext);
    }

    @Override
    @Fitable(id = "72a83a3855064eaca82bf74d65649e70")
    public void deleteMetaInstance(String metaId, String instanceId, OperationContext context) {
        Validation.notNull(instanceId, () -> new BadRequestException(ErrorCodes.INPUT_PARAM_IS_EMPTY,
                        "instanceId"));
        Validation.notNull(metaId, () -> new BadRequestException(ErrorCodes.TASK_ID_INVALID));

        TaskEntity meta = this.taskService.retrieve(metaId, convertToInternalOperationContext(context));
        this.repo.delete(meta, instanceId, convertToInternalOperationContext(context));
    }

    @Override
    @Fitable(id = "47631a35ba1d49cba6502412a5bf3973")
    public RangedResultSet<Instance> list(String metaId, MetaInstanceFilter filter, long offset, int limit,
            OperationContext context) {
        Validation.notNull(filter, () -> new BadRequestException(ErrorCodes.INPUT_PARAM_IS_EMPTY,
                        "filter"));
        Validation.notNull(metaId, () -> new BadRequestException(ErrorCodes.TASK_ID_INVALID));

        modelengine.fit.jane.task.util.OperationContext actualContext = convertToInternalOperationContext(context);
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
