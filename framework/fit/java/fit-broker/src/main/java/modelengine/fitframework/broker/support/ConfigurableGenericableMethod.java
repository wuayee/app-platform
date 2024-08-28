/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
 */

package modelengine.fitframework.broker.support;

import modelengine.fitframework.broker.GenericableMethod;
import modelengine.fitframework.inspection.Nullable;

import java.lang.reflect.Method;

/**
 * 表示可修改的 {@link GenericableMethod}。
 *
 * @author 季聿阶
 * @since 2023-03-29
 */
public class ConfigurableGenericableMethod implements GenericableMethod {
    private Method method;

    @Nullable
    @Override
    public Method method() {
        return this.method;
    }

    /**
     * 设置泛服务的方法。
     *
     * @param method 表示泛服务的方法的 {@link Method}。
     */
    public void method(Method method) {
        this.method = method;
    }
}
