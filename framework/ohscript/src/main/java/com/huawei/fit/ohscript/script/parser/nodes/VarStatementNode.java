/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.ohscript.script.parser.nodes;

import com.huawei.fit.ohscript.script.parser.NonTerminal;

import java.util.stream.Collectors;

/**
 * var语句节点
 * var语句定义的是变量
 *
 * @since 1.0
 */
public class VarStatementNode extends NonTerminalNode {
    public VarStatementNode() {
        super(NonTerminal.VAR_STATEMENT);
    }

    public boolean mutable() {
        return true;
    }

    @Override
    public void optimizeBeta() {
        this.removeChild(this.child(0)); // remove var/let keyword
        if (this.child(0) instanceof GeneralNode) {
            this.refreshChildren(this.child(0)
                    .children()
                    .stream()
                    .filter(c -> c instanceof NonTerminalNode)
                    .collect(Collectors.toList()));
        }
    }
}
