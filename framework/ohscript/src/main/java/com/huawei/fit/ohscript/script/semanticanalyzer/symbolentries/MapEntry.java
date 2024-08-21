/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.ohscript.script.semanticanalyzer.symbolentries;

import com.huawei.fit.ohscript.script.parser.nodes.TerminalNode;
import com.huawei.fit.ohscript.script.semanticanalyzer.Category;
import com.huawei.fit.ohscript.script.semanticanalyzer.type.expressions.TypeExprFactory;
import modelengine.fitframework.util.ObjectUtils;

/**
 * Map条目
 *
 * @since 1.0
 */
public class MapEntry extends KnownSymbolEntry {
    /**
     * 构造函数
     *
     * @param node 术语节点
     * @param scope 作用域
     */
    public MapEntry(TerminalNode node, long scope) {
        super(node, scope, Category.VARIABLE, TypeExprFactory.createMap(ObjectUtils.cast(node.parent())));
    }
}
