/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.ohscript.external;

import static com.huawei.fitframework.util.ObjectUtils.cast;
import static org.assertj.core.api.Assertions.assertThat;

import com.huawei.fit.http.client.HttpClassicClient;
import com.huawei.fit.http.client.HttpClassicClientFactory;
import com.huawei.fit.http.client.jdk.JdkHttpClassicClientFactory;
import com.huawei.fit.ohscript.script.errors.OhPanic;
import com.huawei.fit.ohscript.script.interpreter.ASTEnv;
import com.huawei.fit.ohscript.script.interpreter.Oh;
import com.huawei.fit.ohscript.script.lexer.Lexer;
import com.huawei.fit.ohscript.script.parser.AST;
import com.huawei.fit.ohscript.script.parser.GrammarBuilder;
import com.huawei.fit.ohscript.script.parser.ParserBuilder;
import com.huawei.fit.ohscript.util.OhScriptReader;
import com.huawei.fit.serialization.json.jackson.JacksonObjectSerializer;
import com.huawei.fit.value.fastjson.FastJsonValueHandler;
import com.huawei.fitframework.serialization.ObjectSerializer;
import com.huawei.fitframework.util.MapBuilder;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.value.ValueFetcher;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;

/**
 * 用于对外发送 Http 请求的单元测试。
 *
 * @author 季聿阶
 * @since 2023-10-19
 */
@Disabled
@DisplayName("测试对外 Http 调用")
public class HttpTest {
    private ParserBuilder parserBuilder;

    private HttpClassicClient httpClient;

    @BeforeAll
    static void setupAll() {
        TestFitRuntime.INSTANCE.start();
    }

    @BeforeEach
    void setup() {
        GrammarBuilder grammarBuilder = new GrammarBuilder();
        Lexer lexer = new Lexer();
        this.parserBuilder = new ParserBuilder(grammarBuilder, lexer);

        ObjectSerializer jsonSerializer = new JacksonObjectSerializer(null, null, null);
        Map<String, ObjectSerializer> serializers = MapBuilder.<String, ObjectSerializer>get()
                .put("json", jsonSerializer)
                .build();
        ValueFetcher valueFetcher = new FastJsonValueHandler();
        HttpClassicClientFactory jdkFactory = new JdkHttpClassicClientFactory(serializers, valueFetcher);
        this.httpClient = jdkFactory.create();
    }

    @AfterEach
    void teardown() {
        this.httpClient = null;
        this.parserBuilder = null;
    }

    @Test
    @DisplayName("通过 Get 方法连续获取对象和文本返回值，获取成功")
    void shouldGetTextSuccessfullyAfterGetJson() throws OhPanic {
        this.parserBuilder.addHttpOh("getJson", "get", "http://localhost:8080/oh/get/json?q1=v3");
        this.parserBuilder.addHttpOh("getText", "get", "http://localhost:8080/oh/get/text?q1=v1&q1=v2");
        String ohScript = OhScriptReader.read("http/get_json_and_get_text.oh");
        AST ast = HttpTest.this.parserBuilder.parseString("", ohScript);
        ASTEnv env = new ASTEnv(ast);
        env.setHttpClient(this.httpClient);
        Oh response = ObjectUtils.cast(env.execute());
        assertThat(response.get("body")).isEqualTo("v1,v2,v3,v4");
        assertThat(response.get("headers")).isNotNull();
    }

    @Nested
    @DisplayName("通过 Get 方法调用")
    class TestGet {
        @Test
        @DisplayName("获取文本返回值成功")
        void shouldGetTextSuccessfully() throws OhPanic {
            HttpTest.this.parserBuilder.addHttpOh("getText", "get", "http://localhost:8080/oh/get/text?q1=v1&q1=v2");
            String ohScript = OhScriptReader.read("http/get_text.oh");
            AST ast = HttpTest.this.parserBuilder.parseString("", ohScript);
            ASTEnv env = new ASTEnv(ast);
            env.setHttpClient(HttpTest.this.httpClient);
            Oh response = ObjectUtils.cast(env.execute());
            assertThat(response.get("body")).isEqualTo("v1,v2,v3");
            assertThat(response.get("headers")).isNotNull();
        }

        @Test
        @DisplayName("获取对象返回值成功")
        void shouldGetJsonSuccessfully() throws OhPanic {
            HttpTest.this.parserBuilder.addHttpOh("getJson", "get", "http://localhost:8080/oh/get/json?q1=v3");
            String ohScript = OhScriptReader.read("http/get_json.oh");
            AST ast = HttpTest.this.parserBuilder.parseString("", ohScript);
            ASTEnv env = new ASTEnv(ast);
            env.setHttpClient(HttpTest.this.httpClient);
            Oh response = ObjectUtils.cast(env.execute());
            Map<String, Object> actualEntity = cast(response.get("body"));
            assertThat(actualEntity).isNotNull().containsEntry("k1", "v3").containsEntry("k2", "v4");
            assertThat(response.get("headers")).isNotNull();
        }
    }

