/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.ohscript.script.parser.nodes;

/**
 * 成员节点
 *
 * @since 1.0
 */
public class MemberNode extends TerminalNode {
    private final TerminalNode origin;

    /**
     * 构造函数
     *
     * @param origin 原始节点
     */
    public MemberNode(TerminalNode origin) {
        super(origin.nodeType());
        this.origin = origin;
    }

    @Override
    public boolean isPrivate() {
        return this.origin.isPrivate();
    }

    @Override
    public String lexeme() {
        return "." + this.origin.lexeme();
    }
}
