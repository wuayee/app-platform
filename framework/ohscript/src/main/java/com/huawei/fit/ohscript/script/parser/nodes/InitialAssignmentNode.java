/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.ohscript.script.parser.nodes;

import com.huawei.fit.ohscript.script.lexer.Terminal;
import com.huawei.fit.ohscript.script.parser.NonTerminal;
import modelengine.fitframework.util.ObjectUtils;

/**
 * 初始化赋值节点
 * 构造形如 x = 100 的节点
 *
 * @since 1.0
 */
public class InitialAssignmentNode extends NonTerminalNode {
    /**
     * 构造一个初始化赋值节点
     * 构造形如 x = 100 的节点
     */
    public InitialAssignmentNode() {
        super(NonTerminal.INITIAL_ASSIGNMENT);
    }

    /**
     * 构造一个虚拟的初始化赋值节点
     *
     * @param left 左值节点
     * @param right 右值节点
     * @return 构造的初始化赋值节点
     */
    public static InitialAssignmentNode mock(SyntaxNode left, SyntaxNode right) {
        InitialAssignmentNode node = new InitialAssignmentNode();
        node.addChild(left);
        node.addChild(new TerminalNode(Terminal.EQUAL));
        node.addChild(right);
        return node;
    }

    @Override
    public void optimizeBeta() {
    }

    /**
     * 获取变量名
     * 如果存在，返回形如 x = 100 中的 x 的节点
     * 否则，返回 null
     *
     * @return 变量名节点，如果不存在则返回 null
     */
    public TerminalNode variable() {
        return ObjectUtils.cast(this.child(0));
    }

    /**
     * 获取赋值表达式
     * 如果存在，返回形如 x = 100 中的 100 的节点
     * 否则，返回 null
     *
     * @return 赋值表达式节点，如果不存在则返回 null
     */
    public SyntaxNode expression() {
        if (this.childCount() > 1) {
            return this.child(2);
        } else {
            return null;
        }
    }
}
