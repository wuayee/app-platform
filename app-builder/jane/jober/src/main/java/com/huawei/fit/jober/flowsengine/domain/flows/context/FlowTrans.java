/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.flowsengine.domain.flows.context;

import com.huawei.fit.jober.flowsengine.domain.flows.streams.IdGenerator;

/**
 * 流程实例运行标识
 * offer数据后该流程生成的context的transId唯一
 *
 * @author y00679285
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
