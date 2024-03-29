/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2024. All rights reserved.
 */

package com.huawei.fit.serialization.json.jackson;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import com.huawei.fit.serialization.MessageSerializer;
import com.huawei.fit.serialization.test.box.Box;
import com.huawei.fitframework.util.MapBuilder;
import com.huawei.fitframework.util.ObjectUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;

/**
 * 为 {@link JacksonMessageSerializer} 提供单元测试。
 *
 * @author 梁济时 l00815032
 * @since 2020-11-23
 */
public class JacksonMessageSerializerTest {
    @Test
    void should_use_utf8_charset_when_illegal() {
        Type[] argumentTypes = new Type[] {String.class};
        Object[] values = new Object[] {"用以UTF-8编码的中文"};
        JacksonMessageSerializer jacksonMessageSerializer =
                new JacksonMessageSerializer(new JacksonObjectSerializer(null, null, null));
        byte[] bytes = jacksonMessageSerializer.serializeRequest(argumentTypes, values);
        Object[] arguments = jacksonMessageSerializer.deserializeRequest(argumentTypes, bytes);
        assertArrayEquals(values, arguments);
    }

    @Nested
    @DisplayName("given params are generic")
    class GivenParamsAreGeneric {
        private final MessageSerializer messageSerializer =
                new JacksonMessageSerializer(new JacksonObjectSerializer(null, null, null));

        private Type simpleGenericType;

        /**
         * 测试方法。
         *
         * @param box 测试参数。
         */
        private void testFun1(Box<String> box) {}

        @BeforeEach
        void setup() throws NoSuchMethodException {
            Method method = this.getClass().getDeclaredMethod("testFun1", Box.class);
            this.simpleGenericType = method.getGenericParameterTypes()[0];
        }

        @Nested
        @DisplayName("given 1 Box<String>")
        class GivenSimpleGenericParam {
            @Test
            @DisplayName("given 1 Box<String> when serialize and deserialize request then return correct result")
            void givenSimpleGenericParamWhenSerializeAndDeserializeRequestThenReturnCorrectResult() {
                Box<String> box = Box.<String>builder()
                        .value("hello")
                        .tList(Arrays.asList("s1", "s2"))
                        .tMap(MapBuilder.<String, String>get().put("k", "v").build())
                        .build();

                byte[] bs = GivenParamsAreGeneric.this.messageSerializer.serializeRequest(new Type[] {
                        GivenParamsAreGeneric.this.simpleGenericType
                }, new Object[] {box});
                assertThat(bs).isNotEmpty();
                Object[] params = GivenParamsAreGeneric.this.messageSerializer.deserializeRequest(new Type[] {
                        GivenParamsAreGeneric.this.simpleGenericType
                }, bs);
                assertThat(params).isNotEmpty().hasSize(1);
                assertThat(params[0]).isNotNull();
                Box<String> actualBox = ObjectUtils.cast(params[0]);
                assertThat(actualBox.getValue()).isEqualTo("hello");
                assertThat(actualBox.getTList()).hasSize(2).contains("s1", "s2");
                assertThat(actualBox.getTMap()).hasSize(1).containsEntry("k", "v");
            }

            @Test
            @DisplayName("given 1 Box<String> when serialize and deserialize response then return correct result")
            void givenSimpleGenericParamWhenSerializeAndDeserializeResponseThenReturnCorrectResult() {
                Box<String> box = Box.<String>builder()
                        .value("hello")
                        .tList(Arrays.asList("s1", "s2"))
                        .tMap(MapBuilder.<String, String>get().put("k", "v").build())
                        .build();

                Type type = GivenParamsAreGeneric.this.simpleGenericType;
                byte[] bs = GivenParamsAreGeneric.this.messageSerializer.serializeResponse(type, box);
                assertThat(bs).isNotEmpty();
                Box<String> actualBox = GivenParamsAreGeneric.this.messageSerializer.deserializeResponse(type, bs);
                assertThat(actualBox).isNotNull();
                assertThat(actualBox.getValue()).isEqualTo("hello");
                assertThat(actualBox.getTList()).hasSize(2).contains("s1", "s2");
                assertThat(actualBox.getTMap()).hasSize(1).containsEntry("k", "v");
            }
        }

        @Nested
        @DisplayName("given 1 Box<Box<Integer>>")
        class GivenComplexGenericParam {
            @Test
            @DisplayName("given 1 Box<Box<String>> when serialize and deserialize request then return correct result")
            void givenComplexGenericParamWhenSerializeAndDeserializeRequestThenReturnCorrectResult() {}
        }
    }
}
