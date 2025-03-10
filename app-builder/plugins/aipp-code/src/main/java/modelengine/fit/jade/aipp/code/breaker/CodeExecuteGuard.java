/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.code.breaker;

import modelengine.fit.jade.aipp.code.command.CodeExecuteCommand;

import java.util.function.Supplier;

/**
 * 监视代码执行。
 *
 * @author 邱晓霞
 * @since 2025-01-16
 */
public interface CodeExecuteGuard {
    /**
     * 监视代码执行情况。
     *
     * @param command 包含代码入参和代码的 {@link CodeExecuteCommand}。
     * @param codeExecuteResultSupplier 表示供应代码执行结果的 {@link Supplier}{@code <}{@link Object}{@code >}。
     * @return 表示代码执行结果的 {@link Object}。
     */
    Object apply(CodeExecuteCommand command, Supplier<Object> codeExecuteResultSupplier);
}
