/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.ohscript.script.parser.nodes;

import modelengine.fit.ohscript.script.lexer.Terminal;
import modelengine.fit.ohscript.script.parser.NonTerminal;

/**
 * 数组或者Map的声明节点
 *
 * @since 1.0
 */
public class ArrayOrMapDeclareNode extends NonTerminalNode {
    public ArrayOrMapDeclareNode() {
        super(NonTerminal.ARRAY_MAP_DECLARE);
    }

    @Override
    public void optimizeGama() {
        super.optimizeGama();
        SyntaxNode owner = null;
        if (this.children().stream().anyMatch(c -> c.nodeType() == Terminal.STRING_COLON)) {
            owner = new MapDeclareNode();
        } else {
            owner = new ArrayDeclareNode();
        }
        for (SyntaxNode child : this.children()) {
            if (child.nodeType() != Terminal.LEFT_BRACKET && child.nodeType() != Terminal.RIGHT_BRACKET
                    && child.nodeType() != Terminal.COMMA) {
                owner.addChild(child);
            }
        }
        owner.optimizeGama();
        this.parent().replaceChild(this, owner);
    }
}
