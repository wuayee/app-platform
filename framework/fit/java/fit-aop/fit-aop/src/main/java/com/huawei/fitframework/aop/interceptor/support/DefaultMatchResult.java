/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fitframework.aop.interceptor.support;

import com.huawei.fitframework.aop.interceptor.MethodMatcher;

/**
 * 表示默认的匹配结果。
 *
 * @author 季聿阶 j00559309
 * @since 2022-05-28
 */
public class DefaultMatchResult implements MethodMatcher.MatchResult {
    private final boolean isMatch;

    /**
     * 仅使用匹配结果来实例化 {@link DefaultMatchResult}。
     *
     * @param isMatch 表示匹配结果的 {@code boolean}。
     */
    public DefaultMatchResult(boolean isMatch) {
        this.isMatch = isMatch;
    }

    @Override
    public boolean matches() {
        return this.isMatch;
    }

    @Override
    public Object getResult() {
        return null;
    }
}
