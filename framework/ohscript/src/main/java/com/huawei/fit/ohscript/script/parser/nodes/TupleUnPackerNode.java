/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.ohscript.script.parser.nodes;

import com.huawei.fit.ohscript.script.lexer.Terminal;
import com.huawei.fit.ohscript.script.parser.NonTerminal;
import modelengine.fitframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 元组解构节点
 *
 * @since 1.0
 */
public class TupleUnPackerNode extends NonTerminalNode {
    private final List<SyntaxNode> members = new ArrayList<>();

    public TupleUnPackerNode() {
        super(NonTerminal.TUPLE_UNPACKER);
    }

    @Override
    protected void polishNode(NonTerminalNode parent, List<SyntaxNode> nodes) {
        for (int i = 0; i < parent.childCount(); i++) {
            SyntaxNode n = parent.child(i);
            if (n instanceof NonTerminalNode && ((NonTerminalNode) n).isNodeIgnored()) {
                continue;
            }
            if (n.nodeType() == NonTerminal.IGNORED) {
                this.polishNode((NonTerminalNode) n, nodes);
            } else {
                nodes.add(n);
            }
        }
    }

    /**
     * 获取所有的元组解构节点
     *
     * @return 所有的元组解构节点
     */
    public List<TerminalNode> all() {
        List<TerminalNode> all = new ArrayList<>();
        for (SyntaxNode member : this.items()) {
            if (member.nodeType() == Terminal.ID) {
                all.add(ObjectUtils.cast(member));
            }
            if (member instanceof TupleUnPackerNode) {
                all.addAll((ObjectUtils.<TupleUnPackerNode>cast(member)).all());
            }
        }
        return all;
    }

    /**
     * 获取元组解构节点的所有子节点
     *
     * @return 元组解构节点的所有子节点
     */
    public List<SyntaxNode> items() {
        if (this.members.size() == 0) {
            for (SyntaxNode child : this.children()) {
                if (child.nodeType() == Terminal.LEFT_PAREN || child.nodeType() == Terminal.RIGHT_PAREN
                        || child.nodeType() == Terminal.COMMA) {
                    continue;
                }
                this.members.add(child);
            }
        }
        return this.members;
    }
}
