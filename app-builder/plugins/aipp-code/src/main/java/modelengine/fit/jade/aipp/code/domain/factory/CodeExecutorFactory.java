/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.code.domain.factory;

import modelengine.fit.jade.aipp.code.domain.entity.CodeExecutor;
import modelengine.fit.jade.aipp.code.domain.entity.ProgrammingLanguage;

/**
 * 表示代码执行器工厂。
 *
 * @author 易文渊
 * @author 何嘉斌
 * @since 2024-10-14
 */
public interface CodeExecutorFactory {
    /**
     * 根据策略创建代码执行器。
     *
     * @param type 表示编程语言类型的 {@link ProgrammingLanguage}。
     * @return 表示代码执行器的 {@link CodeExecutor}。
     */
    CodeExecutor create(ProgrammingLanguage type);

    /**
     * 注册一个代码执行器。
     *
     * @param codeExecutor 表示重写算子的 {@link CodeExecutor}。
     */
    void register(CodeExecutor codeExecutor);
}