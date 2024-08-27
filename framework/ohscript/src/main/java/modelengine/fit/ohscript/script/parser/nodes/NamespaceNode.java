/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.ohscript.script.parser.nodes;

import modelengine.fit.ohscript.script.parser.NonTerminal;

/**
 * 命名空间节点
 *
 * @since 1.0
 */
public class NamespaceNode extends NonTerminalNode {
    public NamespaceNode() {
        super(NonTerminal.NAMESPACE);
    }
}
