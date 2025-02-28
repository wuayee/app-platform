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

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jane.meta.multiversion.MetaInstanceService;
import modelengine.fit.jane.meta.multiversion.instance.Instance;
import modelengine.fit.jane.meta.multiversion.instance.InstanceDeclarationInfo;
import modelengine.fit.jane.meta.multiversion.instance.MetaInstanceFilter;
import modelengine.fit.jober.common.ErrorCodes;
import modelengine.fit.jober.common.RangedResultSet;
import modelengine.fit.jober.common.exceptions.BadRequestException;
import modelengine.fit.jober.common.exceptions.GoneException;
import modelengine.fit.jober.common.exceptions.NotFoundException;
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
        Validation.notNull(metaId, () -> new BadRequestException(ErrorCodes.TASK_ID_INVALID));
        Validation.notNull(instanceDeclarationInfo, () -> new BadRequestException(ErrorCodes.INPUT_PARAM_IS_EMPTY,
                        "instanceDeclarationInfo"));
        modelengine.fit.jane.task.util.OperationContext actualContext = convertToInternalOperationContext(context);
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
        Validation.notNull(metaId, () -> new BadRequestException(ErrorCodes.TASK_ID_INVALID));
        Validation.notNull(instanceId, () -> new BadRequestException(ErrorCodes.INPUT_PARAM_IS_EMPTY,
                        "instanceId"));
        Validation.notNull(instanceDeclarationInfo, () -> new BadRequestException(ErrorCodes.INPUT_PARAM_IS_EMPTY,
                        "instanceDeclarationInfo"));
        modelengine.fit.jane.task.util.OperationContext actualContext = convertToInternalOperationContext(context);
        TaskEntity meta = this.taskService.retrieve(metaId, actualContext);
        TaskInstance.Declaration declaration = converter.convert(instanceDeclarationInfo);
        this.repo.patch(meta, instanceId, declaration, actualContext);
    }

    @Override
    @Fitable(id = "72a83a3855064eaca82bf74d65649e70")
    public void deleteMetaInstance(String metaId, String instanceId, OperationContext context) {
        Validation.notNull(metaId, () -> new BadRequestException(ErrorCodes.TASK_ID_INVALID));
        Validation.notNull(instanceId, () -> new BadRequestException(ErrorCodes.INPUT_PARAM_IS_EMPTY,
                        "instanceId"));
        modelengine.fit.jane.task.util.OperationContext actualContext = convertToInternalOperationContext(context);
        TaskEntity meta = this.taskService.retrieve(metaId, actualContext);
        this.repo.delete(meta, instanceId, actualContext);
    }

    @Override
    @Fitable(id = "47631a35ba1d49cba6502412a5bf3973")
    public RangedResultSet<Instance> list(String metaId, MetaInstanceFilter filter, long offset, int limit,
            OperationContext context) {
        Validation.notNull(metaId, () -> new BadRequestException(ErrorCodes.TASK_ID_INVALID));
        Validation.notNull(filter, () -> new BadRequestException(ErrorCodes.INPUT_PARAM_IS_EMPTY,
                        "filter"));
        modelengine.fit.jane.task.util.OperationContext actualContext = convertToInternalOperationContext(context);
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

    @Override
    @Fitable(id = "47631a35ba1d49cba6502412a5b26483")
    public Instance retrieveById(String instanceId, OperationContext context) {
        modelengine.fit.jane.task.util.OperationContext actualContext = convertToInternalOperationContext(context);
        String taskId = this.repo.getMetaId(instanceId);
        TaskEntity task = this.taskService.retrieve(taskId, actualContext);
        try {
            TaskInstance taskInstance = this.repo.retrieve(task, instanceId, false, actualContext);
            return this.converter.convert(task, taskInstance);
        } catch (GoneException | NotFoundException e) {
            return null;
        }
    }

    private List<Instance> convert(TaskEntity task, List<TaskInstance> instances) {
        return instances.stream().map(instance -> this.converter.convert(task, instance)).collect(Collectors.toList());
    }
}
