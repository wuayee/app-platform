/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.ohscript.script.parser.nodes;

import modelengine.fit.ohscript.script.parser.NonTerminal;

/**
 * 匹配变量的节点
 *
 * @since 1.0
 */
public class MatchVarNode extends NonTerminalNode {
    public MatchVarNode() {
        super(NonTerminal.MATCH_VAR);
    }

    @Override
    public void optimizeBeta() {
    }
}
