/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.persist.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * flow context持久化类
 *
 * @author 杨祥宇
 * @since 2023/8/22
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FlowContextPO {
    private String contextId;

    private String traceId;

    private String transId;

    private String rootId;

    private String streamId;

    private String flowData;

    private String positionId;

    private boolean joined;

    private String status;

    private String parallel;

    private String parallelMode;

    private String previous;

    private String batchId;

    private String toBatch;

    private boolean sent;

    private LocalDateTime createAt;

    private LocalDateTime updateAt;

    private LocalDateTime archivedAt;
}
