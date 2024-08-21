/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service.impl;

import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jane.meta.multiversion.MetaInstanceService;
import com.huawei.fit.jane.meta.multiversion.MetaService;
import com.huawei.fit.jane.meta.multiversion.definition.Meta;
import com.huawei.fit.jane.meta.multiversion.instance.Instance;
import com.huawei.fit.jober.aipp.common.exception.AippErrCode;
import com.huawei.fit.jober.aipp.common.exception.AippException;
import com.huawei.fit.jober.aipp.constants.AippConst;
import com.huawei.fit.jober.aipp.domain.AppBuilderRuntimeInfo;
import com.huawei.fit.jober.aipp.repository.AppBuilderRuntimeInfoRepository;
import com.huawei.fit.jober.aipp.service.AippFlowRuntimeInfoService;
import com.huawei.fit.jober.aipp.util.MetaInstanceUtils;
import com.huawei.fit.jober.aipp.util.MetaUtils;
import com.huawei.fit.jober.entity.consts.NodeTypes;
import com.huawei.fit.runtime.entity.NodeInfo;
import com.huawei.fit.runtime.entity.RuntimeData;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 流程运行时服务接口.
 *
 * @author 张越
 * @since 2024-05-25
 */
@Component
public class AippFlowRuntimeInfoServiceImpl implements AippFlowRuntimeInfoService {
    private final MetaService metaService;
    private final MetaInstanceService metaInstanceService;
    private final AppBuilderRuntimeInfoRepository runtimeInfoRepository;

    public AippFlowRuntimeInfoServiceImpl(MetaService metaService,
            MetaInstanceService metaInstanceService,
            AppBuilderRuntimeInfoRepository runtimeInfoRepository) {
        this.metaService = metaService;
        this.metaInstanceService = metaInstanceService;
        this.runtimeInfoRepository = runtimeInfoRepository;
    }

    @Override
    public Optional<RuntimeData> getRuntimeData(String aippId, String version, String instanceId,
            OperationContext context) {
        Meta meta = MetaUtils.getAnyMeta(this.metaService, aippId, version, context);
        if (meta == null) {
            throw new AippException(AippErrCode.APP_NOT_FOUND_WHEN_DEBUG);
        }
        String versionId = meta.getVersionId();
        Instance instDetail = MetaInstanceUtils.getInstanceDetail(
                versionId, instanceId, context, this.metaInstanceService);
        String traceId = instDetail.getInfo().get(AippConst.INST_FLOW_INST_ID_KEY);

        List<AppBuilderRuntimeInfo> runtimeInfoList = this.runtimeInfoRepository.selectByTraceId(traceId);
        if (CollectionUtils.isEmpty(runtimeInfoList)) {
            return Optional.empty();
        }

        AppBuilderRuntimeInfo start = runtimeInfoList.stream()
                .filter(r -> r.getNodeType().equals(NodeTypes.START.getType()))
                .findFirst()
                .orElseThrow(
                        () -> new IllegalStateException(StringUtils.format("No START node info in runtime info.")));
        AppBuilderRuntimeInfo end = runtimeInfoList.get(runtimeInfoList.size() - 1);
        RuntimeData runtimeData = new RuntimeData();
        runtimeData.setStartTime(start.getStartTime());
        runtimeData.setEndTime(end.getEndTime());
        runtimeData.setFlowDefinitionId(end.getFlowDefinitionId());
        runtimeData.setPublished(end.isPublished());
        runtimeData.setTraceId(traceId);
        runtimeData.setAippInstanceId(end.getInstanceId());
        runtimeData.setExecuteTime(end.getEndTime() - start.getStartTime());
        runtimeData.setNodeInfos(
                runtimeInfoList.stream().map(this::toNodeInfo).collect(Collectors.toList()));
        return Optional.of(runtimeData);
    }

    private NodeInfo toNodeInfo(AppBuilderRuntimeInfo info) {
        NodeInfo nodeInfo = new NodeInfo();
        nodeInfo.setNodeId(info.getNodeId());
        nodeInfo.setNodeType(info.getNodeType());
        nodeInfo.setStartTime(info.getStartTime());
        nodeInfo.setRunCost(info.getExecutionCost());
        nodeInfo.setErrorMsg(info.getErrorMsg());
        nodeInfo.setParameters(info.getParameters());
        nodeInfo.setStatus(info.getStatus());
        return nodeInfo;
    }
}
