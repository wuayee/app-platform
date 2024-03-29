/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2024. All rights reserved.
 */

package com.huawei.fitframework.util.wildcard.support;

import com.huawei.fitframework.util.wildcard.Pattern;
import com.huawei.fitframework.util.wildcard.Result;

/**
 * 为 {@link Result} 提供用以匹配结果的实现。
 *
 * @param <T> 表示匹配元素的类型。
 * @author 梁济时 l00815032
 * @since 2022-07-28
 */
final class MatchingResult<T> extends AbstractMatcher<T> implements Result<T> {
    private final Pattern<T> pattern;
    private final boolean[] results;

    MatchingResult(Pattern<T> pattern) {
        this.pattern = pattern;
        this.results = new boolean[pattern.length() + 1];
    }

    @Override
    public Pattern<T> pattern() {
        return this.pattern;
    }

    @Override
    protected Result<T> previous() {
        return this;
    }

    @Override
    public boolean get(int index) {
        return this.results[index + 1];
    }

    void set(int index, boolean value) {
        this.results[index + 1] = value;
    }
}
