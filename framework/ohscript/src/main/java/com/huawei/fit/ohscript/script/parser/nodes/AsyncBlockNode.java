/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.ohscript.script.parser.nodes;

import com.huawei.fit.ohscript.script.parser.NonTerminal;
import com.huawei.fit.ohscript.script.semanticanalyzer.type.expressions.base.TypeExpr;
import com.huawei.fitframework.util.ObjectUtils;

/**
 * 异步调用块节点
 *
 * @since 1.0
 */
public class AsyncBlockNode extends NonTerminalNode {
    private static TypeExpr asyncTypeExpr = null;

    private BlockNode block;

    /**
     * 构造函数
     * 初始化一个新的异步调用块节点
     */
    public AsyncBlockNode() {
        super(NonTerminal.ASYNC_BLOCK);
    }

    @Override
    public void optimizeGama() {
        super.optimizeGama();
        this.block = ObjectUtils.cast(this.child(1));
    }

    /**
     * 获取异步调用块节点
     *
     * @return 块节点
     */
    public BlockNode block() {
        return this.block;
    }

    /**
     * 获取异步调用块节点的类型表达式
     *
     * @return 类型表达式
     */
    public TypeExpr typeExpr() {
        return asyncTypeExpr;
    }

    /**
     * 初始化异异步调用块节点的类型表达式
     *
     * @param typeExpr 类型表达式
     */
    public void initTypeExpr(TypeExpr typeExpr) {
        asyncTypeExpr = typeExpr;
    }
}
