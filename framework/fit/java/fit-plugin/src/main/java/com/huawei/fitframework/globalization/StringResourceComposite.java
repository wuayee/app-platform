/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fitframework.globalization;

import com.huawei.fitframework.util.ArrayUtils;
import com.huawei.fitframework.util.StringUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * 为 {@link StringResource} 提供组合模式的实现。
 *
 * @author 梁济时 l00815032
 * @since 2022-11-22
 */
final class StringResourceComposite implements StringResource {
    private final List<StringResource> providers;

    private StringResourceComposite(List<StringResource> providers) {
        this.providers = providers;
    }

    @Override
    public String getMessage(Locale locale, String key, String defaultMessage, Object... args) {
        for (StringResource provider : this.providers) {
            String message = provider.getMessage(locale, key, args);
            if (message != null) {
                return message;
            }
        }
        if (ArrayUtils.isEmpty(args)) {
            return defaultMessage;
        } else {
            return StringUtils.format(defaultMessage, args);
        }
    }

    /**
     * 将一系列的消息提供程序组合为一个实例。
     * <ul>
     *     <li>组合过程首先会去掉集合中的 {@code null} 元素</li>
     *     <li>如果集合中没有元素，则返回 {@code null}</li>
     *     <li>如果集合中仅有一个元素，则返回该唯一的元素</li>
     *     <li>否则返回组合后的元素</li>
     * </ul>
     *
     * @param providers 表示待组合的消息提供程序的 {@link Iterable}{@code <}{@link StringResource}{@code >}。
     * @return 表示组合后的消息提供程序的 {@link StringResource}。
     */
    static StringResource combine(Iterable<StringResource> providers) {
        if (providers == null) {
            return null;
        }
        List<StringResource> actual = new LinkedList<>();
        for (StringResource provider : providers) {
            if (provider != null) {
                actual.add(provider);
            }
        }
        if (actual.isEmpty()) {
            return null;
        } else if (actual.size() > 1) {
            return new StringResourceComposite(actual);
        } else {
            return actual.get(0);
        }
    }
}
