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
 * undefined 类似javaScript中Undefined，用于处理属性未找到的情况
 *
 * @since 1.0
 */
public class UndefinedTypeExpr extends SimpleTypeExpr {
    public UndefinedTypeExpr() {
        super(null);
    }

    public UndefinedTypeExpr(SyntaxNode node) {
        super(node);
    }

    @Override
    public boolean is(TypeExpr expr) {
        return true;
    }

    @Override
    public TypeExpr duplicate(TerminalNode node) {
        return new UndefinedTypeExpr(node);
    }

    @Override
    public Type type() {
        return Type.UNDEFINED;
    }
}