    @Nested
    @DisplayName("通过 Post 方法调用")
    class TestPost {
        @Test
        @DisplayName("提交文本消息体成功")
        void shouldPostTextSuccessfully() throws OhPanic {
            HttpTest.this.parserBuilder.addHttpOh("postText", "post", "http://localhost:8080/oh/post/text");
            String ohScript = OhScriptReader.read("http/post_text.oh");
            AST ast = HttpTest.this.parserBuilder.parseString("", ohScript);
            ASTEnv env = new ASTEnv(ast);
            env.setHttpClient(HttpTest.this.httpClient);
            Oh response = ObjectUtils.cast(env.execute());
            assertThat(response.get("body")).isEqualTo("Hello World");
            assertThat(response.get("headers")).isNotNull();
        }

        @Test
        @DisplayName("提交 Json 消息体成功")
        void shouldPostJsonSuccessfully() throws OhPanic {
            HttpTest.this.parserBuilder.addHttpOh("postJson", "post", "http://localhost:8080/oh/post/json");
            String ohScript = OhScriptReader.read("http/post_json.oh");
            AST ast = HttpTest.this.parserBuilder.parseString("", ohScript);
            ASTEnv env = new ASTEnv(ast);
            env.setHttpClient(HttpTest.this.httpClient);
            Oh response = ObjectUtils.cast(env.execute());
            Map<String, Object> body = cast(response.get("body"));
            assertThat(body).containsEntry("f1", "v1");
            assertThat(body.get("f2")).isNotNull().hasFieldOrPropertyWithValue("k", 1);
            assertThat(body.get("f3")).asList().contains(1, 2, 3);
            assertThat(response.get("headers")).isNotNull();
        }

        @Test
        @DisplayName("提交表单消息体信息成功")
        void shouldPostFormSuccessfully() throws OhPanic {
            HttpTest.this.parserBuilder.addHttpOh("postForm", "post", "http://localhost:8080/oh/post/form");
            String ohScript = OhScriptReader.read("http/post_form.oh");
            AST ast = HttpTest.this.parserBuilder.parseString("", ohScript);
            ASTEnv env = new ASTEnv(ast);
            env.setHttpClient(HttpTest.this.httpClient);

            Oh response = ObjectUtils.cast(env.execute());
            assertThat(response.get("body")).isEqualTo("v1");
            assertThat(response.get("headers")).isNotNull();
        }
    }

    @Nested
    @DisplayName("通过 Put 方法调用")
    class TestPut {
        @Test
        @DisplayName("提交文本消息体成功")
        void shouldPutTextSuccessfully() throws OhPanic {
            HttpTest.this.parserBuilder.addHttpOh("putText", "put", "http://localhost:8080/oh/put/text");
            String ohScript = OhScriptReader.read("http/put_text.oh");
            AST ast = HttpTest.this.parserBuilder.parseString("", ohScript);
            ASTEnv env = new ASTEnv(ast);
            env.setHttpClient(HttpTest.this.httpClient);
            Oh response = ObjectUtils.cast(env.execute());
            assertThat(response.get("body")).isEqualTo("Hello World");
            assertThat(response.get("headers")).isNotNull();
        }

        @Test
        @DisplayName("提交 Json 消息体成功")
        void shouldPutJsonSuccessfully() throws OhPanic {
            HttpTest.this.parserBuilder.addHttpOh("putJson", "put", "http://localhost:8080/oh/put/json");
            String ohScript = OhScriptReader.read("http/put_json.oh");
            AST ast = HttpTest.this.parserBuilder.parseString("", ohScript);
            ASTEnv env = new ASTEnv(ast);
            env.setHttpClient(HttpTest.this.httpClient);
            Oh response = ObjectUtils.cast(env.execute());
            Map<String, Object> body = cast(response.get("body"));
            assertThat(body).containsEntry("f1", "v1");
            assertThat(body.get("f2")).isNotNull().hasFieldOrPropertyWithValue("k", 1);
            assertThat(body.get("f3")).asList().contains(1, 2, 3);
            assertThat(response.get("headers")).isNotNull();
        }

