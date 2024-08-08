/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.tool.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.huawei.fit.serialization.json.jackson.JacksonObjectSerializer;
import com.huawei.fitframework.serialization.ObjectSerializer;
import com.huawei.fitframework.util.IoUtils;
import com.huawei.fitframework.util.StringUtils;
import com.huawei.fitframework.util.TypeUtils;
import com.huawei.jade.fel.tool.Tool;
import com.huawei.jade.fel.tool.ToolEntity;
import com.huawei.jade.fel.tool.ToolFactory;
import com.huawei.jade.fel.tool.ToolFactoryRepository;
import com.huawei.jade.fel.tool.ToolSchema;
import com.huawei.jade.fel.tool.service.ToolExecuteService;
import com.huawei.jade.fel.tool.service.ToolRepository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

/**
 * 表示 {@link DefaultToolExecutor} 的测试集。
 *
 * @author 王攀博
 * @author 易文渊
 * @since 2024-04-27
 */
@DisplayName("测试 DefaultToolExecutor")
public class DefaultToolExecutorTest {
    private ToolRepository toolRepository;
    private ToolFactoryRepository toolFactoryRepository;
    private ToolFactory toolFactory;
    private final ObjectSerializer serializer = new JacksonObjectSerializer(null, null, null);

    @BeforeEach
    void setUp() {
        this.toolRepository = mock(ToolRepository.class);
        this.toolFactoryRepository = mock(ToolFactoryRepository.class);
        this.toolFactory = mock(ToolFactory.class);
    }

    @AfterEach
    void tearDown() {
        clearInvocations(this.toolRepository, this.toolFactoryRepository, this.toolFactory);
    }

    @Test
    @DisplayName("调用工具成功返回结果")
    void shouldOkWhenExecuteTool() throws IOException {
        ToolEntity toolEntity = getTestEntity();
        when(this.toolRepository.getTool(any(), eq(toolEntity.name()))).thenReturn(toolEntity);
        when(this.toolFactoryRepository.match(any())).thenReturn(Optional.of(this.toolFactory));
        Tool tool = mock(Tool.class, RETURNS_DEEP_STUBS);
        when(this.toolFactory.create(any(), any())).thenReturn(tool);
        when(tool.executeWithJson(any())).thenReturn("hello");
        when(tool.metadata().returnConverter()).thenReturn(StringUtils.EMPTY);
        ToolExecuteService toolExecutor =
                new DefaultToolExecutor(this.toolRepository, this.toolFactoryRepository, this.serializer);
        assertThat(toolExecutor.execute("test", toolEntity.name(), "test")).isEqualTo("hello");
    }

    @Test
    @DisplayName("工具不存在，调用失败")
    void shouldFailWhenToolNotFound() {
        ToolExecuteService toolExecutor =
                new DefaultToolExecutor(this.toolRepository, this.toolFactoryRepository, this.serializer);
        when(this.toolRepository.getTool(any(), any())).thenReturn(null);
        assertThatThrownBy(() -> toolExecutor.execute("test",
                "test",
                "test")).isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("工具工厂不存在，调用失败")
    void shouldFailWhenToolFactoryNotFound() throws IOException {
        ToolEntity toolEntity = getTestEntity();
        when(this.toolRepository.getTool(any(), any())).thenReturn(toolEntity);
        when(this.toolFactoryRepository.match(any())).thenReturn(Optional.empty());

        ToolExecuteService toolExecutor =
                new DefaultToolExecutor(this.toolRepository, this.toolFactoryRepository, this.serializer);
        assertThatThrownBy(() -> toolExecutor.execute("test",
                "test",
                "test")).isInstanceOf(IllegalStateException.class);
    }

    private ToolEntity getTestEntity() throws IOException {
        List<ToolEntity> toolEntities =
                this.serializer.deserialize(IoUtils.content(this.getClass().getClassLoader(), ToolSchema.TOOL_MANIFEST),
                        TypeUtils.parameterized(List.class, new Type[] {ToolEntity.class}));
        return toolEntities.get(0);
    }
}