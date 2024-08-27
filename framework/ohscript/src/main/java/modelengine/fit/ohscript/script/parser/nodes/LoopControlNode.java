/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.ohscript.script.parser.nodes;

import modelengine.fit.ohscript.script.errors.SyntaxError;
import modelengine.fit.ohscript.script.parser.NonTerminal;
import modelengine.fitframework.util.ObjectUtils;

/**
 * 循环条件节点
 *
 * @since 1.0
 */
public class LoopControlNode extends NonTerminalNode {
    /**
     * 构造函数
     * 构造一个循环控制节点
     */
    public LoopControlNode() {
        super(NonTerminal.LOOP_CONTROL);
        this.returnAble = true;
    }

    @Override
    public void semanticCheck() {
        NonTerminalNode parent = ObjectUtils.cast(this.parent());
        while (!(parent instanceof ScriptNode) && !(parent instanceof FunctionDeclareNode)) {
            if (parent.nodeType.loopAble()) {
                return;
            }
            parent = ObjectUtils.cast(parent.parent());
        }
        this.panic(SyntaxError.LOOP_CONTROL_OUT_OF_LOOP);
    }
}
