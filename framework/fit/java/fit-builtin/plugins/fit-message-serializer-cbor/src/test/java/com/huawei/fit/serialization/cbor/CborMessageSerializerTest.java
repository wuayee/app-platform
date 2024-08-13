/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2024. All rights reserved.
 */

package com.huawei.fit.serialization.cbor;

import static org.assertj.core.api.Assertions.assertThat;

import com.huawei.fit.serialization.MessageSerializer;
import com.huawei.fit.serialization.cbor.test.box.Box;
import com.huawei.fitframework.util.MapBuilder;
import com.huawei.fitframework.util.ObjectUtils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;

/**
 * 为 {@link CborMessageSerializer} 提供单元测试。
 *
 * @author 季聿阶
 * @since 2020-11-23
 */
public class CborMessageSerializerTest {
    private CborMessageSerializer cborMessageSerializer;

    @BeforeEach
    void setup() {
        this.cborMessageSerializer = new CborMessageSerializer(new CborObjectSerializer());
    }

    @AfterEach
    void teardown() {
        this.cborMessageSerializer = null;
    }

    @Test
    @DisplayName("序列化并反序列的结果，等于原来的值")
    void shouldEqualsOrigin() {
        Type[] argumentTypes = new Type[] {String.class};
        Object[] values = new Object[] {"用以UTF-8编码的中文"};
        byte[] bytes = this.cborMessageSerializer.serializeRequest(argumentTypes, values);
        Object[] arguments = this.cborMessageSerializer.deserializeRequest(argumentTypes, bytes);
        assertThat(arguments).isEqualTo(values);
    }

    @Nested
    @DisplayName("给定一个结构体对象")
    class GivenParamsAreCustom {
        private final MessageSerializer messageSerializer = new CborMessageSerializer(new CborObjectSerializer());

        private Type simpleGenericType;

        /**
         * 测试方法。
         *
         * @param box 测试参数。
         */
        private void testFun1(Box box) {}

        @BeforeEach
        void setup() throws NoSuchMethodException {
            Method method = this.getClass().getDeclaredMethod("testFun1", Box.class);
            this.simpleGenericType = method.getGenericParameterTypes()[0];
        }

        @Nested
        @DisplayName("给定参数为 Box")
        class GivenSimpleParam {
            @Test
            @DisplayName("序列化并反序列化参数数组之后，等于原值")
            void shouldEqualsOriginWhenSerializeAndDeserializeRequest() {
                Box box = Box.builder()
                        .value("hello")
                        .tList(Arrays.asList("s1", "s2"))
                        .tMap(MapBuilder.<String, String>get().put("k", "v").build())
                        .build();

                byte[] bs = GivenParamsAreCustom.this.messageSerializer.serializeRequest(new Type[] {
                        GivenParamsAreCustom.this.simpleGenericType
                }, new Object[] {box});
                assertThat(bs).isNotEmpty();
                Object[] params = GivenParamsAreCustom.this.messageSerializer.deserializeRequest(new Type[] {
                        GivenParamsAreCustom.this.simpleGenericType
                }, bs);
                assertThat(params).isNotEmpty().hasSize(1);
                assertThat(params[0]).isNotNull();
                Box actualBox = ObjectUtils.cast(params[0]);
                assertThat(actualBox.getValue()).isEqualTo("hello");
                assertThat(actualBox.getTList()).hasSize(2).contains("s1", "s2");
                assertThat(actualBox.getTMap()).hasSize(1).containsEntry("k", "v");
            }

            @Test
            @DisplayName("序列化并反序列化返回值之后，等于原值")
            void shouldEqualsOriginWhenSerializeAndDeserializeResponse() {
                Box box = Box.<String>builder()
                        .value("hello")
                        .tList(Arrays.asList("s1", "s2"))
                        .tMap(MapBuilder.<String, String>get().put("k", "v").build())
                        .build();

                Type type = GivenParamsAreCustom.this.simpleGenericType;
                byte[] bs = GivenParamsAreCustom.this.messageSerializer.serializeResponse(type, box);
                assertThat(bs).isNotEmpty();
                Box actualBox = GivenParamsAreCustom.this.messageSerializer.deserializeResponse(type, bs);
                assertThat(actualBox).isNotNull();
                assertThat(actualBox.getValue()).isEqualTo("hello");
                assertThat(actualBox.getTList()).hasSize(2).contains("s1", "s2");
                assertThat(actualBox.getTMap()).hasSize(1).containsEntry("k", "v");
            }
        }
    }
}
