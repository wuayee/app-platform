/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.ohscript.script.parser.nodes;

import com.huawei.fit.ohscript.script.lexer.Terminal;
import com.huawei.fit.ohscript.script.parser.NonTerminal;

/**
 * Map的声明节点
 *
 * @since 1.0
 */
public class MapDeclareNode extends ArrayDeclareNode {
    public MapDeclareNode() {
        super(NonTerminal.MAP_DECLARE);
    }

    @Override
    public boolean isMeta() {
        return this.name.nodeType() == Terminal.MAP_TYPE;
    }
}
