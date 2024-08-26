/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fitframework.test.domain.mockito;

import static modelengine.fitframework.util.ObjectUtils.nullIf;

import modelengine.fitframework.annotation.Order;
import modelengine.fitframework.ioc.BeanMetadata;
import modelengine.fitframework.ioc.lifecycle.bean.BeanLifecycle;
import modelengine.fitframework.ioc.lifecycle.bean.BeanLifecycleInterceptor;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.TypeUtils;

import org.mockito.Mockito;

import java.util.Collections;
import java.util.Set;

/**
 * 表示 {@link BeanLifecycleInterceptor} 的 Mockito 中的 {@link org.mockito.Spy} 实现。
 *
 * @author 季聿阶
 * @since 2024-07-28
 */
@Order(Order.HIGHEST)
public class SpyInterceptor implements BeanLifecycleInterceptor {
    private final Set<Class<?>> toSpyClasses;

    public SpyInterceptor(Set<Class<?>> toSpyClasses) {
        this.toSpyClasses = ObjectUtils.nullIf(toSpyClasses, Collections.emptySet());
    }

    @Override
    public boolean isInterceptionRequired(BeanMetadata metadata) {
        return this.toSpyClasses.contains(TypeUtils.toClass(metadata.type()));
    }

    @Override
    public Object decorate(BeanLifecycle lifecycle, Object bean) {
        Object initializedBean = lifecycle.decorate(bean);
        return Mockito.spy(initializedBean);
    }
}
