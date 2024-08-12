/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
 */

package com.huawei.fitframework.ioc.lifecycle.bean.support;

import static com.huawei.fitframework.util.ObjectUtils.cast;

import com.huawei.fitframework.beans.BeanAccessor;
import com.huawei.fitframework.beans.convert.ConversionService;
import com.huawei.fitframework.conf.Config;
import com.huawei.fitframework.ioc.lifecycle.bean.BeanInjector;
import com.huawei.fitframework.util.TypeUtils;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * 为 {@link BeanInjector} 提供基于配置的实现。
 *
 * @author 梁济时
 * @since 2023-01-06
 */
public class ConfigBeanInjector implements BeanInjector {
    private final Config config;
    private final String key;

    public ConfigBeanInjector(Config config, String key) {
        this.config = config;
        this.key = key;
    }

    @Override
    public void inject(Object bean) {
        Type type = TypeUtils.parameterized(Map.class, new Type[] {String.class, Object.class});
        Map<String, Object> values = cast(this.config.get(this.key, type));
        if (values == null) {
            return;
        }
        BeanAccessor.of(bean.getClass(), ConversionService.forConfig()).accept(bean, values);
    }
}
