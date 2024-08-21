/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.schema;

import static modelengine.fitframework.util.IoUtils.content;
import static com.huawei.jade.app.engine.schema.code.SchemaValidatorRetCode.VALIDATE_CONTENT_INVALID_ERROR;
import static com.huawei.jade.app.engine.schema.code.SchemaValidatorRetCode.VALIDATE_SCHEMA_INVALID_ERROR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import modelengine.fit.serialization.json.jackson.JacksonObjectSerializer;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.fitframework.util.MapBuilder;
import modelengine.fitframework.util.TypeUtils;
import com.huawei.jade.app.engine.schema.exception.ContentInvalidException;
import com.huawei.jade.app.engine.schema.exception.SchemaInvalidException;
import com.huawei.jade.app.engine.schema.validator.SchemaValidatorImpl;

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
 * 表示 {@link SchemaValidatorImpl} 的测试用例。
 *
 * @author 兰宇晨
 * @since 2024-07-29
 */
public class SchemaValidatorImplTest {
    static class ValidateTestCaseProvider implements ArgumentsProvider {
        static final Map<String, String> testCaseMap = MapBuilder.<String, String>get()
                .put("shouldOkWhenValidEvalData", "/test/valid_eval_data_and_schema_test_case.json")
                .put("shouldNotOkWhenValidateWithInvalidData", "/test/invalid_eval_data_test_case.json")
                .put("shouldNotOkWhenValidateWithInvalidSchema", "/test/invalid_schema_fail_test_case.json")
                .build();

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws IOException {
            ObjectSerializer serializer = new JacksonObjectSerializer(null, null, null);

            String resourceName = testCaseMap.get(extensionContext.getTestMethod().get().getName());
            String jsonContent = content(SchemaValidatorImplTest.ValidateTestCaseProvider.class, resourceName);

            List<SchemaValidatorImplTestCaseParam> testCase = serializer.deserialize(jsonContent,
                    TypeUtils.parameterized(List.class, new Type[] {SchemaValidatorImplTestCaseParam.class}));

            return testCase.stream().map(test -> {
                return Arguments.of(test.getSchema(), test.getContent());
            });
        }
    }

    @ParameterizedTest
    @ArgumentsSource(SchemaValidatorImplTest.ValidateTestCaseProvider.class)
    @DisplayName("批量校验评估数据成功")
    void shouldOkWhenValidEvalData(String schema, List<String> contents) {
        SchemaValidator validator = new SchemaValidatorImpl();
        validator.validate(schema, contents);
        assertDoesNotThrow(() -> validator.validate(schema, contents));
    }

    @ParameterizedTest
    @ArgumentsSource(SchemaValidatorImplTest.ValidateTestCaseProvider.class)
    @DisplayName("批量校验评估数据失败[数据非法]")
    void shouldNotOkWhenValidateWithInvalidData(String schema, List<String> contents) {
        SchemaValidator validator = new SchemaValidatorImpl();
        ContentInvalidException ex =
                assertThrows(ContentInvalidException.class, () -> validator.validate(schema, contents));
        assertThat(ex.getCode()).isEqualTo(VALIDATE_CONTENT_INVALID_ERROR.getCode());
    }

    @ParameterizedTest
    @ArgumentsSource(SchemaValidatorImplTest.ValidateTestCaseProvider.class)
    @DisplayName("批量校验评估数据失败[Schema非法]")
    void shouldNotOkWhenValidateWithInvalidSchema(String schema, List<String> contents) {
        SchemaValidator validator = new SchemaValidatorImpl();
        SchemaInvalidException ex =
                assertThrows(SchemaInvalidException.class, () -> validator.validate(schema, contents));
        assertThat(ex.getCode()).isEqualTo(VALIDATE_SCHEMA_INVALID_ERROR.getCode());
    }
}
