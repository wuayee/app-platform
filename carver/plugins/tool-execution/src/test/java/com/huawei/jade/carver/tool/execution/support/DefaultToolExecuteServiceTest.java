/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.carver.tool.execution.support;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.huawei.fit.serialization.json.jackson.JacksonObjectSerializer;
import com.huawei.fitframework.util.MapBuilder;
import com.huawei.jade.carver.tool.Tool;
import com.huawei.jade.carver.tool.ToolFactory;
import com.huawei.jade.carver.tool.model.transfer.ToolData;
import com.huawei.jade.carver.tool.repository.ToolFactoryRepository;
import com.huawei.jade.carver.tool.service.ToolService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * 表示 {@link DefaultToolExecuteService} 的单元测试。
 *
 * @author 王攀博
 * @since 2024-04-27
 */
@DisplayName("测试 DefaultToolExecuteService")
public class DefaultToolExecuteServiceTest {
    private DefaultToolExecuteService service;
    private JacksonObjectSerializer serializer;

    @BeforeEach
    void setup() {
        ToolService toolService = mock(ToolService.class);
        ToolFactoryRepository toolFactoryRepository = mock(ToolFactoryRepository.class);
        this.serializer = new JacksonObjectSerializer(null, null, null);
        this.service = new DefaultToolExecuteService(toolService, toolFactoryRepository);
        ToolFactory toolFactory = mock(ToolFactory.class);
        Tool tool = mock(Tool.class);
        when(toolService.getTool(any())).thenReturn(this.buildToolData());
        when(toolFactory.create(any(), any())).thenReturn(tool);
        when(toolFactoryRepository.query(any())).thenReturn(Optional.of(toolFactory));
        when(tool.executeWithJson(any())).thenReturn("OK");
        when(tool.executeWithJsonObject(any())).thenReturn("OK");
    }

    @Test
    @DisplayName("当使用 Json 参数调用执行器时，返回正确结果")
    void shouldReturnOKWhenExecuteWithJson() {
        String uniqueName = "testUniqueName";
        String executeResult = this.service.executeTool(uniqueName, this.buildJsonArgs());
        assertThat(executeResult).isEqualTo("OK");
    }

    @Test
    @DisplayName("当使用 Json 对象参数调用执行器时，返回正确结果")
    void shouldReturnOKWhenExecuteWithJsonObject() {
        String uniqueName = "testUniqueName";
        Object executeResult = this.service.executeTool(uniqueName, this.buildJsonObjectArgs());
        assertThat(executeResult).isEqualTo("OK");
    }

    private Map<String, Object> buildSchema() {
        return MapBuilder.<String, Object>get()
                .put("name", "test_schema_default_implementation_name")
                .put("index", "test_schema_index")
                .put("description", "This is a demo FIT function.")
                .put("parameters",
                        MapBuilder.<String, Object>get()
                                .put("type", "object")
                                .put("properties",
                                        MapBuilder.<String, Object>get()
                                                .put("p1",
                                                        MapBuilder.<String, Object>get()
                                                                .put("type", "string")
                                                                .put("description", "This is the first parameter.")
                                                                .build())
                                                .build())
                                .put("required", Collections.singletonList("p1"))
                                .build())
                .put("order", Collections.singletonList("p1"))
                .put("return", MapBuilder.<String, Object>get().put("type", "string").build())
                .build();
    }

    private ToolData buildToolData() {
        ToolData toolData = new ToolData();
        toolData.setSchema(this.buildSchema());
        return toolData;
    }

    private Map<String, Object> buildJsonObjectArgs() {
        return MapBuilder.<String, Object>get().put("p1", "1").build();
    }

    private String buildJsonArgs() {
        Map<String, Object> in = this.buildJsonObjectArgs();
        return new String(this.serializer.serialize(in, UTF_8));
    }
}