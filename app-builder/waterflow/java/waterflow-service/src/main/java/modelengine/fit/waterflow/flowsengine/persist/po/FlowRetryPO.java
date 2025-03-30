/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.persist.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * flow retry持久化类
 *
 * @author 李哲峰
 * @since 2024/1/31
 */
@AllArgsConstructor
@Builder
@Getter
@Setter
public class FlowRetryPO {
    private String entityId;

    private String entityType;

    private LocalDateTime nextRetryTime;

    private LocalDateTime lastRetryTime;

    private int retryCount;

    private int version;
}
