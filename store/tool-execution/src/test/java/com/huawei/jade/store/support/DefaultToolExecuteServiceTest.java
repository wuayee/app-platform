/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.support;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.huawei.fit.serialization.json.jackson.JacksonObjectSerializer;
import com.huawei.fitframework.util.MapBuilder;
import com.huawei.jade.store.Tool;
import com.huawei.jade.store.ToolFactory;
import com.huawei.jade.store.model.transfer.ToolData;
import com.huawei.jade.store.repository.ToolFactoryRepository;
import com.huawei.jade.store.service.ToolService;

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
    private ToolService toolService;
    private ToolFactoryRepository toolFactoryRepository;
    private JacksonObjectSerializer serializer;
    private ToolFactory toolFactory;

    @BeforeEach
    void setup() {
        this.toolService = mock(ToolService.class);
        this.toolFactoryRepository = mock(ToolFactoryRepository.class);
        this.serializer = new JacksonObjectSerializer(null, null, null);
        this.service = new DefaultToolExecuteService(this.toolService, this.toolFactoryRepository);
        this.toolFactory = mock(ToolFactory.class);
        Tool tool = mock(Tool.class);
        when(this.toolService.getTool(any())).thenReturn(this.buildToolData());
        when(toolFactory.create(any(), any())).thenReturn(tool);
        when(toolFactoryRepository.query(any())).thenReturn(Optional.of(toolFactory));
        when(tool.callByJson(any())).thenReturn("OK");
    }

    @Test
    @DisplayName("当调用执行器时返回正确结果")
    void shouldReturnOK() {
        String uniqueName = "testUniqueName";
        String executeResult = this.service.executeTool(uniqueName, buildJsonArgs());
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
                                .put("order", Collections.singletonList("p1"))
                                .put("required", Collections.singletonList("p1"))
                                .build())
                .put("return", MapBuilder.<String, Object>get().put("type", "string").build())
                .build();
    }

    private ToolData buildToolData() {
        ToolData toolData = new ToolData();
        toolData.setSchema(this.buildSchema());
        return toolData;
    }

    private String buildJsonArgs() {
        Map<String, Object> in = MapBuilder.<String, Object>get().put("p1", "1").build();
        return new String(this.serializer.serialize(in, UTF_8));
    }
}