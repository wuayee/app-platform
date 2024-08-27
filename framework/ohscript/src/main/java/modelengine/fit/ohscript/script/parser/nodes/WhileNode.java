/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.ohscript.script.parser.nodes;

import modelengine.fit.ohscript.script.parser.NonTerminal;

/**
 * while节点
 *
 * @since 1.0
 */
public class WhileNode extends NonTerminalNode {
    /**
     * 构造函数
     */
    public WhileNode() {
        super(NonTerminal.WHILE_STATEMENT);
        this.returnAble = true;
    }

    /**
     * 获取条件语句
     *
     * @return 条件语句
     */
    public SyntaxNode condition() {
        return this.child(2);
    }

    /**
     * 获取循环体
     *
     * @return 循环体
     */
    public SyntaxNode body() {
        return this.child(4);
    }
}
