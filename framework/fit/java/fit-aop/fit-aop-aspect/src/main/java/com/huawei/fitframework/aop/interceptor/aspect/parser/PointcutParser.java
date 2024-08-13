/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.aop.interceptor.aspect.parser;

import java.util.List;

/**
 * 表达式解析器。
 *
 * @author 郭龙飞
 * @since 2023-03-10
 */
public interface PointcutParser {
    /**
     * 解析入口。
     *
     * @return 解析结果的 {@link ExpressionParser.Result}。
     */
    List<ExpressionParser.Result> parse();
}
