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
 * boolean类型表达式
 *
 * @since 1.0
 */
public class BoolTypeExpr extends SimpleTypeExpr {
    public BoolTypeExpr(SyntaxNode node) {
        super(node);
    }

    @Override
    public Type type() {
        return Type.BOOLEAN;
    }

    @Override
    public TypeExpr duplicate(TerminalNode node) {
        return new BoolTypeExpr(node);
    }
}
