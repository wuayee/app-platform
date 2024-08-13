/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fitframework.conf;

/**
 * 为配置的值发生变化提供观察者。
 *
 * @author 梁济时
 * @since 2022-11-17
 */
public interface ConfigValueChangedObserver {
    /**
     * 当配置的值发生改变时被通知。
     *
     * @param key 表示值发生变化的配置的键的 {@link String}。
     * @param oldValue 表示配置的原始值的 {@link Object}。
     */
    void notifyConfigValueChanged(String key, Object oldValue);
}
