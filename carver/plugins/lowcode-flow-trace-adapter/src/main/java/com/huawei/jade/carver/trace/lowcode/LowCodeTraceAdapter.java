/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.carver.trace.lowcode;

import com.huawei.fit.runtime.NodeRuntimeDataPublisher;
import com.huawei.fit.runtime.entity.NodeInfo;
import com.huawei.fit.runtime.entity.Parameter;
import com.huawei.fit.runtime.entity.RuntimeData;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.ioc.BeanContainer;
import com.huawei.fitframework.ioc.BeanFactory;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.util.LazyLoader;
import com.huawei.fitframework.util.StringUtils;
import com.huawei.jade.app.engine.eval.po.EvalReportTracePo;
import com.huawei.jade.app.engine.eval.service.EvalTaskReportTraceService;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 低代码平台应用的运行时信息采集适配器。
 *
 * @author 刘信宏
 * @since 2024-05-23
 */
@Component
public class LowCodeTraceAdapter implements NodeRuntimeDataPublisher {
    private static final Logger log = Logger.get(LowCodeTraceAdapter.class);

    private final LazyLoader<EvalTaskReportTraceService> evalServiceLoader;
    private final BeanContainer container;

    public LowCodeTraceAdapter(BeanContainer container) {
        this.container = Validation.notNull(container, "Container can not be null.");
        this.evalServiceLoader = new LazyLoader<>(() -> this.container.lookup(EvalTaskReportTraceService.class).map(
                BeanFactory::<EvalTaskReportTraceService>get).orElse(null));
    }

    @Override
    @Fitable("com.huawei.jade.carver.trace.lowcode.getExtraParamKeys")
    public List<String> getExtraParamKeys() {
        return Collections.emptyList();
    }

    @Fitable("com.huawei.jade.carver.trace.lowcode.LowCodeTraceAdapter")
    @Override
    public void onPublish(RuntimeData runtimeData) {
        if (this.evalServiceLoader.get() == null) {
            return;
        }
        Validation.notNull(runtimeData, "Runtime data can not be null.");
        String instanceId = runtimeData.getAippInstanceId();
        if (instanceId == null) {
            log.warn("Trace info ignored, instanceId not found, published {}", runtimeData.isPublished());
            return;
        }

        List<NodeInfo> nodeInfos = Validation.notNull(runtimeData.getNodeInfos(), "node infos can not be null.");
        if (nodeInfos.isEmpty()) {
            log.warn("Node infos is empty.");
            return;
        }
        List<EvalReportTracePo> reportTraces =
                nodeInfos.stream().map(node -> buildEvalReportTracePo(instanceId, node)).collect(Collectors.toList());
        this.evalServiceLoader.get().insertAllTrace(reportTraces);
    }

    private static EvalReportTracePo buildEvalReportTracePo(String instanceId, NodeInfo nodeInfo) {
        List<Parameter> parameters = Validation.notNull(nodeInfo.getParameters(), "parameters can not be null.");
        StringBuilder inputBuilder = new StringBuilder();
        StringBuilder outputBuilder = new StringBuilder();
        parameters.forEach(parameter -> {
            inputBuilder.append(Optional.ofNullable(parameter.getInput()).orElse(StringUtils.EMPTY));
            outputBuilder.append(Optional.ofNullable(parameter.getOutput()).orElse(StringUtils.EMPTY));
        });
        return EvalReportTracePo.builder()
                .instanceId(instanceId)
                .nodeId(nodeInfo.getNodeId())
                .input(inputBuilder.toString())
                .output(outputBuilder.toString())
                .time(LocalDateTime.ofInstant(Instant.ofEpochMilli(nodeInfo.getStartTime()), ZoneId.systemDefault()))
                .latency(nodeInfo.getRunCost())
                .build();
    }
}
