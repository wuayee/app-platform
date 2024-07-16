/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.carver.tool.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.huawei.fit.serialization.json.jackson.JacksonObjectSerializer;
import com.huawei.fitframework.annotation.Genericable;
import com.huawei.fitframework.annotation.Property;
import com.huawei.fitframework.broker.client.BrokerClient;
import com.huawei.fitframework.broker.client.Invoker;
import com.huawei.fitframework.broker.client.Router;
import com.huawei.fitframework.json.schema.JsonSchemaManager;
import com.huawei.fitframework.serialization.ObjectSerializer;
import com.huawei.fitframework.util.MapBuilder;
import com.huawei.jade.carver.tool.Tool;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 表示 {@link Tool} 的单元测试。
 *
 * @author 季聿阶
 * @since 2024-04-05
 */
@DisplayName("测试 FitMethodFunctionalTool")
public class MethodToolMetadataTest {
    private final ObjectSerializer serializer;
    private final Method testMethod;
    private final String name;
    private final String index;

    private Map<String, Object> toolSchema;
    private Tool tool;

    private Tool.Metadata toolMetadata;

    MethodToolMetadataTest() throws NoSuchMethodException {
        this.testMethod = TestInterface.class.getDeclaredMethod("testMethod", String.class);
        this.serializer = new JacksonObjectSerializer(null, null, null);
        this.name = "test_method_default_implementation_name";
        this.index = "test_method_index";
    }

    @BeforeEach
    void setup() {
        BrokerClient client = mock(BrokerClient.class);
        Router router = mock(Router.class);
        Invoker invoker = mock(Invoker.class);
        when(client.getRouter(eq("t1"), eq(this.testMethod))).thenReturn(router);
        when(router.route(any())).thenReturn(invoker);
        when(invoker.invoke(any())).thenAnswer(invocation -> {
            if (Objects.equals(invocation.getArgument(0), "1")) {
                return "OK";
            } else {
                throw new IllegalStateException("Error");
            }
        });
        this.toolMetadata = Tool.Metadata.fromMethod(this.testMethod);
        FitToolFactory fitToolFactory = new FitToolFactory(client, serializer);
        this.tool = fitToolFactory.create(this.buildInfo(), this.toolMetadata);
        this.toolSchema = this.buildSchema();
    }

    private Tool.Info buildInfo() {
        return Tool.Info.custom()
                .name("test_schema_default_implementation_name")
                .uniqueName("schema-uuid")
                .tags(Collections.singleton("FIT"))
                .description("This is a demo FIT function.")
                .schema(this.buildSchema())
                .runnables(MapBuilder.<String, Object>get()
                        .put("FIT", MapBuilder.<String, Object>get().put("genericableId", "t1").build())
                        .build())
                .build();
    }

    private Map<String, Object> buildSchema() {
        return MapBuilder.<String, Object>get()
                .put("name", "test_method_default_implementation_name")
                .put("index", "test_method_index")
                .put("description", "This is a demo FIT function.")
                .put(SchemaKey.PARAMETERS,
                        MapBuilder.<String, Object>get()
                                .put("type", "object")
                                .put(SchemaKey.PARAMETERS_PROPERTIES,
                                        MapBuilder.<String, Object>get()
                                                .put("p1", MapBuilder.<String, Object>get()
                                                        .put("type", "string")
                                                        .put("default", "This is the first parameter.")
                                                        .build())
                                                .build())
                                .put(SchemaKey.PARAMETERS_ORDER, Collections.singletonList("p1"))
                                .build())
                .put(SchemaKey.RETURN_SCHEMA, MapBuilder.<String, Object>get().put("type", "string").build())
                .build();
    }

    @Test
    @DisplayName("当 FIT 调用成功，返回正确的结果")
    void shouldReturnCorrectResult() {
        Object result = this.tool.execute("1");
        assertThat(result).isEqualTo("OK");
    }

    @Test
    @DisplayName("当 FIT 调用失败，返回错误的结果")
    void shouldReturnIncorrectResult() {
        IllegalStateException cause = catchThrowableOfType(() -> this.tool.execute("2"), IllegalStateException.class);
        assertThat(cause).hasMessage("Error");
    }

    @Test
    @DisplayName("返回正确的参数类型")
    void shouldReturnParameters() {
        List<Type> parameters = this.toolMetadata.parameters();
        assertThat(parameters).containsExactly(String.class);
    }

    @Test
    @DisplayName("返回正确的参数名字")
    void shouldReturnParameterNames() {
        List<String> parameterNames = this.toolMetadata.parameterNames();
        assertThat(parameterNames).containsExactly("P1");
    }

    @Test
    @DisplayName("返回正确的参数序号")
    void shouldReturnParameterIndex() {
        int actual = this.toolMetadata.parameterIndex("P1");
        assertThat(actual).isEqualTo(0);
    }

    @Test
    @DisplayName("返回正确的必须参数名字列表")
    void shouldReturnRequired() {
        List<String> parameterNames = this.toolMetadata.requiredParameterNames();
        assertThat(parameterNames).containsExactly("P1");
    }

    @Test
    @DisplayName("返回正确的返回值类型")
    void shouldReturnReturnType() {
        Map<String, Object> type = this.toolMetadata.returnType();
        assertThat(type).isEqualTo(JsonSchemaManager.create().createSchema(String.class).toJsonObject());
    }

    @Test
    @DisplayName("返回正确的格式规范描述")
    void shouldReturnSchema() {
        Map<String, Object> schema = this.tool.info().schema();
        assertThat(schema).isEqualTo(this.toolSchema);
    }

    interface TestInterface {
        /**
         * 测试方法。
         *
         * @param p1 表示测试参数的 {@link String}。
         * @return 表示测试结果的 {@link String}。
         */
        @Genericable(id = "t1", description = "desc")
        String testMethod(@Property(name = "P1", required = true, defaultValue = "default_value") String p1);
    }
}
