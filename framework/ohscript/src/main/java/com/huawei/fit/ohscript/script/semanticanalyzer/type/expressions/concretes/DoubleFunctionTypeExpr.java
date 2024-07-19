/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.ohscript.script.semanticanalyzer.type.expressions.concretes;

import com.huawei.fit.ohscript.script.parser.nodes.DoubleFunctionDeclareNode;
import com.huawei.fit.ohscript.script.parser.nodes.FunctionCallNode;
import com.huawei.fit.ohscript.script.semanticanalyzer.type.expressions.base.TypeExpr;

/**
 * 双重方法调用类型
 *
 * @since 1.0
 */
public abstract class DoubleFunctionTypeExpr extends FunctionTypeExpr {
    /**
     * 构造函数
     *
     * @param function 双重方法声明节点
     * @param argumentType 参数类型
     * @param returnType 返回类型
     */
    public DoubleFunctionTypeExpr(DoubleFunctionDeclareNode function, TypeExpr argumentType, TypeExpr returnType) {
        super(function, argumentType, returnType);
    }

    /**
     * 投影函数
     *
     * @param function 函数调用节点
     * @return 返回函数类型
     */
    public abstract FunctionTypeExpr project(FunctionCallNode function);
}
