/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.http.call.command;

import com.alibaba.fastjson.JSONObject;

import modelengine.fit.http.HttpMessage;
import modelengine.fit.http.HttpResource;
import modelengine.fit.http.client.HttpClassicClientFactory;
import modelengine.fit.http.entity.Entity;
import modelengine.fit.http.entity.EntitySerializer;
import modelengine.fit.http.entity.ObjectEntity;
import modelengine.fit.http.entity.TextEntity;
import modelengine.fit.http.header.ContentType;
import modelengine.fit.http.header.CookieCollection;
import modelengine.fit.http.protocol.HttpVersion;
import modelengine.fit.http.protocol.MessageHeaders;
import modelengine.fit.http.protocol.MimeType;
import modelengine.fit.jade.aipp.http.call.HttpBody;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.ObjectUtils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 表示 {@link HttpCallCommand} 的测试集。
 *
 * @author 张越
 * @since 2024-12-15
 */
@DisplayName("测试 HttpCallCommand")
public class HttpCallCommandTest {
    @Nested
    @DisplayName("测试 getCompleteUrl 接口")
    class GetCompleteUrl {
        @Test
        @DisplayName("url为空，则返回空字符串")
        void shouldReturnEmptyStringWhenUrlIsEmpty() {
            HttpCallCommand command = new HttpCallCommand();
            String completedUrl = command.getCompleteUrl();
            Assertions.assertTrue(completedUrl.isEmpty());
        }

        @Test
        @DisplayName("路径参数中存在数据不为string")
        void shouldThrowIllegalArgumentExceptionWhenPathArgsIsNotString() {
            HttpCallCommand command = new HttpCallCommand();
            command.setUrl("https://expamples.com/{{aaa}}/app");
            command.setArgs(MapBuilder.<String, Object>get().put("aaa", true).build());
            String url = command.getCompleteUrl();
            Assertions.assertEquals("https://expamples.com/true/app", url);
        }

        @Test
        @DisplayName("路径参数在args中不存在")
        void shouldThrowIllegalArgumentExceptionWhenPathArgsIsNotExists() {
            HttpCallCommand command = new HttpCallCommand();
            command.setUrl("https://expamples.com/{{aaa}}/{{bbb}}/app");
            command.setArgs(MapBuilder.<String, Object>get().put("aaa", "111").build());
            Assertions.assertThrows(IllegalArgumentException.class, command::getCompleteUrl);
        }

        @Test
        @DisplayName("没有请求参数")
        void shouldOkWhenNoPathParams() {
            HttpCallCommand command = new HttpCallCommand();
            command.setUrl("https://expamples.com/{{aaa}}/app");
            command.setArgs(MapBuilder.<String, Object>get().put("aaa", "fit").build());
            String completedUrl = command.getCompleteUrl();
            Assertions.assertEquals("https://expamples.com/fit/app", completedUrl);
        }

        @Test
        @DisplayName("有请求参数")
        void shouldOkWhenHasPathParams() {
            HttpCallCommand command = new HttpCallCommand();
            command.setUrl("https://expamples.com/{{aaa}}/app");
            command.setArgs(MapBuilder.<String, Object>get().put("aaa", "fit").build());
            command.setParams(MapBuilder.<String, String>get(LinkedHashMap::new)
                    .put("name", "zzz")
                    .put("age", "2")
                    .put("sex", "male")
                    .build());
            String completedUrl = command.getCompleteUrl();
            Assertions.assertTrue(completedUrl.contains("name=zzz"));
            Assertions.assertTrue(completedUrl.contains("age=2"));
            Assertions.assertTrue(completedUrl.contains("sex=male"));
        }
    }

    @Nested
    @DisplayName("测试 getEntity 接口")
    class GetEntity {
        @Test
        @DisplayName("body体为JSON")
        void shouldOKWhenBodyIsJson() {
            HttpBody body = new HttpBody();
            body.setType("json");
            body.setData("{\"name\": \"{{name}}\", " + "\"age\": {{age}}, " + "\"isBoy\": {{isBoy}}, "
                    + "\"description\": \"this is xxxxx, example: {{input_example}}\"}");

            HttpCallCommand command = new HttpCallCommand();
            command.setHttpBody(body);
            command.setArgs(MapBuilder.<String, Object>get()
                    .put("name", "zzzz")
                    .put("age", 10)
                    .put("isBoy", true)
                    .put("input_example", "example1")
                    .build());
            Optional<Entity> entityOptional = command.getEntity(new DefaultHttpMessage());
            Assertions.assertTrue(entityOptional.isPresent());
            ObjectEntity<?> entity = ObjectUtils.cast(entityOptional.get());
            JSONObject jsonObject = ObjectUtils.cast(entity.object());
            Assertions.assertEquals("zzzz", jsonObject.get("name"));
            Assertions.assertEquals(10, jsonObject.get("age"));
            Assertions.assertEquals(true, jsonObject.get("isBoy"));
            Assertions.assertEquals("this is xxxxx, example: example1", jsonObject.get("description"));
        }

