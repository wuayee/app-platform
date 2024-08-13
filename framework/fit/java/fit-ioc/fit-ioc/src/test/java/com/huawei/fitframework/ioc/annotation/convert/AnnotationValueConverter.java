/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
 */

package com.huawei.fitframework.ioc.annotation.convert;

import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.convert.Converter;

/**
 * 表示将 {@link StringValue} 的值向 {@link IntValue} 转换的程序。
 *
 * @author 梁济时
 * @since 2023-01-28
 */
public class AnnotationValueConverter implements Converter {
    @Override
    public Object convert(Object value) {
        return Integer.parseInt(ObjectUtils.cast(value));
    }
}
