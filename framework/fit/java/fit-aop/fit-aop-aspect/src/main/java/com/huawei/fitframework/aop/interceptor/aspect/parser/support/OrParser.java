/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.aop.interceptor.aspect.parser.support;

import com.huawei.fitframework.aop.interceptor.aspect.parser.model.PointcutSupportedType;

/**
 * 解析切点表达式中运算符或 '||' 的解析器。
 *
 * @author 郭龙飞 gwx900499
 * @since 2023-03-14
 */
public class OrParser extends BaseParser {
    @Override
    protected PointcutSupportedType parserType() {
        return PointcutSupportedType.OR;
    }

    @Override
    protected Result createConcreteParser(String content) {
        return new OrResult(content);
    }

    class OrResult extends BaseParser.BaseResult {
        public OrResult(String expression) {
            super(expression, null);
        }
    }
}
