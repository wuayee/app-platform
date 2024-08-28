/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2022. All rights reserved.
 */

package modelengine.fitframework.serialization.util;

/**
 * 当可变长度的数字无效时引发的异常。
 *
 * @author 梁济时
 * @since 2020-11-13
 */
public class IllegalVaryingNumberException extends RuntimeException {
    /**
     * 使用异常信息初始化 {@link IllegalVaryingNumberException} 类的新实例。
     *
     * @param message 表示异常信息的 {@link String}。
     */
    public IllegalVaryingNumberException(String message) {
        super(message);
    }
}
