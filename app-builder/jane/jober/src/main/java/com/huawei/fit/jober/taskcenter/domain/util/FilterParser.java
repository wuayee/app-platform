/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.domain.util;

import com.huawei.fit.jane.task.domain.PropertyDataType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 为 {@link Filter} 提供解析器。
 *
 * @author 梁济时 l00815032
 * @since 2024-01-12
 */
public interface FilterParser {
    /**
     * 从字符串中解析过滤器。
     *
     * @param dataType 表示属性数据类型的{@link PropertyDataType}
     * @param text 表示包含过滤器信息的字符串的 {@link String}。
     * @return 表示解析到的过滤器的 {@link Filter}。
     */
    Filter parse(PropertyDataType dataType, String text);

    /**
     * 声明一个过滤器。
     *
     * @author 梁济时 l00815032
     * @since 2024-01-12
     */
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Declare {
        /**
         * 指示过滤器的键。
         *
         * @return 表示过滤器的键的 {@link String}。
         */
        String value();
    }
}
