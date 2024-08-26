/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.waterflow.edatamate.service;

import modelengine.fit.waterflow.flowsengine.domain.flows.InterStream;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowData;

import lombok.Getter;

/**
 * 流程中节点接收外部数据的连接点
 *
 * @author 宋永坦
 * @since 2024/2/19
 */
@Getter
public class FlowAppendPoint {
    private String flowMetaId;

    private String flowVersion;

    private String nodeMetaId;

    private InterStream<FlowData> publisher = new AppendStream();

    public FlowAppendPoint(String flowMetaId, String flowVersion, String nodeMetaId) {
        this.flowMetaId = flowMetaId;
        this.flowVersion = flowVersion;
        this.nodeMetaId = nodeMetaId;
    }

    /**
     * getFlowId
     *
     * @return flowId
     */
    public String getFlowId() {
        return getFlowId(this.flowMetaId, this.flowVersion);
    }

    /**
     * getFlowId
     *
     * @param flowMetaId flowMetaId
     * @param flowVersion flowVersion
     * @return flowId
     */
    public static String getFlowId(String flowMetaId, String flowVersion) {
        return flowMetaId + '-' + flowVersion;
    }
}
