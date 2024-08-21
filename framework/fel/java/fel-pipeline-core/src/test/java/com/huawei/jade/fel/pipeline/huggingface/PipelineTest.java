/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.fel.pipeline.huggingface;

import static modelengine.fitframework.util.IoUtils.content;
import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fit.serialization.json.jackson.JacksonObjectSerializer;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.fitframework.util.ObjectUtils;
import modelengine.fitframework.util.TypeUtils;
import com.huawei.jade.fel.pipeline.Pipeline;
import com.huawei.jade.fel.service.pipeline.HuggingFacePipelineService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Stream;

/**
 * 表示 huggingface pipeline 的单元测试。
 *
 * @author 易文渊
 * @since 2024-06-07
 */
@DisplayName("测试 huggingface pipeline")
public class PipelineTest {
    static class TestCaseProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws IOException {
            ObjectSerializer serializer = new JacksonObjectSerializer(null, null, null);

            String resourceName = "/test_case.json";
            String jsonContent = content(TestCaseProvider.class, resourceName);

            List<PipelineTestCase> testCase = serializer.deserialize(jsonContent,
                    TypeUtils.parameterized(List.class, new Type[] {PipelineTestCase.class}));

            return testCase.stream().map(test -> {
                PipelineTask task = PipelineTask.get(test.getTask());
                return Arguments.of(task.getId(),
                        test.getModel(),
                        ObjectUtils.toCustomObject(test.getInput(), task.getInputType()),
                        ObjectUtils.toCustomObject(test.getOutput(), task.getOutputType()));
            });
        }
    }

    @ParameterizedTest
    @ArgumentsSource(TestCaseProvider.class)
    @DisplayName("测试各种输入参数和输出参数，符合预期")
    void shouldReturnOk(String task, String model, Object input, Object output) {
        HuggingFacePipelineService service = (t, m, args) -> {
            assertThat(t).isEqualTo(task);
            assertThat(m).isEqualTo(model);
            assertThat(args).isEqualTo(ObjectUtils.toJavaObject(input));
            return output;
        };
        Pipeline pipeline = PipelineFactory.create(task, model, service);
        assertThat(pipeline.apply(input)).isEqualTo(output);
    }
}