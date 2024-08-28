/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fitframework.aop.interceptor.aspect.parser.support;

import modelengine.fitframework.aop.interceptor.aspect.parser.model.PointcutSupportedType;

/**
 * 解析切点表达式中运算符非 '!' 的解析器。
 *
 * @author 郭龙飞
 * @since 2023-03-14
 */
public class NotParser extends BaseParser {
    @Override
    protected PointcutSupportedType parserType() {
        return PointcutSupportedType.NOT;
    }

    @Override
    protected Result createConcreteParser(String content) {
        return new NotResult(content);
    }

    class NotResult extends BaseParser.BaseResult {
        public NotResult(String expression) {
            super(expression, null);
        }
    }
}
