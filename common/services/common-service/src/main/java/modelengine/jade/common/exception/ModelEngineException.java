/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.common.exception;

import modelengine.fitframework.exception.FitException;
import modelengine.fitframework.util.StringUtils;
import modelengine.jade.common.code.RetCode;

/**
 * 表示 AppEngine 子系统公共异常。
 *
 * @author 易文渊
 * @since 2024-09-05
 */
public class ModelEngineException extends FitException {
    private final Object[] args;

    /**
     * AppEngine 异常构造函数。
     *
     * @param code 表示返回码 {@link RetCode}。
     * @param args 表示异常信息参数的 {@code Object[]}。
     */
    public ModelEngineException(RetCode code, Object... args) {
        super(code.getCode(), code.getMsg());
        this.args = args;
    }

    /**
     * 插件部署异常构造函数。
     *
     * @param code 表示返回码 {@link RetCode}。
     * @param cause 表示异常原因的 {@link Throwable}。
     * @param args 表示异常信息参数的  {@link Object}{@code []}。
     */
    public ModelEngineException(RetCode code, Throwable cause, Object... args) {
        super(code.getCode(), code.getMsg(), cause);
        this.args = args;
    }

    /**
     * 获取异常参数列表。
     *
     * @return 表示异常参数列表的 {@link Object}{@code []}。
     */
    public Object[] getArgs() {
        return args;
    }

    /**
     * 获取异常信息。
     *
     * @return 表示异常信息的 {@link String}。
     */
    @Override
    public String getMessage() {
        return StringUtils.format(super.getMessage(), this.args);
    }
}