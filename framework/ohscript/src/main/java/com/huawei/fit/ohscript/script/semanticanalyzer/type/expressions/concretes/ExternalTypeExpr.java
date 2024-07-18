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
 * 外部类型表达式
 *
 * @since 1.0
 */
public class ExternalTypeExpr extends SimpleTypeExpr {
    public ExternalTypeExpr(SyntaxNode node) {
        super(node);
    }

    @Override
    public boolean is(TypeExpr expr) {
        return expr instanceof ExternalTypeExpr;
    }

    @Override
    public TypeExpr duplicate(TerminalNode node) {
        return new ExternalTypeExpr(node);
    }

    @Override
    public Type type() {
        return Type.EXTERNAL;
    }
}
