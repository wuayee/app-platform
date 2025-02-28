/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.entity;

/**
 * 流程启动返回对象
 *
 * @author yangxiangyu
 * @since 2025/2/22
 */
public class FlowStartDTO {
    /**
     * 流程实例运行标识
     */
    private String transId;

    /**
     * 启动流程后数据所属trace
     */
    private String traceId;

    public FlowStartDTO(String transId, String traceId) {
        this.transId = transId;
        this.traceId = traceId;
    }

    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }
}
