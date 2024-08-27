/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2024 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.serialization.json.jackson;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fit.serialization.test.person.PersonAlias;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.fitframework.util.MapBuilder;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * {@link JacksonObjectSerializer} 的单元测试。
 *
 * @author 邬涨财
 * @since 2024-01-23
 */
@DisplayName("测试 jackson 的序列化功能")
public class JacksonObjectSerializerTest {
    private final Charset charset = StandardCharsets.UTF_8;
    private ObjectSerializer jsonSerializer;

    @BeforeEach
    void setup() {
        this.jsonSerializer = new JacksonObjectSerializer(null, null, "America/New_York");
    }

    @AfterEach
    void teardown() {
        this.jsonSerializer = null;
    }

    @Nested
    @DisplayName("测试方法：serialize(T object, Charset charset, OutputStream out)")
    class TestSerialize {
        private OutputStream out;

        @BeforeEach
        void setup() {
            this.out = new ByteArrayOutputStream();
        }

        @AfterEach
        void teardown() {
            this.out = null;
        }

        @Test
        @DisplayName("当输入为字符串时，序列化结果为 Json 字符串")
        void givenStringThenResultIsJsonString() {
            String in = "hello";
            JacksonObjectSerializerTest.this.jsonSerializer.serialize(in,
                    JacksonObjectSerializerTest.this.charset,
                    this.out);
            String actual = this.out.toString();
            assertThat(actual).isEqualTo("\"hello\"");
        }

        @Test
        @DisplayName("当输入为 LocalDateTime 类型时，序列化结果为 Json 字符串")
        void givenLocalDateTimeThenResultIsJsonString() {
            LocalDateTime in = LocalDateTime.of(2024, 1, 1, 0, 0);
            JacksonObjectSerializerTest.this.jsonSerializer.serialize(in,
                    JacksonObjectSerializerTest.this.charset,
                    this.out);
            String actual = this.out.toString();
            assertThat(actual).isEqualTo("\"2024-01-01 00:00:00\"");
        }

        @Test
        @DisplayName("当输入为 LocalDate 类型时，序列化结果为 Json 字符串")
        void givenLocalDateThenResultIsJsonString() {
            LocalDate in = LocalDate.of(2024, 1, 1);
            JacksonObjectSerializerTest.this.jsonSerializer.serialize(in,
                    JacksonObjectSerializerTest.this.charset,
                    this.out);
            String actual = this.out.toString();
            assertThat(actual).isEqualTo("\"2024-01-01\"");
        }

        @Test
        @DisplayName("当输入为 ZonedDateTime 类型时，序列化结果为 Json 字符串")
        void givenZonedDateTimeThenResultIsJsonString() {
            LocalDateTime localDateTime = LocalDateTime.of(2024, 1, 1, 0, 0);
            ZonedDateTime local = localDateTime.atZone(ZoneId.of("Asia/Shanghai"));
            ZonedDateTime in = local.withZoneSameInstant(ZoneId.of("America/New_York"));
            JacksonObjectSerializerTest.this.jsonSerializer.serialize(in,
                    JacksonObjectSerializerTest.this.charset,
                    this.out);
            String actual = this.out.toString();
            assertThat(actual).isEqualTo("\"2023-12-31 11:00:00\"");
        }
    }

    @Nested
    @DisplayName("测试方法：deserialize(InputStream in, Charset charset, Class<T> objectClass)")
    class TestDeserialize {
        @Test
        @DisplayName("当输入为 Map 序列化后的结果时，反序列化为 Map")
        void givenMapSerializedThenResultIsMap() {
            InputStream in =
                    this.constructInputStream(MapBuilder.<String, Object>get().put("k1", "v1").put("k2", 2).build());
            Object actual = JacksonObjectSerializerTest.this.jsonSerializer.deserialize(in,
                    JacksonObjectSerializerTest.this.charset,
                    Object.class);
            assertThat(actual).isInstanceOf(Map.class)
                    .hasFieldOrPropertyWithValue("k1", "v1")
                    .hasFieldOrPropertyWithValue("k2", 2);
        }

        @Test
        @DisplayName("当输入为 List 序列化后的结果时，反序列化为 List")
        void givenListSerializedThenResultIsList() {
            InputStream in = this.constructInputStream(Arrays.asList(1, 2));
            Object actual = JacksonObjectSerializerTest.this.jsonSerializer.deserialize(in,
                    JacksonObjectSerializerTest.this.charset,
                    Object.class);
            assertThat(actual).isInstanceOf(List.class).asList().containsSequence(1, 2);
        }

