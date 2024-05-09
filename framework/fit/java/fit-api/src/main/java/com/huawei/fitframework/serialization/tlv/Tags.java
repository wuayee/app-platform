/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fitframework.serialization.tlv;

import static com.huawei.fitframework.inspection.Validation.isTrue;
import static com.huawei.fitframework.inspection.Validation.notNull;
import static com.huawei.fitframework.util.ObjectUtils.cast;

import com.huawei.fitframework.util.ReflectionUtils;
import com.huawei.fitframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 表示 {@link com.huawei.fitframework.serialization.TagLengthValues} 的标签常量值。
 *
 * @author 季聿阶
 * @since 2024-05-09
 */
public class Tags {
    /** 表示进程唯一标识的标签值。 */
    private static final int WORKER_ID_TAG = 0x00;

    /** 表示进程实例唯一标识的标签值。 */
    private static final int WORKER_INSTANCE_ID_TAG = 0x01;

    /** 表示异常属性的标签值。 */
    private static final int EXCEPTION_PROPERTIES_TAG = 0x10;

    static {
        // 校验标签值，确保所有标签值不冲突。
        validate(Tags.class);
    }

    /**
     * 获取进程唯一标识的标签值。
     *
     * @return 表示进程唯一标识的标签值 {@code int}。
     */
    public static int getWorkerIdTag() {
        return WORKER_ID_TAG;
    }

    /**
     * 获取进程实例唯一标识的标签值。
     *
     * @return 表示进程实例唯一标识的标签值的 {@code int}。
     */
    public static int getWorkerInstanceIdTag() {
        return WORKER_INSTANCE_ID_TAG;
    }

    /**
     * 获取异常属性的标签值。
     *
     * @return 表示异常属性的标签值的 {@code int}。
     */
    public static int getExceptionPropertiesTag() {
        return EXCEPTION_PROPERTIES_TAG;
    }

    /**
     * 校验 TLV 的常量，保证所有标签值不冲突。
     *
     * @param tlvClass 表示待校验的 TLV 的类的 {@link Class}{@code <?>}。
     */
    protected static void validate(Class<?> tlvClass) {
        notNull(tlvClass, "The TLV class cannot be null.");
        isTrue(Tags.class.isAssignableFrom(tlvClass),
                "The TLV class is not TlvConstants. [class={0}]",
                tlvClass.getName());
        Set<Integer> values = new HashSet<>();
        for (Field field : findConstants(tlvClass)) {
            int value = cast(ReflectionUtils.getField(null, field));
            if (values.contains(value)) {
                throw new IllegalStateException(StringUtils.format("TLV definition conflict. [field={0}, value={1}]",
                        field.getName(),
                        value));
            }
            values.add(value);
        }
    }

    private static List<Field> findConstants(Class<?> clazz) {
        return Arrays.stream(ReflectionUtils.getDeclaredFields(clazz, true))
                .filter(field -> Modifier.isPrivate(field.getModifiers()) && Modifier.isStatic(field.getModifiers())
                        && Modifier.isFinal(field.getModifiers()) && Objects.equals(field.getType(), int.class))
                .collect(Collectors.toList());
    }
}
