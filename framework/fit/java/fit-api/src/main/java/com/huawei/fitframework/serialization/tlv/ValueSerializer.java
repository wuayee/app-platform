/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.serialization.tlv;

import com.huawei.fitframework.inspection.Nonnull;

/**
 * 表示 {@link com.huawei.fitframework.serialization.TagLengthValues} 中值的序列化器。
 *
 * @param <T> 表示值的类型的 {@link T}。
 * @author 季聿阶 j00559309
 * @since 2023-06-15
 */
public interface ValueSerializer<T> {
    /**
     * 表示跨进程异常的属性集的标签值。
     */
    int TAG_EXCEPTION_PROPERTIES = 1;

    /**
     * 获取当前序列化器所对应的标签。
     *
     * @return 表示当前序列化器所对应的标签的 {@code int}。
     */
    int tag();

    /**
     * 将标签的值序列化为二进制数组。
     *
     * @param value 表示待序列化的值的 {@link T}。
     * @return 表示标签的值序列化后的二进制数组的 {@code byte[]}。
     */
    byte[] serialize(@Nonnull T value);

    /**
     * 将标签的值的二进制数组反序列化为对象。
     *
     * @param bytes 表示待反序列化的二进制数组的 {@code byte[]}。
     * @return 表示反序列化后的标签的值的 {@link T}。
     */
    T deserialize(@Nonnull byte[] bytes);
}
