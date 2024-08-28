/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.ohscript.script.parser.nodes;

import modelengine.fit.ohscript.script.parser.NonTerminal;

/**
 * 命名空间声明节点
 *
 * @since 1.0
 */
public class NamespaceDeclareNode extends NonTerminalNode {
    public NamespaceDeclareNode() {
        super(NonTerminal.NAMESPACE_DECLARE);
    }
}
