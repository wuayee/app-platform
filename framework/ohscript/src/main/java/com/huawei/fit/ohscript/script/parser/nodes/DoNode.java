/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.ohscript.script.parser.nodes;

import com.huawei.fit.ohscript.script.parser.NonTerminal;

/**
 * do关键字节点
 *
 * @since 1.0
 */
public class DoNode extends NonTerminalNode {
    /**
     * 构造函数
     * 构造一个do关键字节点
     */
    public DoNode() {
        super(NonTerminal.DO_STATEMENT);
        this.returnAble = true;
    }

    /**
     * 获取do关键字节点的条件
     *
     * @return 返回do关键字节点的条件
     */
    public SyntaxNode condition() {
        return this.child(4);
    }

    /**
     * 获取do关键字节点的主体
     *
     * @return 返回do关键字节点的主体
     */
    public SyntaxNode body() {
        return this.child(1);
    }
}
