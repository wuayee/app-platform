/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.code.ports;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import modelengine.jade.common.code.CommonRetCode;

import modelengine.fit.http.client.HttpClassicClientResponse;
import modelengine.fit.jade.aipp.code.breaker.CodeExecuteGuard;
import modelengine.fit.jade.aipp.code.breaker.DefaultCodeExecuteGuardImpl;
import modelengine.fit.jade.aipp.code.command.CodeExecuteCommand;
import modelengine.fit.jade.aipp.code.command.CodeExecuteCommandHandler;
import modelengine.fit.jade.aipp.code.domain.entity.ProgrammingLanguage;
import modelengine.fit.jade.aipp.code.ports.rest.CodeExecuteController;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.exception.FitException;
import modelengine.fitframework.test.annotation.Mock;
import modelengine.fitframework.test.annotation.MvcTest;
import modelengine.fitframework.test.domain.mvc.MockMvc;
import modelengine.fitframework.test.domain.mvc.request.MockMvcRequestBuilders;
import modelengine.fitframework.test.domain.mvc.request.MockRequestBuilder;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;

/**
 * 表示 @{@link CodeExecuteController} 的测试集。
 *
 * @author 何嘉斌
 * @since 2024-10-16
 */
@MvcTest(classes = {CodeExecuteController.class, DefaultCodeExecuteGuardImpl.class})
@DisplayName("测试 CodeExecuteController")
public class CodeExecuteControllerTest {
    @Fit
    private MockMvc mockMvc;

    @Fit
    private CodeExecuteGuard codeExecuteGuard;

    @Mock
    private CodeExecuteCommandHandler commandHandler;

    private HttpClassicClientResponse<?> response;

    @AfterEach
    void teardown() throws IOException {
        reset(this.commandHandler);
        if (this.response != null) {
            this.response.close();
        }
    }

    @Test
    @DisplayName("节点运行成功")
    void shouldOkWhenRunCode() {
        when(this.commandHandler.handle(any())).thenReturn(1);
        CodeExecuteCommand command = new CodeExecuteCommand(new HashMap<>(), "fake code", ProgrammingLanguage.PYTHON);

        MockRequestBuilder requestBuilder =
                MockMvcRequestBuilders.post("/v1/api/code/run").jsonEntity(command).responseType(Void.class);
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(200);
    }

    @Test
    @DisplayName("节点运行失败，抛出异常")
    void shouldFailWhenRunCodeThrowException() {
        String msg = "Execution timed out";
        when(this.commandHandler.handle(any())).thenThrow(new FitException(CommonRetCode.INTERNAL_ERROR.getCode(),
                msg));
        CodeExecuteCommand command = new CodeExecuteCommand(new HashMap<>(), "fake code", ProgrammingLanguage.PYTHON);

        MockRequestBuilder requestBuilder =
                MockMvcRequestBuilders.post("/v1/api/code/run").jsonEntity(command).responseType(Void.class);
        this.response = this.mockMvc.perform(requestBuilder);
        assertThat(this.response.statusCode()).isEqualTo(500);
    }
}