/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.ohscript.script.parser.nodes;

import com.huawei.fit.ohscript.script.lexer.Terminal;
import com.huawei.fit.ohscript.script.parser.NonTerminal;
import com.huawei.fit.ohscript.script.semanticanalyzer.type.expressions.TypeExprFactory;
import com.huawei.fit.ohscript.script.semanticanalyzer.type.expressions.base.TypeExpr;
import com.huawei.fitframework.util.ObjectUtils;

/**
 * 参数节点
 *
 * @since 1.0
 */
public class ArgumentNode extends NonTerminalNode {
    /**
     * 构造函数
     * 构造一个参数节点
     */
    public ArgumentNode() {
        super(NonTerminal.ARGUMENT);
    }

    @Override
    public void optimizeBeta() {
    }

    /**
     * 获取参数节点
     *
     * @return 参数节点
     */
    public TerminalNode argument() {
        return ObjectUtils.cast(this.child(0));
    }

    @Override
    public TypeExpr typeExpr() {
        if (this.argument().nodeType() == Terminal.UNIT) {
            return TypeExprFactory.createUnit();
        }
        return this.argument().symbolEntry().typeExpr();
    }
}
