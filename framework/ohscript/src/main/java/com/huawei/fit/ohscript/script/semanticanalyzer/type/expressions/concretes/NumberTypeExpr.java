/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.ohscript.script.semanticanalyzer.type.expressions.concretes;

import com.huawei.fit.ohscript.script.parser.nodes.SyntaxNode;
import com.huawei.fit.ohscript.script.parser.nodes.TerminalNode;
import com.huawei.fit.ohscript.script.semanticanalyzer.Type;
import com.huawei.fit.ohscript.script.semanticanalyzer.type.expressions.base.SimpleTypeExpr;
import com.huawei.fit.ohscript.script.semanticanalyzer.type.expressions.base.TypeExpr;

/**
 * 数值类型表达式
 *
 * @since 1.0
 */
public class NumberTypeExpr extends SimpleTypeExpr {
    public NumberTypeExpr(SyntaxNode node) {
        super(node);
    }

    @Override
    public Type type() {
        return Type.NUMBER;
    }

    @Override
    public TypeExpr duplicate(TerminalNode node) {
        return new NumberTypeExpr(node);
    }
}
