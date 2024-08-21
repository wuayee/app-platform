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

import modelengine.fit.serialization.json.jackson.JacksonObjectSerializer;
import modelengine.fitframework.broker.client.BrokerClient;
import modelengine.fitframework.broker.client.Invoker;
import modelengine.fitframework.broker.client.Router;
import modelengine.fitframework.json.schema.JsonSchemaManager;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.fitframework.util.MapBuilder;
import com.huawei.jade.carver.tool.Tool;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 表示 {@link Tool} 的单元测试。
 *
 * @author 季聿阶
 * @since 2024-04-06
 */
@DisplayName("测试 FitJsonSchemaFunctionalTool")
public class SchemaToolMetadataTest {
    private Map<String, Object> toolSchema;
    private Tool tool;

    private Tool.Metadata toolMetadata;

    @BeforeEach
    void setup() {
        Router router = mock(Router.class);
        Invoker invoker = mock(Invoker.class);
        BrokerClient client = mock(BrokerClient.class);
        when(client.getRouter(eq("t1"))).thenReturn(router);
        when(router.route(any())).thenReturn(invoker);
        when(invoker.communicationType(any())).thenReturn(invoker);
        when(invoker.invoke(any())).thenAnswer(invocation -> {
            if (Objects.equals(invocation.getArgument(0), "1")) {
                return "OK";
            } else {
                throw new IllegalStateException("Error");
            }
        });

        this.toolSchema = buildSchema();
        this.toolMetadata = Tool.Metadata.fromSchema(this.toolSchema);
        ObjectSerializer serializer = new JacksonObjectSerializer(null, null, null);
        FitToolFactory fitToolFactory = new FitToolFactory(client, serializer);
        this.tool = fitToolFactory.create(this.buildInfo(), this.toolMetadata);
    }

    Tool.Info buildInfo() {
        return Tool.Info.custom()
                .name("test_schema_default_implementation_name")
                .uniqueName("schema-uuid")
                .tags(Collections.singleton("FIT"))
                .schema(buildSchema())
                .runnables(MapBuilder.<String, Object>get()
                        .put("FIT", MapBuilder.<String, Object>get().put("genericableId", "t1").build())
                        .build())
                .build();
    }

    Map<String, Object> buildSchema() {
        return MapBuilder.<String, Object>get()
                .put("name", "test_schema_default_implementation_name")
                .put("index", "test_schema_index")
                .put("description", "This is a demo FIT function.")
                .put(SchemaKey.PARAMETERS,
                        MapBuilder.<String, Object>get()
                                .put("type", "object")
                                .put(SchemaKey.PARAMETERS_PROPERTIES,
                                        MapBuilder.<String, Object>get()
                                                .put("p1",
                                                        MapBuilder.<String, Object>get()
                                                                .put("type", "string")
                                                                .put("description", "This is the first parameter.")
                                                                .build())
                                                .put("extraP1",
                                                        MapBuilder.<String, Object>get()
                                                                .put("type", "string")
                                                                .put("description",
                                                                        "This is the first extra parameter.")
                                                                .build())
                                                .build())
                                .put(SchemaKey.PARAMETERS_REQUIRED, Collections.singletonList("p1"))
                                .build())
                .put(SchemaKey.PARAMETERS_ORDER, Collections.singletonList("p1"))
                .put(SchemaKey.PARAMETERS_EXTENSIONS,
                        MapBuilder.<String, Object>get()
                                .put(SchemaKey.CONFIG_PARAMETERS, Collections.singletonList("extraP1"))
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
        assertThat(parameters).containsExactly(String.class, String.class);
    }

    @Test
    @DisplayName("返回正确的参数名字")
    void shouldReturnParameterNames() {
        List<String> parameterNames = this.toolMetadata.parameterNames();
        assertThat(parameterNames).containsExactly("p1", "extraP1");
    }

    @Test
    @DisplayName("返回正确的参数序号")
    void shouldReturnParameterIndex() {
        int actual = this.toolMetadata.parameterIndex("p1");
        assertThat(actual).isEqualTo(0);

        actual = this.toolMetadata.parameterIndex("extraP1");
        assertThat(actual).isEqualTo(1);
    }

    @Test
    @DisplayName("返回正确的必须参数名字列表")
    void shouldReturnRequired() {
        List<String> parameterNames = this.toolMetadata.requiredParameterNames();
        assertThat(parameterNames).containsExactly("p1");
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

    @Test
    @DisplayName("给定的order值为空，默认使用properties中参数的顺序")
    void givenEmptyOrderThenReturnPropertiesKeysInOrder() {
        Map<String, Object> map = new HashMap<>();
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("b", MapBuilder.<String, Object>get().put("type", "string").build());
        parameters.put("c", MapBuilder.<String, Object>get().put("type", "string").build());
        parameters.put("d", MapBuilder.<String, Object>get().put("type", "string").build());
        parameters.put("a", MapBuilder.<String, Object>get().put("type", "string").build());
        map.put(SchemaKey.PARAMETERS,
                MapBuilder.<String, Object>get()
                        .put("type", "object")
                        .put(SchemaKey.PARAMETERS_PROPERTIES, parameters)
                        .put(SchemaKey.PARAMETERS_REQUIRED, Collections.singletonList("p1"))
                        .build());
        this.toolMetadata = Tool.Metadata.fromSchema(map);
        List<String> parameterNames = this.toolMetadata.parameterNames();
        assertThat(parameterNames).containsExactly("b", "c", "d", "a");
    }
}
