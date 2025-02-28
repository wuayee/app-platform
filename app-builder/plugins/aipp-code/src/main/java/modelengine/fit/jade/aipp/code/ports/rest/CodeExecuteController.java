/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.code.ports.rest;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.jade.common.vo.Result;

import modelengine.fit.http.annotation.PostMapping;
import modelengine.fit.http.annotation.RequestBody;
import modelengine.fit.http.annotation.RequestMapping;
import modelengine.fit.jade.aipp.code.breaker.CodeExecuteGuard;
import modelengine.fit.jade.aipp.code.command.CodeExecuteCommand;
import modelengine.fit.jade.aipp.code.command.CodeExecuteCommandHandler;
import modelengine.fitframework.annotation.Component;

/**
 * 代码节点 IDE 运行接口控制器。
 *
 * @author 易文渊
 * @author 何嘉斌
 * @since 2024-10-14
 */
@Component
@RequestMapping(path = "/v1/api/code", group = "aipp 代码运行接口")
public class CodeExecuteController {
    private final CodeExecuteCommandHandler codeExecuteCommandHandler;
    private final CodeExecuteGuard codeExecuteGuard;

    public CodeExecuteController(CodeExecuteCommandHandler codeExecuteCommandHandler,
            CodeExecuteGuard codeExecuteGuard) {
        this.codeExecuteCommandHandler = notNull(codeExecuteCommandHandler, "The command service cannot be null");
        this.codeExecuteGuard = notNull(codeExecuteGuard, "The circuit breaker manager cannot be null");
    }

    /**
     * 执行用户代码。
     *
     * @param command 包含代码入参和代码的 {@link CodeExecuteCommand}。
     * @return 表示代码执行结果的 {@link Result}{@code <}{@link Object}{@code >}。
     */
    @PostMapping(value = "/run", description = "运行用户的代码")
    public Object run(@RequestBody CodeExecuteCommand command) {
        return this.codeExecuteGuard.apply(command, () -> this.codeExecuteCommandHandler.handle(command));
    }
}