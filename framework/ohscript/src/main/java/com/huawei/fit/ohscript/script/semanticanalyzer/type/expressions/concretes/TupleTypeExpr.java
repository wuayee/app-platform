/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.ohscript.script.semanticanalyzer.type.expressions.concretes;

import com.huawei.fit.ohscript.script.parser.nodes.TupleDeclareNode;
import com.huawei.fit.ohscript.script.semanticanalyzer.type.expressions.base.TypeExpr;

import java.util.Map;

/**
 * 元组类型表达式
 *
 * @since 1.0
 */
public class TupleTypeExpr extends EntityTypeExpr {
    /**
     * 构造函数
     *
     * @param entity 元组声明节点
     * @param members 元组成员类型表达式映射
     * 构造元组类型表达式实例，包含元组声明节点和元组成员类型表达式映射
     */
    public TupleTypeExpr(TupleDeclareNode entity, Map<String, TypeExpr> members) {
        super(entity, members);
    }
}
