/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fitframework.aop.interceptor.support;

import com.huawei.fitframework.aop.interceptor.MethodMatcher;
import com.huawei.fitframework.aop.interceptor.MethodMatcherCollection;
import com.huawei.fitframework.inspection.Validation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * {@link MethodMatcherCollection} 的默认实现。
 *
 * @author 季聿阶
 * @since 2022-05-11
 */
public class DefaultMethodMatcherCollection implements MethodMatcherCollection {
    private final List<MethodMatcher> methodMatchers = new ArrayList<>();

    @Override
    public void add(MethodMatcher matcher) {
        Validation.notNull(matcher, "The method matcher to add to method matcher collection cannot be null.");
        this.methodMatchers.add(matcher);
    }

    @Override
    public List<MethodMatcher> all() {
        return Collections.unmodifiableList(this.methodMatchers);
    }
}
