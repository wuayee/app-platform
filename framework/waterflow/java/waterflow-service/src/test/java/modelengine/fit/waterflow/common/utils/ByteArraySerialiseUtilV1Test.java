/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.common.utils;

import com.alibaba.fastjson.JSONObject;

import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.ObjectUtils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 测试自定义二进制序列化和反序列化
 *
 * @author 宋永坦
 * @since 2024/2/26
 */
@ExtendWith(MockitoExtension.class)
class ByteArraySerialiseUtilV1Test {
    private static final String ORIGIN_STRING = "hello world";

    private static final String EXPECT_ENCODE_STRING = "BYTE_BASE64:aGVsbG8gd29ybGQ=";

    private static final String EXPECT_JSON_ENCODE_STRING = '"' + EXPECT_ENCODE_STRING + '"';

    @Nested
    @DisplayName("测试json中二进制的序列化和反序列化")
    class TestSerializeBytes {
        @Test
        @DisplayName("二进制对象应序列化为特定格式的字符串")
        void shouldReturnCorrectStringWhenGivenStringAndBytesSerializeConfig() {
            String jsonString = JSONObject.toJSONString(ORIGIN_STRING.getBytes(StandardCharsets.UTF_8),
                    ByteArraySerialiseUtilV1.getSerializeConfig());
            Assertions.assertEquals(EXPECT_JSON_ENCODE_STRING, jsonString);
        }

        @Test
        @DisplayName("json中二进制编码后的字符串应反序列化为bytes")
        void shouldReturnCorrectBytesWhenGivenStringJsonWithJsonEncodedBytes() {
            Object jsonObject = JSONObject.parse(EXPECT_JSON_ENCODE_STRING);
            Object bytesResult = ByteArraySerialiseUtilV1.deserializeBytesValue(jsonObject);
            Assertions.assertTrue(bytesResult instanceof byte[]);
            Assertions.assertArrayEquals((byte[]) bytesResult, ORIGIN_STRING.getBytes(StandardCharsets.UTF_8));
        }

        @Test
        @DisplayName("嵌套对象中的二进制值应序列化为特定格式的字符串")
        void shouldReturnCorrectStringWhenGivenNestedMapAndBytesSerializeConfig() {
            Map<String, Object> nestedMap = MapBuilder.<String, Object>get()
                    .put("level1", MapBuilder.<String, Object>get()
                            .put("level2", ORIGIN_STRING.getBytes(StandardCharsets.UTF_8))
                            .build())
                    .build();
            String jsonString = JSONObject.toJSONString(nestedMap, ByteArraySerialiseUtilV1.getSerializeConfig());
            Assertions.assertEquals(String.format("{\"level1\":{\"level2\":\"%s\"}}", EXPECT_ENCODE_STRING),
                    jsonString);
        }

        @Test
        @DisplayName("嵌套json中二进制编码后的字符串应反序列化为bytes")
        void shouldReturnCorrectBytesWhenGivenNestedJsonWithJsonEncodedBytes() {
            String nestedJsonString = String.format("{\"level1\":{\"level2\":\"%s\"}}", EXPECT_ENCODE_STRING);
            Object jsonObject = JSONObject.parse(nestedJsonString);
            Object result = ByteArraySerialiseUtilV1.deserializeBytesValue(jsonObject);
            Object level1 = ObjectUtils.<Map<String, Object>>cast(result).get("level1");
            Object level2 = ObjectUtils.<Map<String, Object>>cast(level1).get("level2");
            Assertions.assertArrayEquals(ObjectUtils.cast(level2), ORIGIN_STRING.getBytes(StandardCharsets.UTF_8));
        }

        @Test
        @DisplayName("数组中的二进制值应序列化为特定格式的字符串")
        void shouldReturnCorrectStringWhenGivenArrayAndBytesSerializeConfig() {
            List<Object> arrayList = new ArrayList<>();
            arrayList.add(ORIGIN_STRING.getBytes(StandardCharsets.UTF_8));
            String jsonString = JSONObject.toJSONString(arrayList, ByteArraySerialiseUtilV1.getSerializeConfig());
            Assertions.assertEquals(String.format("[\"%s\"]", EXPECT_ENCODE_STRING), jsonString);
        }

        @Test
        @DisplayName("json数组中二进制编码后的字符串应反序列化为bytes")
        void shouldReturnCorrectBytesWhenGivenJsonArrayWithJsonEncodedBytes() {
            String nestedJsonString = String.format("[\"%s\"]", EXPECT_ENCODE_STRING);
            Object jsonObject = JSONObject.parse(nestedJsonString);
            Object result = ByteArraySerialiseUtilV1.deserializeBytesValue(jsonObject);

            List<Object> jsonArray = ObjectUtils.cast(result);
            Assertions.assertEquals(jsonArray.size(), 1);
            Assertions.assertArrayEquals((byte[]) jsonArray.get(0), ORIGIN_STRING.getBytes(StandardCharsets.UTF_8));
        }
    }
}