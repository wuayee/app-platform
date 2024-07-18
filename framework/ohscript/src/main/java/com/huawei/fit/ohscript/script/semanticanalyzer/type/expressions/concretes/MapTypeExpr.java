/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.ohscript.script.semanticanalyzer.type.expressions.concretes;

import com.huawei.fit.ohscript.script.parser.nodes.SyntaxNode;
import com.huawei.fit.ohscript.script.parser.nodes.TerminalNode;
import com.huawei.fit.ohscript.script.semanticanalyzer.Type;
import com.huawei.fit.ohscript.script.semanticanalyzer.type.expressions.base.ComplexTypeExpr;
import com.huawei.fit.ohscript.script.semanticanalyzer.type.expressions.base.TypeExpr;

/**
 * Map类型表达式
 *
 * @since 1.0
 */
public class MapTypeExpr extends ComplexTypeExpr {
    public MapTypeExpr(SyntaxNode node) {
        super(node);
    }

    @Override
    public Type type() {
        return Type.MAP;
    }

    @Override
    public boolean is(TypeExpr expr) {
        if (super.is(expr)) {
            return true;
        }
        return expr instanceof MapTypeExpr; // maybe not that simple
    }

    @Override
    public TypeExpr duplicate(TerminalNode node) {
        return new MapTypeExpr(node);
    }
}
