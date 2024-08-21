/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fitframework.serialization.tlv;

import modelengine.fitframework.inspection.Nonnull;

/**
 * 表示 {@link com.huawei.fitframework.serialization.TagLengthValues} 中值的序列化器。
 *
 * @param <T> 表示值的类型的 {@link T}。
 * @author 季聿阶
 * @since 2023-06-15
 */
public interface ValueSerializer<T> {
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
