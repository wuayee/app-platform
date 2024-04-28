/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.builder.store;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.huawei.fit.serialization.json.jackson.JacksonObjectSerializer;
import com.huawei.fitframework.broker.client.BrokerClient;
import com.huawei.fitframework.broker.client.Invoker;
import com.huawei.fitframework.broker.client.Router;
import com.huawei.fitframework.serialization.ObjectSerializer;
import com.huawei.fitframework.util.MapBuilder;
import com.huawei.jade.store.ItemInfo;
import com.huawei.jade.store.Tool;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * 表示 {@link WaterFlowTool} 的单元测试。
 *
 * @author 王攀博
 * @since 2024-04-22
 */
@DisplayName("测试 CustomizeWorkflowTool")
public class WaterFlowToolTest {
    private WaterFlowTool customizeWorkflowTool;
    private ObjectSerializer serializer;
    private BrokerClient client;

    @BeforeEach
    void setup() {
        this.client = mock(BrokerClient.class);
        ItemInfo itemInfo = this.buildItemInfo();
        Tool.Metadata toolMetadata = Tool.Metadata.fromSchema(itemInfo.schema());
        this.serializer = new JacksonObjectSerializer(null, null, null);

        WaterFlowToolFactory waterflowToolFactory = new WaterFlowToolFactory(this.client, this.serializer);
        Tool waterFlowTool = waterflowToolFactory.create(itemInfo, toolMetadata);
        if (waterFlowTool instanceof WaterFlowTool) {
            this.customizeWorkflowTool = (WaterFlowTool) waterFlowTool;
        }
    }

    @Test
    @DisplayName("当调用成功，返回正确的结果")
    void shouldReturnCorrectResultWhenCallByJsonGivenJsonArgs() {
        // given
        Router router = mock(Router.class);
        Invoker invoker = mock(Invoker.class);
        when(this.client.getRouter(eq("t1"))).thenReturn(router);
        when(router.route(any())).thenReturn(invoker);
        when(invoker.invoke(any())).thenAnswer(invocation -> {
            if (Objects.equals(invocation.getArgument(0), "p1_value") && Objects.equals(invocation.getArgument(1),
                    "p2_default_value") && Objects.equals(invocation.getArgument(2), "p3_value")) {
                return "OK";
            } else {
                throw new IllegalStateException("Error");
            }
        });

        Map<String, Object> args = MapBuilder.<String, Object>get().put("p1", "p1_value").put("p3", "p3_value").build();
        String jsonArgs = new String(this.serializer.serialize(args, UTF_8), UTF_8);
        String expectedResult = new String(this.serializer.serialize("OK", UTF_8), UTF_8);

        // when
        Object result = this.customizeWorkflowTool.callByJson(jsonArgs);

        // then
        assertThat(result).isEqualTo(expectedResult);
    }

    ItemInfo buildItemInfo() {
        return ItemInfo.custom()
                .category("Tool")
                .group("t1")
                .name("test_schema_default_implementation_name")
                .uniqueName("customize-water-flow-uuid")
                .tags(Collections.singleton("Customize-Workflow"))
                .description("This is a demo schema tool.")
                .schema(this.buildSchema())
                .build();
    }

    Map<String, Object> buildSchema() {
        return MapBuilder.<String, Object>get()
                .put("group", "t1")
                .put("name", "test_schema_default_implementation_name")
                .put("index", "test_schema_index")
                .put("description", "This is a demo schema tool.")
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
                                                .put("p2",
                                                        MapBuilder.<String, Object>get()
                                                                .put("type", "string")
                                                                .put("default", "p2_default_value")
                                                                .build())
                                                .put("p3",
                                                        MapBuilder.<String, Object>get().put("type", "string").build())
                                                .build())
                                .put("order", Arrays.asList("p1", "p2", "p3"))
                                .put("required", Collections.singletonList("p1"))
                                .build())
                .put("return", MapBuilder.<String, Object>get().put("type", "string").build())
                .put("toolType", "Customize-Workflow")
                .put("manualIntervention", true)
                .put("tag", "Customize-Workflow")
                .build();
    }
}
