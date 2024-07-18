/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.ohscript.script.parser.nodes;

import com.huawei.fit.ohscript.script.lexer.Terminal;
import com.huawei.fit.ohscript.script.lexer.Token;
import com.huawei.fit.ohscript.script.parser.NonTerminal;
import com.huawei.fit.ohscript.util.OhFrom;
import com.huawei.fitframework.util.ObjectUtils;

/**
 * oh对象调用节点
 *
 * @since 1.0
 */
public class OhCallNode extends NonTerminalNode {
    private OhFrom from;

    private TerminalNode source;

    public OhCallNode() {
        super(NonTerminal.OH_CALL);
    }

    @Override
    public void optimizeBeta() {
        if (this.child(0).nodeType() == Terminal.OH) {
            this.from = OhFrom.valueFrom(this.removeAt(0).lexeme());
            this.source = (ObjectUtils.cast(this.child(0).child(0)));
            Token old = this.source.token();
            this.source.setToken(
                    new Token(old.tokenType(), this.from.ohName() + old.lexeme(), old.line(), old.start(), old.end()));
        }
        super.optimizeBeta();
    }
}
