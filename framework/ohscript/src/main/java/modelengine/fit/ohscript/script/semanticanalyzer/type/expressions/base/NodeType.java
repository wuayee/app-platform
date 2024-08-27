/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.base;

import modelengine.fit.ohscript.script.parser.nodes.SyntaxNode;

/**
 * 词法分析中的节点类型
 *
 * @since 1.0
 */
public interface NodeType {
    /**
     * 解析节点
     *
     * @return 解析后的节点
     */
    SyntaxNode parse();

    /**
     * 获取节点类型名称
     *
     * @return 节点类型名称
     */
    String name();
}
