/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.ohscript.script.parser.nodes;

import com.huawei.fit.ohscript.script.lexer.Terminal;

/**
 * end关键字节点
 *
 * @since 1.0
 */
public class EndNode extends TerminalNode {
    /**
     * 构造函数
     * 构造一个EndNode对象，该对象代表一个end关键字
     */
    public EndNode() {
        super(Terminal.END);
    }
}
