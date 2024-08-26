/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.concretes;

import modelengine.fit.ohscript.script.parser.nodes.TerminalNode;
import modelengine.fit.ohscript.script.semanticanalyzer.Type;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.base.SimpleTypeExpr;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.base.TypeExpr;

/**
 * null类型表达式
 *
 * @since 1.0
 */
public class NullTypeExpr extends SimpleTypeExpr {
    public NullTypeExpr() {
        super(null);
    }

    private NullTypeExpr(TerminalNode node) {
        super(node);
    }

    @Override
    public boolean is(TypeExpr expr) {
        return true;
    }

    @Override
    public TypeExpr duplicate(TerminalNode node) {
        return new NullTypeExpr(node);
    }

    @Override
    public Type type() {
        return Type.NULL;
    }
}
