/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.aop.interceptor.aspect.parser.support;

import com.huawei.fitframework.aop.interceptor.aspect.parser.model.PointcutSupportedType;

/**
 * 解析切点表达式中运算符 '(' 的解析器。用于 pointcut 解析时自动添加，解决多个 pointcut 运算优先级问题。
 *
 * @author 白鹏坤
 * @since 2023-03-31
 */
public class LeftBracketParser extends BaseParser {
    @Override
    protected PointcutSupportedType parserType() {
        return PointcutSupportedType.LEFT_BRACKET;
    }

    @Override
    public Result createConcreteParser(String content) {
        return new LeftBracketResult(content);
    }

    /**
     * 左括号解析结果类。
     */
    public class LeftBracketResult extends BaseParser.BaseResult {
        public LeftBracketResult(String expression) {
            super(expression, null);
        }
    }

    /**
     * 创建解析结果实例。
     *
     * @return 解析结果实例的 {@link Result}。
     */
    public static Result getResult() {
        return new LeftBracketParser().createConcreteParser(PointcutSupportedType.LEFT_BRACKET.getValue());
    }
}