        @Test
        @DisplayName("当输入为 LocalDateTime 序列化后的结果时，反序列化为 LocalDateTime")
        void givenLocalDateTimeSerializedThenResultIsLocalDateTime() {
            LocalDateTime expected = LocalDateTime.of(2024, 1, 1, 0, 0);
            InputStream in = this.constructInputStream(expected);
            LocalDateTime actual = JacksonObjectSerializerTest.this.jsonSerializer.deserialize(in,
                    JacksonObjectSerializerTest.this.charset,
                    LocalDateTime.class);
            assertThat(actual).isEqualTo(expected);
        }

        @Test
        @DisplayName("当输入为 LocalDate 序列化后的结果时，反序列化为 LocalDate")
        void givenLocalDateSerializedThenResultIsLocalDateTime() {
            LocalDate expected = LocalDate.of(2024, 1, 1);
            InputStream in = this.constructInputStream(expected);
            LocalDate actual = JacksonObjectSerializerTest.this.jsonSerializer.deserialize(in,
                    JacksonObjectSerializerTest.this.charset,
                    LocalDate.class);
            assertThat(actual).isEqualTo(expected);
        }

        @Test
        @DisplayName("当输入为 ZonedDateTime 序列化后的结果时，反序列化为 ZonedDateTime")
        void givenZonedDateTimeSerializedThenResultIsLocalDateTime() {
            LocalDateTime localDateTime = LocalDateTime.of(2024, 1, 1, 0, 0);
            ZonedDateTime local = localDateTime.atZone(ZoneId.of("Asia/Shanghai"));
            ZonedDateTime expected = local.withZoneSameInstant(ZoneId.of("America/New_York"));
            InputStream in = this.constructInputStream(expected);
            ZonedDateTime actual = JacksonObjectSerializerTest.this.jsonSerializer.deserialize(in,
                    JacksonObjectSerializerTest.this.charset,
                    ZonedDateTime.class);
            assertThat(actual).isEqualTo(expected);
        }

        private InputStream constructInputStream(Object obj) {
            byte[] serialized = JacksonObjectSerializerTest.this.jsonSerializer.serialize(obj,
                    JacksonObjectSerializerTest.this.charset);
            return new ByteArrayInputStream(serialized);
        }
    }

    @Test
    @DisplayName("当输入为字符串时，反序列化为字符串")
    void givenStringThenResultIsString() {
        String expected = "123";
        String serialize = JacksonObjectSerializerTest.this.jsonSerializer.serialize(expected);
        Object actual = JacksonObjectSerializerTest.this.jsonSerializer.deserialize(serialize, String.class);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("当存在别名时，反序列化成功")
    void givenAliasJsonThenDeserializeOk() {
        String json = "{\"first_name\":\"foo\"}";
        PersonAlias personAlias = JacksonObjectSerializerTest.this.jsonSerializer.deserialize(json, PersonAlias.class);
        assertThat(personAlias.firstName()).isEqualTo("foo");
    }

    @Test
    @DisplayName("当存在别名时，序列化成功")
    void givenAliasObjectThenSerializeOk() {
        String expect = "{\"lastName\":null,\"first_name\":\"foo\",\"person_name\":null}";
        PersonAlias personAlias = new PersonAlias();
        personAlias.firstName("foo");
        assertThat(JacksonObjectSerializerTest.this.jsonSerializer.serialize(personAlias)).isEqualTo(expect);
    }

    @Test
    @DisplayName("当使用原生注解，存在别名时，反序列化成功")
    void givenJsonPropertyThenDeserializeOriginOk() {
        String json = "{\"lastName\":\"bar\"}";
        PersonAlias personAlias = JacksonObjectSerializerTest.this.jsonSerializer.deserialize(json, PersonAlias.class);
        assertThat(personAlias.lastName()).isEqualTo("bar");
    }

    @Test
    @DisplayName("当使用原生注解，存在别名时，反序列化成功")
    void givenJsonPropertyThenSerializeOriginOk() {
        String expect = "{\"lastName\":\"bar\",\"first_name\":null,\"person_name\":null}";
        PersonAlias personAlias = new PersonAlias();
        personAlias.lastName("bar");
        assertThat(JacksonObjectSerializerTest.this.jsonSerializer.serialize(personAlias)).isEqualTo(expect);
    }
}
