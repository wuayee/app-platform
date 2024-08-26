/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.tool.parser.exception;

import modelengine.fitframework.exception.FitException;
import modelengine.fitframework.util.StringUtils;

import com.huawei.jade.store.tool.parser.code.PluginDeployRetCode;

import java.util.HashMap;
import java.util.Map;

/**
 * 插件部署异常。
 *
 * @author 张雪彬
 * @since 2024-08-07
 */
public class PluginDeployException extends FitException {
    private Object[] args;
    private PluginDeployRetCode code;

    /**
     * 插件部署异常构造函数。
     *
     * @param code 表示返回码 {@link PluginDeployRetCode}。
     * @param args 表示异常信息参数的 {@link Object}{@code []}。
     */
    public PluginDeployException(PluginDeployRetCode code, Object... args) {
        super(code.getCode(), StringUtils.format(code.getMsg(), args));
        this.args = args;
        this.code = code;
        super.setProperties(convertArgsToMap(args));
    }

    /**
     * 插件部署异常构造函数。
     *
     * @param code 表示返回码 {@link PluginDeployRetCode}。
     * @param cause 表示异常原因的 {@link Throwable}。
     * @param args 表示异常信息参数的  {@link Object}{@code []}。
     */
    public PluginDeployException(PluginDeployRetCode code, Throwable cause, Object... args) {
        super(code.getCode(), StringUtils.format(code.getMsg(), args), cause);
        this.args = args;
        this.code = code;
        super.setProperties(convertArgsToMap(args));
    }

    private Map<String, String> convertArgsToMap(Object[] args) {
        Map<String, String> argsMap = new HashMap<>(args.length);
        for (int i = 0; i < args.length; i++) {
            argsMap.put(String.valueOf(i), String.valueOf(args[i]));
        }
        return argsMap;
    }

    /**
     * 获取异常中参数。
     *
     * @return 表示异常中的参数 {@link Object}{@code []}。
     */
    public Object[] getArgs() {
        return args;
    }

    /**
     * 设置异常的参数。
     *
     * @param args 表示异常中的参数 {@link Object}{@code []}。
     */
    public void setArgs(Object[] args) {
        this.args = args;
    }

    /**
     * 设置异常的错误码。
     *
     * @param code 表示异常的 {@link PluginDeployRetCode}。
     */
    public void setCode(PluginDeployRetCode code) {
        this.code = code;
    }
}
