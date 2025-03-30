/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.code.ports;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import modelengine.jade.common.code.CommonRetCode;
import modelengine.jade.schema.SchemaValidator;

import modelengine.fit.jade.aipp.code.breaker.CodeExecuteGuard;
import modelengine.fit.jade.aipp.code.breaker.DefaultCodeExecuteGuardImpl;
import modelengine.fit.jade.aipp.code.command.CodeExecuteCommandHandler;
import modelengine.fit.jade.aipp.code.domain.entity.ProgrammingLanguage;
import modelengine.fit.jade.aipp.code.ports.fit.AippCodeExecuteService;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.exception.FitException;
import modelengine.fitframework.test.annotation.FitTestWithJunit;
import modelengine.fitframework.test.annotation.Mock;
import modelengine.fitframework.util.MapBuilder;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * 表示 {@link AippCodeExecuteService} 的测试集。
 *
 * @author 何嘉斌
 * @since 2024-10-17
 */
@FitTestWithJunit(includeClasses = {AippCodeExecuteService.class, DefaultCodeExecuteGuardImpl.class})
@DisplayName("测试 AippCodeExecuteService")
public class AippCodeExecuteServiceTest {
    @Fit
    private AippCodeExecuteService codeExecuteService;

    @Fit
    private CodeExecuteGuard codeExecuteGuard;

    @Mock
    private CodeExecuteCommandHandler commandHandler;

    @Mock
    private SchemaValidator schemaValidator;

    @AfterEach
    void teardown() {
        reset(this.commandHandler);
        reset(this.schemaValidator);
    }

    @Test
    @DisplayName("节点运行成功")
    void shouldOkWhenRunCode() {
        Map<String, Object> resultMap = MapBuilder.<String, Object>get().put("age", 10).build();
        when(this.commandHandler.handle(any())).thenReturn(resultMap);
        Map<String, Object> properties =
                MapBuilder.<String, Object>get().put("age", MapBuilder.get().put("type", "integer").build()).build();
        Map<String, Object> schema = MapBuilder.<String, Object>get().put("properties", properties).build();
        Object result = this.codeExecuteService.executeCode(new HashMap<>(),
                "fake code",
                ProgrammingLanguage.PYTHON.toString(),
                schema);
        assertThat(result).isEqualTo(resultMap);
    }

    @Test
    @DisplayName("节点运行失败，抛出异常")
    void shouldFailWhenRunCodeThrowException() {
        String msg = "Execution timed out";
        when(this.commandHandler.handle(any())).thenThrow(new FitException(CommonRetCode.INTERNAL_ERROR.getCode(),
                msg));
        assertThatThrownBy(() -> this.codeExecuteService.executeCode(new HashMap<>(),
                "fake code",
                ProgrammingLanguage.PYTHON.toString(),
                null)).isInstanceOf(FitException.class)
                .extracting("code", "message")
                .containsExactly(CommonRetCode.INTERNAL_ERROR.getCode(), msg);
    }

    @Test
    @DisplayName("输出校验失败，抛出异常")
    void shouldFailWhenExecuteResultFailedSchemaValidation() {
        Map<String, Object> properties =
                MapBuilder.<String, Object>get().put("age", MapBuilder.get().put("type", "integer").build()).build();
        Map<String, Object> schema = MapBuilder.<String, Object>get().put("properties", properties).build();
        doThrow(new FitException("Validation Failed.")).when(this.schemaValidator).validate(anyMap(), anyMap());

        assertThatThrownBy(() -> this.codeExecuteService.executeCode(new HashMap<>(),
                "fake code",
                ProgrammingLanguage.PYTHON.toString(),
                schema)).isInstanceOf(FitException.class).extracting("message").isEqualTo("Validation Failed.");
    }

    @Test
    @DisplayName("校验一层输出成功")
    void shouldSuccessWithPrimitiveOutput() {
        Object codeResult = "10";
        when(this.commandHandler.handle(any())).thenReturn(codeResult);
        Map<String, Object> properties =
                MapBuilder.<String, Object>get().put("output", MapBuilder.get().put("type", "String").build()).build();
        Map<String, Object> schema = MapBuilder.<String, Object>get().put("properties", properties).build();
        Object result = this.codeExecuteService.executeCode(new HashMap<>(),
                "fake code",
                ProgrammingLanguage.PYTHON.toString(),
                schema);
        assertThat(result).isEqualTo(codeResult);
    }
}