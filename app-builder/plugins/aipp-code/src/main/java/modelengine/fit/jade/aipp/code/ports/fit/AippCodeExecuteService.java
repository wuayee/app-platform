/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.code.ports.fit;

import modelengine.fit.jade.aipp.code.CodeExecuteService;
import modelengine.jade.schema.SchemaValidator;

import modelengine.fit.jade.aipp.code.breaker.CodeExecuteGuard;
import modelengine.fit.jade.aipp.code.command.CodeExecuteCommand;
import modelengine.fit.jade.aipp.code.command.CodeExecuteCommandHandler;
import modelengine.fit.jade.aipp.code.domain.entity.ProgrammingLanguage;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.ObjectUtils;

import java.util.Map;

/**
 * 表示 {@link CodeExecuteService} 的 aipp 实现。
 *
 * @author 易文渊
 * @author 何嘉斌
 * @since 2024-10-14
 */
@Component
public class AippCodeExecuteService implements CodeExecuteService {
    private final CodeExecuteCommandHandler codeExecuteCommandHandler;
    private final CodeExecuteGuard codeExecuteGuard;

    private final SchemaValidator schemaValidator;

    public AippCodeExecuteService(CodeExecuteCommandHandler codeExecuteCommandHandler,
            CodeExecuteGuard codeExecuteGuard, SchemaValidator schemaValidator) {
        this.codeExecuteCommandHandler = codeExecuteCommandHandler;
        this.codeExecuteGuard = codeExecuteGuard;
        this.schemaValidator = schemaValidator;
    }

    @Override
    @Fitable("default")
    public Object executeCode(Map<String, Object> args, String code, String language, Map<String, Object> output) {
        CodeExecuteCommand command = new CodeExecuteCommand(args, code, ProgrammingLanguage.from(language));
        return codeExecuteGuard.apply(command, () -> {
            Object result = this.codeExecuteCommandHandler.handle(command);
            Map<String, Object> resultMap = MapBuilder.<String, Object>get().put("output", result).build();
            if (output != null) {
                addRequiredFields(output);
                this.schemaValidator.validate(output, resultMap);
            }
            return result;
        });
    }

    private void addRequiredFields(Map<String, Object> schema) {
        if (schema.containsKey("properties")) {
            Map<String, Object> properties = ObjectUtils.cast(schema.get("properties"));
            if (!schema.containsKey("required")) {
                schema.put("required", properties.keySet().toArray());
            }
            for (Object value : properties.values()) {
                Map<String, Object> field = ObjectUtils.cast(value);
                if (field.get("type").equals("object")) {
                    addRequiredFields(ObjectUtils.cast(value));
                }
            }
        }
    }
}