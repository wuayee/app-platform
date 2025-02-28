/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.classify.question.command;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fel.core.chat.ChatModel;
import modelengine.fel.core.chat.ChatOption;
import modelengine.fel.core.chat.Prompt;
import modelengine.fel.core.chat.support.AiMessage;
import modelengine.fel.core.memory.CacheMemory;
import modelengine.fit.jade.aipp.classify.question.command.impl.ClassifyQuestionCommandHandlerImpl;
import modelengine.fit.jade.aipp.classify.question.utils.TestUtils;
import modelengine.fit.jade.aipp.memory.AippMemoryFactory;
import modelengine.fit.jade.aipp.model.service.AippModelCenter;
import modelengine.fitframework.flowable.Choir;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * 表示 {@link ClassifyQuestionCommandHandler} 的测试集。
 *
 * @author 张越
 * @since 2024-11-18
 */
@DisplayName("测试 ClassifyQuestionCommandHandler")
public class ClassifyQuestionCommandHandlerTest {
    private AippMemoryFactory memoryFactory;

    private AippModelCenter aippModelCenter;

    private ChatModel modelService;

    private ClassifyQuestionCommandHandler commandService;

    @BeforeEach
    void setUp() throws IOException {
        this.memoryFactory = mock(AippMemoryFactory.class);
        this.aippModelCenter = mock(AippModelCenter.class);
        this.modelService = mock(ChatModel.class);
        this.commandService = new ClassifyQuestionCommandHandlerImpl(this.memoryFactory, this.aippModelCenter,
                this.modelService);
    }

    @Test
    @DisplayName("测试执行问题分类命令成功")
    void shouldOkWhenHandleClassifyCommand() {
        String expect = "1";
        String histories = "Q: q1\nA: a1";
        when(this.memoryFactory.create(any(), any())).thenReturn(new CacheMemory() {
            @Override
            public String text() {
                return histories;
            }
        });
        when(this.aippModelCenter.getModelBaseUrl(any())).thenReturn("/model");
        when(this.modelService.generate(any(Prompt.class), any(ChatOption.class))).thenReturn(
                Choir.just(new AiMessage("1")));
        ClassifyQuestionCommand command = TestUtils.getCommand();
        Assertions.assertEquals(this.commandService.handle(command), expect);
        Assertions.assertEquals(command.getTypeList(),
                "{\"类型ID\":\"1\", \"问题类型\":\"a\"}" + "\n------\n" + "{\"类型ID\":\"2\", \"问题类型\":\"b\"}");
    }
}