/*
 *  Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jane.flow.graph.entity.elsa.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 保存流程定义response
 *
 * @author y00679285
 * @since 2023/10/13
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaveFlowsResponse {
    private int code;

    private FlowDataInfo data;

    private String msg;

    /**
     * FlowDataInfo
     */
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FlowDataInfo {
        private String updateTime;
    }
}
