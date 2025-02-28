/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.context;

import lombok.Getter;

/**
 * 每次offer后返回的标识
 *
 * @author 宋永坦
 * @since 2024/2/18
 */
@Getter
public class FlowOfferId {
    private FlowTrans trans;

    private String traceId;

    public FlowOfferId(FlowTrans trans, String traceId) {
        this.trans = trans;
        this.traceId = traceId;
    }
}
