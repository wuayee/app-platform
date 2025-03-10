/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.context;

import modelengine.fit.waterflow.flowsengine.domain.flows.streams.IdGenerator;

/**
 * 流程实例运行标识
 * offer数据后该流程生成的context的transId唯一
 *
 * @author 杨祥宇
 * @since 2023/8/29
 */
public class FlowTrans extends IdGenerator {
    /**
     * FlowTrans
     */
    public FlowTrans() {
    }

    public FlowTrans(String id) {
        super(id);
    }

    /**
     * equals
     *
     * @param trans trans
     * @return boolean
     */
    public boolean equals(FlowTrans trans) {
        return this.id.equals(trans.id);
    }
}
