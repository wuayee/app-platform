/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.ohscript.script.parser.nodes;

import modelengine.fit.ohscript.script.errors.OhPanic;
import modelengine.fit.ohscript.script.interpreter.ASTEnv;
import modelengine.fit.ohscript.script.interpreter.ActivationContext;
import modelengine.fit.ohscript.script.interpreter.ReturnValue;

/**
 * 可解释执行的
 *
 * @since 1.0
 */
public interface Interpretable {
    /**
     * 解释执行
     *
     * @param env 抽象语法树环境
     * @param context 激活上下文
     * @return 返回值
     * @throws OhPanic 抛出OhPanic异常
     */
    ReturnValue interpret(ASTEnv env, ActivationContext context) throws OhPanic;
}
