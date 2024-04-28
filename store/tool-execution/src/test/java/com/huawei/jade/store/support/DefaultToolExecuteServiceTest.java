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
import com.huawei.fitframework.util.MapBuilder;
import com.huawei.jade.store.Tool;
import com.huawei.jade.store.repository.ItemRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;

/**
 * 表示 {@link DefaultToolExecuteService} 的单元测试。
 *
 * @author 王攀博
 * @since 2024-04-27
 */
@DisplayName("测试 工具的执行 ")
public class DefaultToolExecuteServiceTest {
    private DefaultToolExecuteService service;
    private ItemRepository itemRepository;
    private Tool tool;
    private JacksonObjectSerializer serializer;

    @BeforeEach
    void setup() {
        this.tool = mock(Tool.class);
        this.itemRepository = mock(ItemRepository.class);
        this.serializer = new JacksonObjectSerializer(null, null, null);
        this.service = new DefaultToolExecuteService(this.serializer, this.itemRepository);
    }

    @Test
    @DisplayName("当调用执行器时返回正确结果")
    void shouldReturnOK() {
        // given
        String uniqueName = "testUniqueName";
        when(this.itemRepository.getItem(eq(uniqueName))).thenReturn(Optional.of(this.tool));
        when(this.tool.callByJson(any())).thenReturn("OK");

        // when
        String executeResult = this.service.executeTool(uniqueName, buildJsonArgs());

        // then
        assertThat(executeResult).isEqualTo("OK");
    }

    String buildJsonArgs() {
        Map<String, Object> in = MapBuilder.<String, Object>get().put("p1", "1").build();
        return new String(this.serializer.serialize(in, UTF_8));
    }
}