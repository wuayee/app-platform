/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.persist.po;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 流程实例Trace持久化类
 *
 * @author 杨祥宇
 * @since 2023/8/30
 */
@Builder
@Getter
@Setter
public class FlowTracePO {
    private String traceId;

    private String streamId;

    private String operator;

    private String application;

    private String startNode;

    private String contextPool;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String status;
}
