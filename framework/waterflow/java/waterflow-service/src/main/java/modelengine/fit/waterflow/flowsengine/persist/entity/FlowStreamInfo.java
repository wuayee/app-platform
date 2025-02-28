/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.persist.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * streamId对应结构
 *
 * @author 杨祥宇
 * @since 2024/4/15
 */
@Getter
@Setter
public class FlowStreamInfo {
    /**
     * 流程对应metaId
     */
    private final String metaId;

    /**
     * 流程对应版本
     */
    private final String version;

    public FlowStreamInfo(String metaId, String version) {
        this.metaId = metaId;
        this.version = version;
    }
}
