/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.ohscript.script.semanticanalyzer.type.expressions.assists;

import com.huawei.fit.ohscript.script.parser.nodes.SyntaxNode;

/**
 * constraints just for generic type expression
 * add supposed to be which is predefined in function declaration
 * huizi 2023
 *
 * @since 1.0
 */
public class GenericConstraints extends Constraints {
    public GenericConstraints(SyntaxNode node) {
        super(node);
    }
}
