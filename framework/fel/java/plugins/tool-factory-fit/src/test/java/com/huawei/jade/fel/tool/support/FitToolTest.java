/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.tool.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.huawei.fit.serialization.json.jackson.JacksonObjectSerializer;
import com.huawei.fitframework.broker.client.BrokerClient;
import com.huawei.fitframework.serialization.ObjectSerializer;
import com.huawei.fitframework.util.IoUtils;
import com.huawei.fitframework.util.TypeUtils;
import com.huawei.jade.fel.tool.Tool;
import com.huawei.jade.fel.tool.ToolEntity;
import com.huawei.jade.fel.tool.ToolFactory;
import com.huawei.jade.fel.tool.ToolSchema;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

/**
 * 表示 {@link FitTool} 的测试集。
 *
 * @author 易文渊
 * @since 2024-08-16
 */
@DisplayName("测试 FitTool")
public class FitToolTest {
    private Tool tool;

    @BeforeEach
    void setUp() throws IOException {
        BrokerClient client = mock(BrokerClient.class, RETURNS_DEEP_STUBS);
        when(client.getRouter(eq("test")).route(any()).invoke(any())).thenAnswer(invocation -> {
            if (Objects.equals(invocation.getArgument(0), "1")) {
                return "OK";
            }
            throw new IllegalStateException("Error");
        });
        ObjectSerializer serializer = new JacksonObjectSerializer(null, null, null);
        List<ToolEntity> toolEntities =
                serializer.deserialize(IoUtils.content(this.getClass().getClassLoader(), ToolSchema.TOOL_MANIFEST),
                        TypeUtils.parameterized(List.class, new Type[] {ToolEntity.class}));
        ToolEntity testEntity = toolEntities.get(0);
        ToolFactory toolFactory = new FitToolFactory(client, serializer);
        this.tool = toolFactory.create(testEntity, Tool.Metadata.from(testEntity.schema()));
    }

    @Test
    void test0() {
        assertThat(tool.executeWithJson("{\"orderId\": \"1\"}")).isEqualTo("OK");
    }

    @Test
    void test1() {
        assertThatThrownBy(() -> this.tool.execute("{\"config\": \"2\"}")).isInstanceOf(IllegalStateException.class)
                .hasMessage("Error");
    }
}