        @Test
        @DisplayName("提交表单消息体信息成功")
        void shouldPutFormSuccessfully() throws OhPanic {
            HttpTest.this.parserBuilder.addHttpOh("putForm", "put", "http://localhost:8080/oh/put/form");
            String ohScript = OhScriptReader.read("http/put_form.oh");
            AST ast = HttpTest.this.parserBuilder.parseString("", ohScript);
            ASTEnv env = new ASTEnv(ast);
            env.setHttpClient(HttpTest.this.httpClient);
            Oh response = ObjectUtils.cast(env.execute());
            assertThat(response.get("body")).isEqualTo("v1");
            assertThat(response.get("headers")).isNotNull();
        }
    }

    @Nested
    @DisplayName("通过 Patch 方法调用")
    class TestPatch {
        @Test
        @DisplayName("提交文本消息体成功")
        void shouldPatchTextSuccessfully() throws OhPanic {
            HttpTest.this.parserBuilder.addHttpOh("patchText", "patch", "http://localhost:8080/oh/patch/text");
            String ohScript = OhScriptReader.read("http/patch_text.oh");
            AST ast = HttpTest.this.parserBuilder.parseString("", ohScript);
            ASTEnv env = new ASTEnv(ast);
            env.setHttpClient(HttpTest.this.httpClient);
            Oh response = ObjectUtils.cast(env.execute());
            assertThat(response.get("body")).isEqualTo("Hello World");
            assertThat(response.get("headers")).isNotNull();
        }

        @Test
        @DisplayName("提交 Json 消息体成功")
        void shouldPatchJsonSuccessfully() throws OhPanic {
            HttpTest.this.parserBuilder.addHttpOh("patchJson", "patch", "http://localhost:8080/oh/patch/json");
            String ohScript = OhScriptReader.read("http/patch_json.oh");
            AST ast = HttpTest.this.parserBuilder.parseString("", ohScript);
            ASTEnv env = new ASTEnv(ast);
            env.setHttpClient(HttpTest.this.httpClient);
            Oh response = ObjectUtils.cast(env.execute());
            Map<String, Object> body = cast(response.get("body"));
            assertThat(body).containsEntry("f1", "v1");
            assertThat(body.get("f2")).isNotNull().hasFieldOrPropertyWithValue("k", 1);
            assertThat(body.get("f3")).asList().contains(1, 2, 3);
            assertThat(response.get("headers")).isNotNull();
        }

        @Test
        @DisplayName("提交表单消息体信息成功")
        void shouldPatchFormSuccessfully() throws OhPanic {
            HttpTest.this.parserBuilder.addHttpOh("patchForm", "patch", "http://localhost:8080/oh/patch/form");
            String ohScript = OhScriptReader.read("http/patch_form.oh");
            AST ast = HttpTest.this.parserBuilder.parseString("", ohScript);
            ASTEnv env = new ASTEnv(ast);
            env.setHttpClient(HttpTest.this.httpClient);
            Oh response = ObjectUtils.cast(env.execute());
            assertThat(response.get("body")).isEqualTo("v1");
            assertThat(response.get("headers")).isNotNull();
        }
    }

    @Nested
    @DisplayName("通过 Delete 方法调用")
    class TestDelete {
        @Test
        @DisplayName("获取文本返回值成功")
        void shouldDeleteTextSuccessfully() throws OhPanic {
            HttpTest.this.parserBuilder.addHttpOh("deleteText", "delete",
                    "http://localhost:8080/oh/delete/text?q1=v1&q1=v2");
            String ohScript = OhScriptReader.read("http/delete_text.oh");
            AST ast = HttpTest.this.parserBuilder.parseString("", ohScript);
            ASTEnv env = new ASTEnv(ast);
            env.setHttpClient(HttpTest.this.httpClient);
            Oh response = ObjectUtils.cast(env.execute());
            assertThat(response.get("body")).isEqualTo("v1,v2,v3");
            assertThat(response.get("headers")).isNotNull();
        }

        @Test
        @DisplayName("获取对象返回值成功")
        void shouldDeleteJsonSuccessfully() throws OhPanic {
            HttpTest.this.parserBuilder.addHttpOh("deleteJson", "delete", "http://localhost:8080/oh/delete/json?q1=v3");
            String ohScript = OhScriptReader.read("http/delete_json.oh");
            AST ast = HttpTest.this.parserBuilder.parseString("", ohScript);
            ASTEnv env = new ASTEnv(ast);
            env.setHttpClient(HttpTest.this.httpClient);
            Oh response = ObjectUtils.cast(env.execute());
            Map<String, Object> actualEntity = cast(response.get("body"));
            assertThat(actualEntity).isNotNull().containsEntry("k1", "v3").containsEntry("k2", "v4");
            assertThat(response.get("headers")).isNotNull();
        }
    }
}
