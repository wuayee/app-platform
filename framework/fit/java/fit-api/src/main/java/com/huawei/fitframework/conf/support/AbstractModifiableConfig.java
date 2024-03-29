/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 */

package com.huawei.fitframework.conf.support;

import static com.huawei.fitframework.util.ObjectUtils.cast;

import com.huawei.fitframework.conf.Config;
import com.huawei.fitframework.conf.ConfigDecryptor;
import com.huawei.fitframework.conf.ModifiableConfig;
import com.huawei.fitframework.conf.ModifiableConfigListener;
import com.huawei.fitframework.inspection.Nonnull;
import com.huawei.fitframework.util.TypeUtils;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

/**
 * 为 {@link ModifiableConfig} 提供基类。
 *
 * @author 梁济时 l00815032
 * @since 2022-12-14
 */
public abstract class AbstractModifiableConfig extends AbstractConfig implements ModifiableConfig {
    private final List<ModifiableConfigListener> listeners;

    /**
     * 初始化 {@link AbstractModifiableConfig} 类的新实例。
     *
     * @param name 表示配置的名称的 {@link String}。
     */
    public AbstractModifiableConfig(String name) {
        super(name);
        this.listeners = new LinkedList<>();
    }

    @Override
    public void set(String key, Object value) {
        this.setWithCanonicalKey(Config.canonicalizeKey(key), value);
    }

    /**
     * 设置配置的值。
     *
     * @param key 表示配置的标准化的键的 {@link String}。
     * @param value 表示配置的值的 {@link Object}。
     * @throws IllegalArgumentException 当 {@code key} 不是一个有效的配置键时。
     */
    protected abstract void setWithCanonicalKey(String key, Object value);

    @Override
    public void subscribe(ModifiableConfigListener listener) {
        if (listener != null) {
            this.listeners.add(listener);
        }
    }

    @Override
    public void unsubscribe(ModifiableConfigListener listener) {
        if (listener != null) {
            this.listeners.remove(listener);
        }
    }

    @Override
    public void decrypt(@Nonnull ConfigDecryptor decryptor) {
        for (String key : this.keys()) {
            List<String> values = cast(this.get(key, TypeUtils.parameterized(List.class, new Type[] {String.class})));
            for (String originValue : values) {
                decryptor.decrypt(key, originValue).ifPresent(newValue -> this.set(key, newValue));
            }
        }
    }

    /**
     * 通知属性的值发生变化。
     *
     * @param key 表示发生变化的配置的键的 {@link String}。
     */
    protected void notifyValueChanged(String key) {
        for (ModifiableConfigListener listener : this.listeners) {
            listener.onValueChanged(this, key);
        }
    }
}
