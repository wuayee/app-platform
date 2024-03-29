/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fitframework.ioc.support;

import com.huawei.fitframework.ioc.BeanDependency;
import com.huawei.fitframework.ioc.DependencyNotFoundException;
import com.huawei.fitframework.ioc.DependencyResolvingResult;
import com.huawei.fitframework.ioc.lifecycle.bean.ValueSupplier;
import com.huawei.fitframework.util.StringUtils;

/**
 * 为 {@link ValueSupplier} 提供基于 {@link BeanDependency} 的实现。
 *
 * @author 梁济时 l00815032
 * @since 2022-06-27
 */
class BeanDependencySupplier implements ValueSupplier {
    private final BeanDependency dependency;

    /**
     * 使用依赖初始化 {@link BeanDependencySupplier} 类的新实例。
     *
     * @param dependency 表示依赖的 {@link BeanDependency}。
     */
    public BeanDependencySupplier(BeanDependency dependency) {
        this.dependency = dependency;
    }

    @Override
    public Object get() {
        DependencyResolvingResult result = this.dependency.resolve();
        Object value = result.resolved() ? result.get() : null;
        if (this.dependency.required() && value == null) {
            throw new DependencyNotFoundException(StringUtils.format(
                    "Dependency required but not found. [type={0}, name={1}]",
                    this.dependency.type().getTypeName(), this.dependency.name()));
        } else {
            return value;
        }
    }
}
