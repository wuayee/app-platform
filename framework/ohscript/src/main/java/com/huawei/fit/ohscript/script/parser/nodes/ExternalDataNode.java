/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.ohscript.script.parser.nodes;

import com.huawei.fit.ohscript.script.parser.NonTerminal;

/**
 * external的对象数据节点
 *
 * @since 1.0
 */
public class ExternalDataNode extends NonTerminalNode {
    private final Object data;

    /**
     * 构造函数
     *
     * @param data 需要持有的external对象
     */
    public ExternalDataNode(Object data) {
        super(NonTerminal.EXTERNAL_DATA);
        this.data = data;
    }

    /**
     * 获取数据对象
     *
     * @return 持有的external对象
     */
    public Object getData() {
        return this.data;
    }

    @Override
    protected boolean isNodeIgnored() {
        return false;
    }
}
