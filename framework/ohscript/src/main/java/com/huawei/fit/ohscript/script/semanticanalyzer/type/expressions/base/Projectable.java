/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.ohscript.script.semanticanalyzer.type.expressions.base;

import com.huawei.fit.ohscript.script.semanticanalyzer.type.expressions.SyntaxException;

/**
 * 表达式投影
 *
 * @since 1.0
 */
public interface Projectable {
    /**
     * 表达式投影
     *
     * @param origin 原始表达式
     * @param projection 投影表达式
     * @return 投影结果
     * @throws SyntaxException 语法异常
     */
    default TypeExpr project(TypeExpr origin, TypeExpr projection) throws SyntaxException {
        return projection;
    }

    /**
     * 默认表达式投影
     *
     * @param projection 投影表达式
     * @return 投影结果
     * @throws SyntaxException 语法异常
     */
    default TypeExpr project(TypeExpr projection) throws SyntaxException {
        return projection;
    }

    /**
     * 清除表达式投影
     */
    void clearProjection();
}
