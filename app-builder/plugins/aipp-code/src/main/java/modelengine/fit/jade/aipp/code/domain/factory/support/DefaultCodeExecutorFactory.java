/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.code.domain.factory.support;

import modelengine.fit.jade.aipp.code.domain.entity.CodeExecutor;
import modelengine.fit.jade.aipp.code.domain.entity.ProgrammingLanguage;
import modelengine.fit.jade.aipp.code.domain.factory.CodeExecutorFactory;
import modelengine.fitframework.inspection.Validation;
import modelengine.fitframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 表示 @{@link CodeExecutorFactory} 的默认实现。
 *
 * @author 何嘉斌
 * @since 2024-10-16
 */
public class DefaultCodeExecutorFactory implements CodeExecutorFactory {
    private final Map<ProgrammingLanguage, CodeExecutor> cache = new ConcurrentHashMap<>();

    @Override
    public CodeExecutor create(ProgrammingLanguage type) {
        return Validation.notNull(this.cache.get(type),
                () -> new IllegalArgumentException(StringUtils.format("Failed to create {0} code executor.", type)));
    }

    @Override
    public void register(CodeExecutor codeExecutor) {
        this.cache.put(codeExecutor.language(), codeExecutor);
    }
}