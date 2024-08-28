/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.ohscript.script.semanticanalyzer.symbolentries;

import modelengine.fit.ohscript.script.parser.nodes.TerminalNode;
import modelengine.fit.ohscript.script.semanticanalyzer.Category;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.TypeExprFactory;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.concretes.ArrayTypeExpr;

/**
 * 数组条目
 *
 * @since 1.0
 */
public class ArrayEntry extends KnownSymbolEntry<ArrayTypeExpr> {
    public ArrayEntry(TerminalNode node, long scope) {
        super(node, scope, Category.VARIABLE, TypeExprFactory.createArray(node.parent()));
    }
}
