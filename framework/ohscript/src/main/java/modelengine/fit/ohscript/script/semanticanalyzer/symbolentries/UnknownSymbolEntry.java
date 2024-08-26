/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.ohscript.script.semanticanalyzer.symbolentries;

import modelengine.fit.ohscript.script.parser.nodes.TerminalNode;
import modelengine.fit.ohscript.script.semanticanalyzer.Category;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.TypeExprFactory;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.base.TypeExpr;

/**
 * 未知的条目，用于处理条目异常的情况
 *
 * @since 1.0
 */
public class UnknownSymbolEntry extends SymbolEntry {
    public UnknownSymbolEntry(TerminalNode node) {
        super(node, Category.VARIABLE);
    }

    @Override
    public long id() {
        return -1;
    }

    @Override
    public TypeExpr typeExpr() {
        return TypeExprFactory.createUnknown();
    }

    @Override
    public long scope() {
        return this.node().scope();
    }
}
