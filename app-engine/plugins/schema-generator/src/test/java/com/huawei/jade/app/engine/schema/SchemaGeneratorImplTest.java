/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.schema;

import static com.huawei.fitframework.util.IoUtils.content;
import static com.huawei.jade.app.engine.schema.code.SchemaGeneratorRetCode.JSON_INVALID_ERROR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.huawei.fit.serialization.json.jackson.JacksonObjectSerializer;
import com.huawei.fitframework.serialization.ObjectSerializer;
import com.huawei.fitframework.util.MapBuilder;
import com.huawei.fitframework.util.TypeUtils;
import com.huawei.jade.app.engine.schema.exception.JsonInvalidException;
import com.huawei.jade.app.engine.schema.generator.SchemaGenerator;
import com.huawei.jade.app.engine.schema.generator.SchemaGeneratorImpl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * 表示 {@link SchemaGenerator} 的测试用例。
 *
 * @author 兰宇晨
 * @since 2024-08-10
 */
public class SchemaGeneratorImplTest {
    static class ValidateTestCaseProvider implements ArgumentsProvider {
        static final Map<String, String> testCaseMap = MapBuilder.<String, String>get()
                .put("shouldOkWhenGenerateSchema", "/test/generate_schema_success_test_case.json")
                .put("shouldNotOKWhenGenerateSchema", "/test/generate_schema_fail_test_case.json")
                .build();

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws IOException {
            ObjectSerializer serializer = new JacksonObjectSerializer(null, null, null);

            String resourceName = testCaseMap.get(extensionContext.getTestMethod().get().getName());
            String jsonContent = content(SchemaGeneratorImplTest.ValidateTestCaseProvider.class, resourceName);

            List<SchemaGeneratorImplTestCaseParam> testCase = serializer.deserialize(jsonContent,
                    TypeUtils.parameterized(List.class, new Type[] {SchemaGeneratorImplTestCaseParam.class}));

            return testCase.stream().map(test -> {
                return Arguments.of(test.getJson(), test.getSchema());
            });
        }
    }

    @ParameterizedTest
    @ArgumentsSource(SchemaGeneratorImplTest.ValidateTestCaseProvider.class)
    @DisplayName("成功生成数据约束")
    public void shouldOkWhenGenerateSchema(String json, String schema) {
        SchemaGenerator generator = new SchemaGeneratorImpl();
        assertThat(generator.generateSchema(json)).isEqualTo(schema);
    }

    @ParameterizedTest
    @ArgumentsSource(SchemaGeneratorImplTest.ValidateTestCaseProvider.class)
    @DisplayName("无效 Json 导致生成数据约束失败")
    public void shouldNotOKWhenGenerateSchema(String json) {
        SchemaGenerator generator = new SchemaGeneratorImpl();
        JsonInvalidException ex =
                assertThrows(JsonInvalidException.class, () -> generator.generateSchema(json));
        assertThat(ex.getCode()).isEqualTo(JSON_INVALID_ERROR.getCode());
    }
}
