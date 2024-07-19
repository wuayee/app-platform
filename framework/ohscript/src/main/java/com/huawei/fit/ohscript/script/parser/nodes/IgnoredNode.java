/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.ohscript.script.parser.nodes;

import com.huawei.fit.ohscript.script.parser.NonTerminal;

/**
 * 忽略的节点
 *
 * @since 1.0
 */
public class IgnoredNode extends NonTerminalNode {
    private String name;

    /**
     * 构造函数
     * 构造一个忽略的节点
     */
    public IgnoredNode() {
        super(NonTerminal.IGNORED);
    }

    /**
     * 设置节点名称
     *
     * @param name 节点名称
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String name() {
        return this.name;
    }
}
