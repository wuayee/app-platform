/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fitframework.beans.convert;

import static com.huawei.fitframework.inspection.Validation.greaterThanOrEquals;
import static com.huawei.fitframework.util.ObjectUtils.cast;

import com.huawei.fitframework.beans.BeanAccessor;
import com.huawei.fitframework.util.ReflectionUtils;
import com.huawei.fitframework.util.StringUtils;
import com.huawei.fitframework.util.TypeUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Map;

/**
 * 表示 {@link ConversionService} 的标准实现。
 *
 * @author 季聿阶 j00559309
 * @since 2024-02-25
 */
public class StandardConversionService extends AbstractConversionService {
    /** 获取当前类型的单例实现。 */
    static final ConversionService INSTANCE = new StandardConversionService();

    private StandardConversionService() {}

    @Override
    public Object convert(Object value, Type type) {
        if (type == null) {
            return value;
        } else if (type instanceof Class) {
            return this.as(value, (Class<?>) type);
        } else if (type instanceof ParameterizedType) {
            return this.as(value, (ParameterizedType) type);
        } else {
            throw new IllegalArgumentException(StringUtils.format("Cannot convert value to target type. [target={0}]",
                    type.getTypeName()));
        }
    }

    @Override
    protected Object toCustomObject(Object value, ParameterizedType type) {
        Class<?> rawClass = (Class<?>) type.getRawType();
        Map<String, Object> properties = cast(value);
        Object bean = ReflectionUtils.instantiate(rawClass);
        for (Field field : rawClass.getDeclaredFields()) {
            Type fieldType = field.getGenericType();
            if (fieldType instanceof TypeVariable) {
                int typeIndex = TypeUtils.getTypeVariableIndex((TypeVariable<?>) fieldType);
                greaterThanOrEquals(typeIndex, 0, "The type variable is undefined. [field={0}]", field.getName());
                Type actualType = type.getActualTypeArguments()[typeIndex];
                Object removed = properties.remove(field.getName());
                Object converted = this.convert(removed, actualType);
                ReflectionUtils.setField(bean, field, converted);
            }
        }
        BeanAccessor.of(rawClass).accept(bean, properties);
        return bean;
    }
}
