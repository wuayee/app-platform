/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package modelengine.fitframework.aop.interceptor.support;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fitframework.aop.interceptor.MethodMatcher;
import modelengine.fitframework.inspection.Nonnull;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * 表示指定方法的匹配器。
 *
 * @author 季聿阶
 * @since 2022-12-15
 */
public class SpecifiedMethodMatcher implements MethodMatcher {
    private final Method method;

    public SpecifiedMethodMatcher(Method method) {
        this.method = notNull(method, "The specified method cannot be null.");
    }

    @Override
    public MatchResult match(@Nonnull Method method) {
        return MatchResult.match(Objects.equals(this.method, method));
    }
}
