/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.huawei.fitframework.annotation.Genericable;
import com.huawei.fitframework.annotation.Property;
import com.huawei.fitframework.broker.client.BrokerClient;
import com.huawei.fitframework.broker.client.Invoker;
import com.huawei.fitframework.broker.client.Router;
import com.huawei.fitframework.util.MapBuilder;
import com.huawei.jade.store.FunctionalTool;

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
 * 表示 {@link FitMethodFunctionalTool} 的单元测试。
 *
 * @author 季聿阶
 * @since 2024-04-05
 */
@DisplayName("测试 FitMethodFunctionalTool")
public class FitMethodFunctionalToolTest {
    private final Method testMethod;

    private FunctionalTool tool;

    FitMethodFunctionalToolTest() throws NoSuchMethodException {
        this.testMethod = TestInterface.class.getDeclaredMethod("testMethod", String.class);
    }

    @BeforeEach
    void setup() {
        BrokerClient client = mock(BrokerClient.class);
        Router router = mock(Router.class);
        Invoker invoker = mock(Invoker.class);
        when(client.getRouter(eq("t1"), eq(this.testMethod))).thenReturn(router);
        when(router.route()).thenReturn(invoker);
        when(invoker.invoke(any())).thenAnswer(invocation -> {
            if (Objects.equals(invocation.getArgument(0), "1")) {
                return "OK";
            } else {
                throw new IllegalStateException("Error");
            }
        });
        this.tool = new FitMethodFunctionalTool(client, this.testMethod);
    }

    @Test
    @DisplayName("当 FIT 调用成功，返回正确的结果")
    void shouldReturnCorrectResult() {
        Object result = this.tool.call("1");
        assertThat(result).isEqualTo("OK");
    }

    @Test
    @DisplayName("当 FIT 调用失败，返回错误的结果")
    void shouldReturnIncorrectResult() {
        IllegalStateException cause = catchThrowableOfType(() -> this.tool.call("2"), IllegalStateException.class);
        assertThat(cause).hasMessage("Error");
    }

    @Test
    @DisplayName("返回正确的参数类型")
    void shouldReturnParameters() {
        List<Type> parameters = this.tool.parameters();
        assertThat(parameters).containsExactly(String.class);
    }

    @Test
    @DisplayName("返回正确的参数名字")
    void shouldReturnParameterNames() {
        List<String> parameterNames = this.tool.parameterNames();
        assertThat(parameterNames).containsExactly("p1");
    }

    @Test
    @DisplayName("返回正确的参数序号")
    void shouldReturnParameterIndex() {
        int actual = this.tool.parameterIndex("p1");
        assertThat(actual).isEqualTo(0);
    }

    @Test
    @DisplayName("返回正确的必须参数名字列表")
    void shouldReturnRequired() {
        List<String> parameterNames = this.tool.requiredParameterNames();
        assertThat(parameterNames).containsExactly("p1");
    }

    @Test
    @DisplayName("返回正确的返回值类型")
    void shouldReturnReturnType() {
        Type type = this.tool.returnType();
        assertThat(type).isEqualTo(String.class);
    }

    @Test
    @DisplayName("返回正确的格式规范描述")
    void shouldReturnSchema() {
        Map<String, Object> schema = this.tool.schema();
        assertThat(schema).containsEntry("name", "t1")
                .containsEntry("description", "desc")
                .containsEntry("type", "function")
                .containsEntry("parameters",
                        MapBuilder.get()
                                .put("type", "object")
                                .put("properties",
                                        MapBuilder.get()
                                                .put("p1", MapBuilder.get().put("type", "string").build())
                                                .build())
                                .put("required", Collections.singletonList("p1"))
                                .build());
    }

    interface TestInterface {
        /**
         * 测试方法。
         *
         * @param p1 表示测试参数的 {@link String}。
         * @return 表示测试结果的 {@link String}。
         */
        @Genericable(id = "t1", description = "desc")
        String testMethod(@Property(required = true) String p1);
    }
}
