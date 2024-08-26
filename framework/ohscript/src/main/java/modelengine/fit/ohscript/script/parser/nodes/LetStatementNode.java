/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.ohscript.script.parser.nodes;

import modelengine.fit.ohscript.script.parser.NonTerminal;

/**
 * let语句节点
 * let语句定义的是场景
 *
 * @since 1.0
 */
public class LetStatementNode extends VarStatementNode {
    /**
     * 构造函数
     * 初始化节点类型为LET_STATEMENT
     */
    public LetStatementNode() {
        super();
        this.nodeType = NonTerminal.LET_STATEMENT;
    }

    @Override
    public boolean mutable() {
        return false;
    }
}
