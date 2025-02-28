/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.entity;

import java.util.List;

/**
 * 流程结束后回调所需信息
 *
 * @author 杨祥宇
 * @since 2024/2/28
 */
public class FlowTransCompletionInfo {
    /**
     * 流程定义meta id
     */
    private String flowMetaId;

    /**
     * 流程版本
     */
    private String flowVersion;

    /**
     * 流程transId
     */
    private String flowTransId;

    /**
     * 流程trace id列表
     */
    private List<String> flowTraceIds;

    /**
     * 流程trans的状态
     * READY --未执行；RUNNING --执行中；ARCHIVED -- 执行完成
     * ERROR --执行失败；PARTIAL_ERROR --部分失败；TERMINATE --已终止
     */
    private String status;

    public FlowTransCompletionInfo() {

    }

    public FlowTransCompletionInfo(String flowMetaId, String flowVersion, String flowTransId, List<String> flowTraceIds,
                                   String status) {
        this.flowMetaId = flowMetaId;
        this.flowVersion = flowVersion;
        this.flowTransId = flowTransId;
        this.flowTraceIds = flowTraceIds;
        this.status = status;
    }

    public String getFlowMetaId() {
        return flowMetaId;
    }

    public void setFlowMetaId(String flowMetaId) {
        this.flowMetaId = flowMetaId;
    }

    public String getFlowVersion() {
        return flowVersion;
    }

    public void setFlowVersion(String flowVersion) {
        this.flowVersion = flowVersion;
    }

    public String getFlowTransId() {
        return flowTransId;
    }

    public void setFlowTransId(String flowTransId) {
        this.flowTransId = flowTransId;
    }

    public List<String> getFlowTraceIds() {
        return flowTraceIds;
    }

    public void setFlowTraceIds(List<String> flowTraceIds) {
        this.flowTraceIds = flowTraceIds;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}