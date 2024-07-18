/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.ohscript.script.parser.nodes;

import com.huawei.fit.ohscript.script.parser.NonTerminal;

/**
 * java静态调用节点
 *
 * @since 1.0
 */
public class JavaStaticCallNode extends NonTerminalNode {
    public JavaStaticCallNode() {
        super(NonTerminal.JAVA_STATIC_CALL);
    }

    @Override
    public void optimizeBeta() {
        super.optimizeBeta();
        this.child(1).addChild(this.child(0), 0);
        this.parent().replaceChild(this, this.child(1));
    }
}
