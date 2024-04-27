/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.support;

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
import com.huawei.fitframework.util.MapBuilder;
import com.huawei.jade.store.repository.ToolFactoryRepository;
import com.huawei.jade.store.service.ItemDto;
import com.huawei.jade.store.service.ItemService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * 表示 {@link DefaultToolExecuteService} 的单元测试。
 *
 * @author 王攀博
 * @since 2024-04-27
 */
@DisplayName("测试 工具的执行 ")
public class DefaultToolExecuteServiceTest {
    private DefaultToolExecuteService service;
    private BrokerClient client;
    private ItemService itemService;
    private JacksonObjectSerializer serializer;
    private Router router;
    private Invoker invoker;
    private final ToolFactoryRepository toolFactoryRepository;

    DefaultToolExecuteServiceTest() {
        this.toolFactoryRepository = new DefaultToolFactoryRepo();
    }

    @BeforeEach
    void setup() {
        this.router = mock(Router.class);
        this.invoker = mock(Invoker.class);
        this.itemService = mock(ItemService.class);
        this.client = mock(BrokerClient.class);
        this.serializer = new JacksonObjectSerializer(null, null, null);
        this.service = new DefaultToolExecuteService(this.itemService,
                this.client,
                this.serializer,
                this.toolFactoryRepository);
    }

    @Test
    @DisplayName("当调用执行器时返回正确结果")
    void shouldReturnOK() {
        // given
        String uniqueName = "testUniqueName";
        ItemDto itemDto = this.buildDto();
        String jsonArgs = this.buildJsonArgs();

        when(itemService.getItem(eq(uniqueName))).thenReturn(itemDto);
        when(client.getRouter(eq("t1"))).thenReturn(this.router);
        when(router.route(any())).thenReturn(invoker);
        when(invoker.invoke(any())).thenAnswer(invocation -> {
            if (Objects.equals(invocation.getArgument(0), "1")) {
                return "OK";
            } else {
                throw new IllegalStateException("Error");
            }
        });

        // when
        String executeResult = this.service.executeTool(uniqueName, jsonArgs);

        // then
        assertThat(executeResult).isEqualTo(new String(this.serializer.serialize("OK", UTF_8)));
    }

    ItemDto buildDto() {
        return new ItemDto().setCategory("Tool")
                .setGroup("t1")
                .setName("test_tool_exe_name")
                .setUniqueName("uuid")
                .setTags(Collections.singleton("FIT"))
                .setDescription("This is a demo FIT function.")
                .setSchema(buildSchema());
    }

    Map<String, Object> buildSchema() {
        return MapBuilder.<String, Object>get()
                .put("name", "test_tool_exe_name")
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
                .put("tags", "FIT")
                .build();
    }

    String buildJsonArgs() {
        Map<String, Object> in = MapBuilder.<String, Object>get().put("p1", "1").build();
        return new String(this.serializer.serialize(in, UTF_8));
    }
}