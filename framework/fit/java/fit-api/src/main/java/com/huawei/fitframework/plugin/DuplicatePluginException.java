/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fitframework.plugin;

/**
 * 当插件重复时引发的异常。
 *
 * @author 梁济时 l00815032
 * @since 2022-06-29
 */
public class DuplicatePluginException extends RuntimeException {
    /**
     * 使用异常信息初始化 {@link DuplicatePluginException} 类的新实例。
     *
     * @param message 表示异常信息的 {@link String}。
     */
    public DuplicatePluginException(String message) {
        super(message);
    }
}
