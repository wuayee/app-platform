/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.ohscript.script.parser.nodes;

import com.huawei.fit.ohscript.script.parser.NonTerminal;
import com.huawei.fitframework.util.ObjectUtils;

/**
 * 系统扩展节点
 *
 * @since 1.0
 */
public class SystemExtensionNode extends NonTerminalNode {
    private TerminalNode name;

    public SystemExtensionNode() {
        super(NonTerminal.SYSTEM_EXTENSION);
    }

    @Override
    public void optimizeBeta() {
        super.optimizeBeta();
    }

    @Override
    public void optimizeGama() {
        if (this.name != null) {
            return;
        }
        this.name = ObjectUtils.cast(this.child(0));
        this.name.scope = this.scope();
        for (InitialAssignmentNode member : this.members) {
            TerminalNode node = ObjectUtils.cast(member.child(0));
            MemberNode newName = new MemberNode(node);
            member.replaceChild(node, newName);
        }
        super.optimizeGama();
    }

    @Override
    public TerminalNode declaredName() {
        return this.name;
    }
}
