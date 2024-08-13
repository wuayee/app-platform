/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fitframework.jvm.classfile.lang;

import com.huawei.fitframework.util.StringUtils;

/**
 * 当发生值溢出时引发的异常。
 *
 * @author 梁济时
 * @since 2022-06-07
 */
public class ValueOverflowException extends RuntimeException {
    ValueOverflowException(String value, String minimum, String maximum) {
        super(StringUtils.format("The value is overflow. [value={0}, minimum={1}, maximum={2}]",
                value, minimum, maximum));
    }
}
