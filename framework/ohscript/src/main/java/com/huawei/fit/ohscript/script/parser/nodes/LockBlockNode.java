/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.ohscript.script.parser.nodes;

import com.huawei.fit.ohscript.script.parser.NonTerminal;

/**
 * lock代码块节点
 *
 * @since 1.0
 */
public class LockBlockNode extends NonTerminalNode {
    public LockBlockNode() {
        super(NonTerminal.LOCK_BLOCK);
    }
}
