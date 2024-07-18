/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.ohscript.script.parser.nodes;

import com.huawei.fit.ohscript.script.parser.NonTerminal;

/**
 * 三元运算符节点
 *
 * @since 1.0
 */
public class TernaryExpressionNode extends NonTerminalNode {
    public TernaryExpressionNode() {
        super(NonTerminal.TERNARY_EXPRESSION);
    }

    @Override
    public void optimizeBeta() {
        SyntaxNode parent = this.parent();
        SyntaxNode condition = parent.child(parent.children().indexOf(this) - 1);
        parent.removeChild(condition);
        this.addChild(condition, 0);
        if (parent.nodeType() == NonTerminal.EXPRESSION) { // simplify the structure
            parent.parent().replaceChild(parent, this);
        }
    }
}
