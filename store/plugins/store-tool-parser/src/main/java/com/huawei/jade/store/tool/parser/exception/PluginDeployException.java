/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.tool.parser.exception;

import com.huawei.fitframework.exception.FitException;
import com.huawei.fitframework.util.StringUtils;
import com.huawei.jade.store.tool.parser.code.PluginDeployRetCode;

/**
 * 插件部署异常
 *
 * @author 张雪彬
 * @since 2024-08-07
 */
public class PluginDeployException extends FitException {
    /**
     * 插件部署异常构造函数
     *
     * @param code 表示返回码 {@link PluginDeployRetCode}
     * @param args 表示异常信息参数的 {@code Object[]}
     */
    public PluginDeployException(PluginDeployRetCode code, Object... args) {
        super(code.getCode(), StringUtils.format(code.getMsg(), args));
    }

    /**
     * 插件部署异常构造函数
     *
     * @param code 表示返回码 {@link PluginDeployRetCode}
     * @param cause 表示异常原因的 {@link Throwable}
     * @param args 表示异常信息参数的 {@code Object[]}
     */
    public PluginDeployException(PluginDeployRetCode code, Throwable cause, Object... args) {
        super(code.getCode(), StringUtils.format(code.getMsg(), args), cause);
    }
}