        @Test
        @DisplayName("body体为text")
        void shouldOKWhenBodyIsText() {
            HttpBody body = new HttpBody();
            body.setType("text");
            body.setData("this is a template text, template is {{template}}");

            HttpCallCommand command = new HttpCallCommand();
            command.setHttpBody(body);
            command.setArgs(MapBuilder.<String, Object>get().put("template", "zzzz").build());
            Optional<Entity> entityOptional = command.getEntity(new DefaultHttpMessage());
            Assertions.assertTrue(entityOptional.isPresent());
            TextEntity entity = ObjectUtils.cast(entityOptional.get());
            Assertions.assertEquals(entity.content(), "this is a template text, template is zzzz");
        }

        private class DefaultHttpMessage implements HttpMessage {
            @Override
            public HttpVersion httpVersion() {
                return null;
            }

            @Override
            public MessageHeaders headers() {
                return null;
            }

            @Override
            public Optional<String> transferEncoding() {
                return Optional.empty();
            }

            @Override
            public boolean isChunked() {
                return false;
            }

            @Override
            public Optional<ContentType> contentType() {
                return Optional.empty();
            }

            @Override
            public int contentLength() {
                return 0;
            }

            @Override
            public CookieCollection cookies() {
                return null;
            }

            @Override
            public Optional<Entity> entity() {
                return Optional.empty();
            }

            @Override
            public EntitySerializer<? extends Entity> entitySerializer() {
                return null;
            }

            @Override
            public Optional<ObjectSerializer> jsonSerializer() {
                return Optional.empty();
            }

            @Override
            public EntitySerializer<? extends Entity> entitySerializer(Type type) {
                return null;
            }

            @Override
            public boolean isCommitted() {
                return false;
            }

            @Override
            public void customEntitySerializer(MimeType mimeType, EntitySerializer<? extends Entity> serializer) {

            }

            @Override
            public void customJsonSerializer(ObjectSerializer objectSerializer) {

            }

            @Override
            public HttpResource httpResource() {
                return null;
            }
        }
    }

    @Nested
    @DisplayName("测试 getConfig 接口")
    class GetConfig {
        @Test
        @DisplayName("timeout为空")
        void shouldOKWhenTimeoutIsNull() {
            HttpCallCommand command = new HttpCallCommand();
            HttpClassicClientFactory.Config config = command.getConfig();
            Assertions.assertEquals(-1, config.connectionRequestTimeout());
            Assertions.assertEquals(-1, config.connectTimeout());
            Assertions.assertEquals(-1, config.socketTimeout());
        }

        @Test
        @DisplayName("timeout为100")
        void shouldOKWhenTimeoutIs100() {
            HttpCallCommand command = new HttpCallCommand();
            command.setTimeout(100);
            HttpClassicClientFactory.Config config = command.getConfig();
            Assertions.assertEquals(100, config.connectionRequestTimeout());
            Assertions.assertEquals(100, config.connectTimeout());
            Assertions.assertEquals(100, config.socketTimeout());
        }
    }

    @Nested
    @DisplayName("测试 getHeaders 接口")
    class GetHeaders {
        @Test
        @DisplayName("header不存在占位符")
        void shouldOKWhenNoPlaceholder() {
            HttpCallCommand command = new HttpCallCommand();
            command.setHeaders(MapBuilder.<String, String>get().put("aaa", "bbb").build());
            Map<String, String> headers = command.getHeaders();
            Assertions.assertEquals("bbb", headers.get("aaa"));
        }

        @Test
        @DisplayName("header中存在占位符")
        void shouldOKWhenHasPlaceHolder() {
            HttpCallCommand command = new HttpCallCommand();
            command.setHeaders(MapBuilder.<String, String>get().put("aa{{name}}a", "bb{{age}}b").build());
            command.setArgs(MapBuilder.<String, Object>get().put("name", "zy").put("age", 10).build());
            Map<String, String> headers = command.getHeaders();
            Assertions.assertEquals("bb10b", headers.get("aazya"));
        }

        @Test
        @DisplayName("header的key为空")
        void shouldOKWhenHeaderKeyIsBlank() {
            HttpCallCommand command = new HttpCallCommand();
            command.setHeaders(MapBuilder.<String, String>get().put("", "bbbb").build());
            Map<String, String> headers = command.getHeaders();
            Assertions.assertEquals("bbbb", headers.get(""));
        }
    }
}