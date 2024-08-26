/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.waterflow.edatamate.client.elsa.reponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 保存流程定义response
 *
 * @author 杨祥宇
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
     * flowData内部结构体
     *
     * @author 杨祥宇
     * @since 2023/10/13
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
