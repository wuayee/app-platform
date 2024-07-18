/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.ohscript.script.parser.nodes;

import com.huawei.fit.ohscript.script.parser.NonTerminal;

/**
 * 通用节点
 *
 * @since 1.0
 */
public class GeneralNode extends NonTerminalNode {
    /**
     * 构造函数
     *
     * @param type 非终端节点类型
     */
    public GeneralNode(NonTerminal type) {
        super(type);
    }
}
