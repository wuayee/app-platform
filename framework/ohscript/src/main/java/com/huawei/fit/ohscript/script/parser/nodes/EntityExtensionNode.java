/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.ohscript.script.parser.nodes;

import com.huawei.fit.ohscript.script.parser.NonTerminal;

/**
 * 扩展Entity节点
 *
 * @since 1.0
 */
public class EntityExtensionNode extends EntityDeclareNode {
    private SyntaxNode host;

    /**
     * 构造函数
     * 初始化EntityExtensionNode对象，设置非终端符为ENTITY_EXTENSION
     */
    public EntityExtensionNode() {
        super(NonTerminal.ENTITY_EXTENSION);
    }

    @Override
    public void optimizeBeta() {
        super.optimizeBeta();
    }

    @Override
    public void optimizeDelta() {
        this.host = this.removeAt(0);
        this.host.optimizeDelta();
        this.addChild(host);
    }

    /**
     * 获取宿主
     *
     * @return 返回宿主
     */
    public SyntaxNode host() {
        return this.host;
    }
}
