/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.ohscript.script.semanticanalyzer.type.expressions.concretes;

import com.huawei.fit.ohscript.script.parser.nodes.TerminalNode;
import com.huawei.fit.ohscript.script.semanticanalyzer.Type;
import com.huawei.fit.ohscript.script.semanticanalyzer.type.expressions.base.SimpleTypeExpr;
import com.huawei.fit.ohscript.script.semanticanalyzer.type.expressions.base.TypeExpr;

/**
 * 未知类型表达式，通常用于处理错误
 *
 * @since 1.0
 */
public class UnknownTypeExpr extends SimpleTypeExpr implements VainTypeExpr {
    public UnknownTypeExpr() {
        super(null);
    }

    private UnknownTypeExpr(TerminalNode node) {
        super(node);
    }

    @Override
    public Type type() {
        return Type.UNKNOWN;
    }

    @Override
    public TypeExpr duplicate(TerminalNode node) {
        return new UnknownTypeExpr(node);
    }

    @Override
    public boolean is(TypeExpr expr) {
        return true;
    }
}
