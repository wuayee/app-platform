/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.ohscript.script.semanticanalyzer.symbolentries;

import modelengine.fit.ohscript.script.parser.nodes.TerminalNode;
import modelengine.fit.ohscript.script.semanticanalyzer.type.expressions.concretes.ExtensionTypeExpr;

/**
 * 扩展的Entity条目
 *
 * @since 1.0
 */
public class ExtensionEntry extends EntityEntry {
    public ExtensionEntry(TerminalNode node, long scope, ExtensionTypeExpr typeExpr) {
        super(node, scope, typeExpr);
    }
}
