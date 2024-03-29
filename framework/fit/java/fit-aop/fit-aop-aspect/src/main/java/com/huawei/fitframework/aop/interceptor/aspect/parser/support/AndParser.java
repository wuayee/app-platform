/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.aop.interceptor.aspect.parser.support;

import com.huawei.fitframework.aop.interceptor.aspect.parser.model.PointcutSupportedType;

/**
 * 解析切点表达式中运算符与 && 的解析器。
 *
 * @author 郭龙飞 gwx900499
 * @since 2023-03-14
 */
public class AndParser extends BaseParser {
    @Override
    protected PointcutSupportedType parserType() {
        return PointcutSupportedType.AND;
    }

    @Override
    protected Result createConcreteParser(String content) {
        return new AndResult(content);
    }

    class AndResult extends BaseParser.BaseResult {
        public AndResult(String expression) {
            super(expression, null);
        }
    }
}
