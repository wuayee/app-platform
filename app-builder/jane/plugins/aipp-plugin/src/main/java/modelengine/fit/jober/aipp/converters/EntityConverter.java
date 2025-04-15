/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2025-2025. All rights reserved.
 */

package modelengine.fit.jober.aipp.converters;

/**
 * 实体转换器接口.
 *
 * @author 张越
 * @since 2025-02-14
 */
public interface EntityConverter {
    /**
     * 源类型.
     *
     * @return {@link Class}{@code <}{@code T}{@code >} 对象.
     */
    Class<?> source();

    /**
     * 目标类型.
     *
     * @return {@link Class}{@code <}{@code T}{@code >} 对象.
     */
    Class<?> target();

    /**
     * 将源对象转换为目标独享.
     *
     * @param source 源对象.
     * @return 目标对象.
     */
    Object convert(Object source);
}
