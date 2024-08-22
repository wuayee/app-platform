/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.concretes;

import modelengine.fit.ohscript.script.parser.nodes.TerminalNode;
import modelengine.fit.ohscript.script.semanticanalyzer.Type;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.base.SimpleTypeExpr;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.base.TypeExpr;

/**
 * UNIT类型表达式
 *
 * @since 1.0
 */
public class UnitTypeExpr extends SimpleTypeExpr {
    public UnitTypeExpr() {
        super(null);
    }

    private UnitTypeExpr(TerminalNode node) {
        super(node);
    }

    @Override
    public Type type() {
        return Type.UNIT;
    }

    @Override
    public TypeExpr duplicate(TerminalNode node) {
        return new UnitTypeExpr(node);
    }
}
