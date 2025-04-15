/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.domains.taskinstance.service.impl;

import static modelengine.fitframework.util.ObjectUtils.nullIf;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jane.common.enums.DirectionEnum;
import modelengine.fit.jane.meta.multiversion.MetaInstanceService;
import modelengine.fit.jane.meta.multiversion.instance.Instance;
import modelengine.fit.jane.meta.multiversion.instance.InstanceDeclarationInfo;
import modelengine.fit.jane.meta.multiversion.instance.MetaInstanceFilter;
import modelengine.fit.jober.aipp.constants.AippConst;
import modelengine.fit.jober.aipp.domains.taskinstance.AppTaskInstance;
import modelengine.fit.jober.aipp.domains.taskinstance.AppTaskInstanceFactory;
import modelengine.fit.jober.aipp.domains.taskinstance.TaskInstanceQueryEntity;
import modelengine.fit.jober.aipp.domains.taskinstance.service.AppTaskInstanceService;
import modelengine.fit.jober.aipp.enums.MetaInstSortKeyEnum;
import modelengine.fit.jober.aipp.util.MetaUtils;
import modelengine.fit.jober.common.RangeResult;
import modelengine.fit.jober.common.RangedResultSet;

import lombok.RequiredArgsConstructor;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.util.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 应用任务实例服务实现类.
 *
 * @author 张越
 * @since 2024-12-31
 */
@Component
@RequiredArgsConstructor
public class AppTaskInstanceServiceImpl implements AppTaskInstanceService {
    private final MetaInstanceService metaInstanceService;
    private final AppTaskInstanceFactory factory;

    @Override
    public Optional<AppTaskInstance> getInstance(String taskId, String taskInstanceId, OperationContext context) {
        MetaInstanceFilter filter = new MetaInstanceFilter();
        filter.setIds(Collections.singletonList(taskInstanceId));
        List<Instance> list = metaInstanceService.list(taskId, filter, 0, 1, context).getResults();
        if (list.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(list.get(0)).map(instance -> this.factory.create(instance, taskId, this));
    }

    @Override
    public RangedResultSet<AppTaskInstance> getInstances(String taskId, AppTaskInstance query, long offset, int limit,
            OperationContext context) {
        RangedResultSet<Instance> resultSet = this.metaInstanceService.list(taskId,
                this.buildFilter(query.getEntity()), offset, limit, context);
        RangeResult range = resultSet.getRange();
        List<AppTaskInstance> taskInstances = resultSet.getResults()
                .stream()
                .map(instance -> this.factory.create(instance, taskId, this))
                .toList();
        return RangedResultSet.create(taskInstances, range.getOffset(), range.getLimit(), range.getTotal());
    }

    private MetaInstanceFilter buildFilter(TaskInstanceQueryEntity queryEntity) {
        // 设置sort和order.
        MetaInstanceFilter filter = new MetaInstanceFilter();
        String order = nullIf(queryEntity.getOrder(), DirectionEnum.DESCEND.name());
        String sort = nullIf(queryEntity.getSort(), MetaInstSortKeyEnum.START_TIME.name());
        List<String> orderBy = Collections.singletonList(
                String.format(Locale.ROOT, "%s(info.%s)", DirectionEnum.getDirection(order).getValue(),
                        MetaInstSortKeyEnum.getInstSortKey(sort).getKey()));
        filter.setOrderBy(orderBy);

        // 设置创建人.
        Map<String, List<String>> infos = new HashMap<>();
        String creator = queryEntity.getCreator();
        if (StringUtils.isNotBlank(creator)) {
            infos.put(AippConst.INST_CREATOR_KEY, Collections.singletonList(creator));
        }

        // 设置实例名称.
        String instanceName = queryEntity.getName();
        if (StringUtils.isNotBlank(instanceName)) {
            infos.put(AippConst.INST_NAME_KEY, Collections.singletonList(instanceName));
        }
        filter.setInfos(infos);
        return filter;
    }

    @Override
    public List<AppTaskInstance> getInstancesByTaskId(String taskId, int limit, OperationContext context) {
        MetaInstanceFilter filter = new MetaInstanceFilter();
        return MetaUtils.getAllFromRangedResult(limit,
                        (os) -> metaInstanceService.list(taskId, filter, os, limit, context))
                .map(instance -> this.factory.create(instance, taskId, this))
                .collect(Collectors.toList());
    }

    @Override
    public Stream<AppTaskInstance> getInstanceStreamByTaskId(String taskId, int limit, OperationContext context) {
        return MetaUtils.getAllFromRangedResult(limit,
                        os -> metaInstanceService.list(taskId, new MetaInstanceFilter(), os, limit, context))
                .map(instance -> this.factory.create(instance, taskId, this));
    }

    @Override
    public void update(AppTaskInstance instance, OperationContext context) {
        InstanceDeclarationInfo declarationInfo = this.factory.toDeclarationInfo(instance);
        this.metaInstanceService.patchMetaInstance(instance.getTaskId(), instance.getId(), declarationInfo, context);
    }

    @Override
    public AppTaskInstance createInstance(AppTaskInstance instance, OperationContext context) {
        InstanceDeclarationInfo declarationInfo = this.factory.toDeclarationInfo(instance);
        Instance metaInst = this.metaInstanceService.createMetaInstance(instance.getTaskId(), declarationInfo, context);
        return this.factory.create(metaInst, instance.getTaskId(), this);
    }

    @Override
    public void delete(String taskId, String taskInstanceId, OperationContext context) {
        this.metaInstanceService.deleteMetaInstance(taskId, taskInstanceId, context);
    }

    @Override
    public String getTaskId(String taskInstanceId) {
        return this.metaInstanceService.getMetaVersionId(taskInstanceId);
    }

    @Override
    public Optional<AppTaskInstance> getInstanceById(String taskInstanceId, OperationContext context) {
        String taskId = this.getTaskId(taskInstanceId);
        Instance metaInst = this.metaInstanceService.retrieveById(taskInstanceId, context);
        return Optional.ofNullable(metaInst).map(i -> this.factory.create(i, taskId, this));
    }
}