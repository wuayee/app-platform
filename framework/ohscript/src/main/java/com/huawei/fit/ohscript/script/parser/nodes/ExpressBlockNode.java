/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.ohscript.script.parser.nodes;

import com.huawei.fit.ohscript.script.lexer.Terminal;
import com.huawei.fit.ohscript.script.parser.NonTerminal;

/**
 * 表达式块节点
 *
 * @since 1.0
 */
public class ExpressBlockNode extends NonTerminalNode {
    public ExpressBlockNode() {
        super(NonTerminal.EXPRESS_BLOCK_STATEMENT);
    }

    @Override
    public void optimizeAlpha() {
        BlockNode node = new BlockNode();
        this.parent().replaceChild(this, node);
        node.addChild(new TerminalNode(Terminal.LEFT_BRACE));
        this.children().forEach(child -> node.addChild(child));
        node.addChild(new TerminalNode(Terminal.RIGHT_BRACE));
        node.parent().removeChild(node.parent().child(0));
        node.parent().removeChild(node.parent().child(1));
    }
}
