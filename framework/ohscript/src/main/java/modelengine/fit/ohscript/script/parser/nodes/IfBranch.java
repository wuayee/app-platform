/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.ohscript.script.parser.nodes;

import modelengine.fit.ohscript.script.parser.NonTerminal;

/**
 * if分支节点
 *
 * @since 1.0
 */
public class IfBranch extends NonTerminalNode {
    /**
     * 构造函数
     */
    protected IfBranch() {
        super(NonTerminal.IF_BRANCH);
    }

    /**
     * 创建一个模拟的if分支节点
     *
     * @param condition 条件语句节点
     * @param block 代码块语句节点
     * @return 创建的模拟if分支节点
     */
    public static IfBranch mock(SyntaxNode condition, SyntaxNode block) {
        IfBranch branch = new IfBranch();
        branch.addChild(condition);
        branch.addChild(block);
        return branch;
    }
}
