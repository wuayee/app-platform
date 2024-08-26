/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.ohscript.script.semanticanalyzer;

import modelengine.fit.ohscript.script.parser.nodes.SyntaxNode;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.base.TypeExpr;

/**
 * 具有语义的节点
 *
 * @since 1.0
 */
public interface SemanticAble {
    /**
     * 符号化
     *
     * @param node 节点
     */
    void symbolize(SyntaxNode node);

    /**
     * 类型推导
     *
     * @param node 节点
     * @return 推导出的类型
     */
    TypeExpr typeInfer(SyntaxNode node);
}
