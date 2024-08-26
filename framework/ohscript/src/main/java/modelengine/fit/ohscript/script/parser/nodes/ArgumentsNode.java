/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.ohscript.script.parser.nodes;

import modelengine.fit.ohscript.script.lexer.Terminal;
import modelengine.fit.ohscript.script.parser.NonTerminal;

import java.util.List;

/**
 * 参数管理节点，每个节点有多个实际的参数节点
 *
 * @since 1.0
 */
public class ArgumentsNode extends NonTerminalNode {
    public ArgumentsNode() {
        super(NonTerminal.ARGUMENTS);
    }

    @Override
    public void optimizeBeta() {
        List<SyntaxNode> children = this.children();
        children.removeIf(n -> n instanceof TerminalNode && ((TerminalNode) n).nodeType() == Terminal.COMMA);
        this.refreshChildren(children);
    }

    @Override
    protected boolean isNodeIgnored() {
        if (this.childCount() == 0) {
            ArgumentNode arg = new ArgumentNode();
            this.addChild(arg);
            arg.addChild(TerminalNode.unit());
        }
        return false;
    }
}
