/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.ohscript.script.parser.nodes;

import com.huawei.fit.ohscript.script.parser.NonTerminal;
import modelengine.fitframework.util.ObjectUtils;

/**
 * safe块节点
 *
 * @since 1.0
 */
public class SafeBlockNode extends NonTerminalNode {
    private BlockNode block;

    public SafeBlockNode() {
        super(NonTerminal.SAFE_BLOCK);
    }

    @Override
    public void optimizeGama() {
        super.optimizeGama();
        this.block = ObjectUtils.cast(this.child(1));
    }

    /**
     * 获取safe块节点
     *
     * @return 返回块节点
     */
    public BlockNode block() {
        return this.block;
    }
}
