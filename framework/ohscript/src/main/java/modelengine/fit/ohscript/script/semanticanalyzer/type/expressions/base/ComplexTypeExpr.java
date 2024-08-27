/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.base;

import modelengine.fit.ohscript.script.parser.nodes.SyntaxNode;

/**
 * 复杂类型表达式
 *
 * @since 1.0
 */
public abstract class ComplexTypeExpr extends ConcreteTypeExpr {
    public ComplexTypeExpr(SyntaxNode node) {
        super(node);
    }
}
