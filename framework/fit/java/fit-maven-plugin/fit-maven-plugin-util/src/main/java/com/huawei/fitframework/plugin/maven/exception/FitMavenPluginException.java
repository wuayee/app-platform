/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2023. All rights reserved.
 */

package com.huawei.fitframework.plugin.maven.exception;

/**
 * 表示Fit插件异常的类
 *
 * @author 张浩亮
 * @since 2021/5/24
 */
public class FitMavenPluginException extends RuntimeException {
    public FitMavenPluginException(String message) {
        super(message);
    }

    public FitMavenPluginException(String message, Throwable cause) {
        super(message, cause);
    }
}
