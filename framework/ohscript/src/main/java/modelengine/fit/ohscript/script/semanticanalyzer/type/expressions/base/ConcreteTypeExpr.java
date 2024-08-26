/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.base;

import modelengine.fit.ohscript.script.parser.nodes.SyntaxNode;

/**
 * provides base class for concrete type expressions.
 * 基本类型表达式
 *
 * @author 张群辉
 * @since 1.0
 */
public abstract class ConcreteTypeExpr extends TypeExpr {
    public ConcreteTypeExpr(SyntaxNode node) {
        super(node);
    }

    public ConcreteTypeExpr(String key, SyntaxNode node) {
        super(key, node);
    }
}
