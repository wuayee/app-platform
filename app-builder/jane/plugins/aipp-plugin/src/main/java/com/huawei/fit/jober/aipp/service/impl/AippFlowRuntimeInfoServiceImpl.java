/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service.impl;

import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jane.meta.multiversion.MetaInstanceService;
import com.huawei.fit.jane.meta.multiversion.MetaService;
import com.huawei.fit.jane.meta.multiversion.definition.Meta;
import com.huawei.fit.jane.meta.multiversion.instance.Instance;
import com.huawei.fit.jober.aipp.common.MetaUtils;
import com.huawei.fit.jober.aipp.common.Utils;
import com.huawei.fit.jober.aipp.constants.AippConst;
import com.huawei.fit.jober.aipp.service.AippFlowRuntimeInfoService;
import com.huawei.fit.runtime.entity.RuntimeData;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * 流程运行时服务接口.
 *
 * @author z00559346 张越
 * @since 2024-05-25
 */
@Component
public class AippFlowRuntimeInfoServiceImpl implements AippFlowRuntimeInfoService {
    private final Map<String, List<RuntimeData>> cache = new ConcurrentHashMap<>();
    private final MetaService metaService;
    private final MetaInstanceService metaInstanceService;

    public AippFlowRuntimeInfoServiceImpl(MetaService metaService, MetaInstanceService metaInstanceService) {
        this.metaService = metaService;
        this.metaInstanceService = metaInstanceService;
    }

    @Override
    public Optional<RuntimeData> getRuntimeData(String aippId, String version, String instanceId,
            OperationContext context) {
        Meta meta = MetaUtils.getAnyMeta(this.metaService, aippId, version, context);
        String versionId = meta.getVersionId();
        Instance instDetail = Utils.getInstanceDetail(versionId, instanceId, context, this.metaInstanceService);
        String traceId = instDetail.getInfo().get(AippConst.INST_FLOW_INST_ID_KEY);

        List<RuntimeData> dataList = this.cache.get(traceId);
        if (CollectionUtils.isEmpty(dataList)) {
            return Optional.empty();
        }
        RuntimeData end = dataList.get(dataList.size() - 1);
        RuntimeData runtimeData = new RuntimeData();
        runtimeData.setStartTime(end.getStartTime());
        runtimeData.setEndTime(end.getEndTime());
        runtimeData.setFlowDefinitionId(end.getFlowDefinitionId());
        runtimeData.setExtraParams(end.getExtraParams());
        runtimeData.setPublished(end.isPublished());
        runtimeData.setTraceId(traceId);
        runtimeData.setAippInstanceId(end.getAippInstanceId());
        runtimeData.setExecuteTime(end.getEndTime() - end.getStartTime());
        runtimeData.setNodeInfos(
                dataList.stream().flatMap(d -> d.getNodeInfos().stream()).collect(Collectors.toList()));
        return Optional.of(runtimeData);
    }

    @Override
    public void cache(RuntimeData runtimeData) {
        List<RuntimeData> dataList = this.cache.computeIfAbsent(runtimeData.getTraceId(),
                k -> new CopyOnWriteArrayList<>());
        dataList.add(runtimeData);
    }

    @Override
    public void delete(String traceId) {
        this.cache.remove(traceId);
    }
}
