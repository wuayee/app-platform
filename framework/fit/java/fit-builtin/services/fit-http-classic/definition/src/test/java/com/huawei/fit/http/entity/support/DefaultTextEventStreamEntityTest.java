/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.http.entity.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.huawei.fit.http.HttpMessage;
import com.huawei.fit.http.entity.TextEvent;
import com.huawei.fit.http.entity.TextEventStreamEntity;
import com.huawei.fitframework.flowable.Choir;
import com.huawei.fitframework.serialization.ObjectSerializer;
import com.huawei.fitframework.util.StringUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;

/**
 * 为 {@link DefaultTextEventStreamEntity} 提供单元测试。
 *
 * @author 易文渊
 * @since 2024-07-16
 */
@DisplayName("测试 DefaultSseEntity 类")
public class DefaultTextEventStreamEntityTest {
    private HttpMessage httpMessage;
    private ObjectSerializer objectSerializer;

    @BeforeEach
    void setup() {
        this.httpMessage = mock(HttpMessage.class);
        this.objectSerializer = mock(ObjectSerializer.class);
        when(this.objectSerializer.serialize(any())).then(invocation -> invocation.getArgument(0).toString());
    }

    private String serialize(TextEventStreamEntity eventStreamEntity) {
        return eventStreamEntity.stream().map(textEvent -> textEvent.serialize(this.objectSerializer))
            .reduce(String::concat).block().orElse(StringUtils.EMPTY);
    }

    @Test
    @DisplayName("测试流式返回数值")
    void shouldReturnIntegerOk() {
        Choir<Integer> stream = Choir.just(1, 2, 3);
        TextEventStreamEntity eventStreamEntity = new DefaultTextEventStreamEntity(this.httpMessage, stream);
        assertThat(this.serialize(eventStreamEntity)).isEqualTo("data:1\n\ndata:2\n\ndata:3\n\n");
    }

    @Test
    @DisplayName("测试流式返回多行字符串")
    void shouldReturnMultiLineOk() {
        Choir<String> stream = Choir.just("1\n2", "3");
        TextEventStreamEntity eventStreamEntity = new DefaultTextEventStreamEntity(this.httpMessage, stream);
        assertThat(this.serialize(eventStreamEntity)).isEqualTo("data:1\ndata:2\n\ndata:3\n\n");
    }

    @Test
    @DisplayName("测试流式返回 TextEvent 数据")
    void shouldReturnTextEventOk() {
        TextEvent textEvent = TextEvent.builder()
            .id("1")
            .event("foo")
            .retry(Duration.ofSeconds(1))
            .comment("this is test")
            .build();
        Choir<TextEvent> stream = Choir.just(textEvent);
        TextEventStreamEntity eventStreamEntity = new DefaultTextEventStreamEntity(this.httpMessage, stream);
        assertThat(this.serialize(eventStreamEntity)).isEqualTo("id:1\nevent:foo\nretry:1000\n:this is test\n\n");
    }
}