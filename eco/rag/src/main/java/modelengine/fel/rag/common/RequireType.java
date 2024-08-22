/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fel.rag.common;

/**
 * 类型校验工具。
 *
 * @since 2024-05-07
 */
public class RequireType {
    /**
     * 对传入的对象进行类型校验。
     * <p>成功则返回转换后的值，否则抛出异常。</p>
     *
     * @param obj 表示要校验的对象的 {@link Object}。
     * @param clazz 表示要校验的类型的 {@link Class}。
     * @param <T> 表示任何类型的泛型参数 {@link T}。
     * @return 表示转换后的值。
     */
    public static <T> T check(Object obj, Class<T> clazz) {
        if (obj == null) {
            throw new IllegalArgumentException("Object is null");
        }
        if (!clazz.isInstance(obj)) {
            throw new IllegalArgumentException("Object is not of type " + clazz.getSimpleName());
        }
        return clazz.cast(obj);
    }
}
