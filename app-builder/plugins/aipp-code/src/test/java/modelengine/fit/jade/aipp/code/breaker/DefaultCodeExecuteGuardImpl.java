/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.code.breaker;

import modelengine.fit.jade.aipp.code.command.CodeExecuteCommand;
import modelengine.fitframework.annotation.Component;

import java.util.function.Supplier;

/**
 * {@link CodeExecuteGuard} 的打桩实现。
 *
 * @author 邱晓霞
 * @since 2025-01-16
 */
@Component
public class DefaultCodeExecuteGuardImpl implements CodeExecuteGuard {
    @Override
    public Object apply(CodeExecuteCommand command, Supplier<Object> codeExecuteResultSupplier) {
        return codeExecuteResultSupplier.get();
    }
}
