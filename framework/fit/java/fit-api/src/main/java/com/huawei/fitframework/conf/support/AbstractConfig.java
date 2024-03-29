/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2024. All rights reserved.
 */

package com.huawei.fitframework.conf.support;

import static com.huawei.fitframework.util.ObjectUtils.nullIf;

import com.huawei.fitframework.beans.convert.ConversionService;
import com.huawei.fitframework.conf.Config;
import com.huawei.fitframework.conf.ConfigValueSupplier;
import com.huawei.fitframework.util.StringUtils;

import java.lang.reflect.Type;

/**
 * 为 {@link Config} 提供基类。
 *
 * @author 梁济时 l00815032
 * @since 2022-12-14
 */
public abstract class AbstractConfig implements Config, ConfigValueSupplier {
    private final String name;

    /**
     * 使用配置的名称初始化 {@link AbstractConfig} 类的新实例。
     *
     * @param name 表示配置的名称的 {@link String}。
     */
    public AbstractConfig(String name) {
        this.name = nullIf(name, StringUtils.EMPTY);
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public Object get(String key) {
        return this.getWithCanonicalKey(Config.canonicalizeKey(key));
    }

    /**
     * 获取指定规范化键的值。
     *
     * @param key 表示规范化后的键的 {@link String}。
     * @return 表示指定规范化键的值的 {@link Object}。
     */
    protected abstract Object getWithCanonicalKey(String key);

    @Override
    public Object get(String key, Type type) {
        Object value = this.get(key);
        return ConversionService.forConfig().convert(value, type);
    }

    @Override
    public <T> T get(String key, Class<T> clazz) {
        Object value = this.get(key);
        return ConversionService.forConfig().convert(value, clazz);
    }

    @Override
    public String toString() {
        String display = this.name();
        if (StringUtils.isEmpty(display)) {
            display = super.toString();
        }
        return display;
    }
}
