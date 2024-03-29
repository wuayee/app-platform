/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
 */

package com.huawei.fit.http.server.handler.parameter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.huawei.fit.http.annotation.RequestParam;
import com.huawei.fit.http.annotation.RequestQuery;
import com.huawei.fit.http.server.handler.PropertyValueMapper;
import com.huawei.fitframework.ioc.annotation.AnnotationMetadata;
import com.huawei.fitframework.ioc.annotation.AnnotationMetadataResolver;
import com.huawei.fitframework.util.ReflectionUtils;
import com.huawei.fitframework.value.PropertyValue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.Optional;

/**
 * 表示 {@link AbstractRequestParamMapperResolver} 的单元测试。
 *
 * @author bWX1068551
 * @since 2023-02-24
 */
@DisplayName("测试 RequestParamMapperResolver 类")
class RequestParamMapperResolverTest {
    private final AnnotationMetadataResolver annotationResolver = mock(AnnotationMetadataResolver.class);
    private final AbstractRequestParamMapperResolver metadataResolver =
            new RequestQueryMapperResolver(this.annotationResolver);

    @Test
    @DisplayName("通过注解解析器来实例化参数映射器")
    void givenParamThenReturnParameterMapper() {
        final Parameter parameter =
                ReflectionUtils.getDeclaredMethod(HttpParamTest.class, "requestParam", String.class).getParameters()[0];
        final AnnotationMetadata annotations = mock(AnnotationMetadata.class);
        final RequestParam requestParam = parameter.getAnnotation(RequestParam.class);
        when(annotations.getAnnotation(any())).thenReturn(requestParam);
        final Optional<PropertyValueMapper> parameterMapper =
                this.metadataResolver.resolve(PropertyValue.createParameterValue(parameter), annotations);
        assertThat(parameterMapper).isPresent();
    }

    @Test
    @DisplayName("获取需要解析的注解的类型")
    void shouldReturnAnnotation() {
        final Class<? extends Annotation> annotation = this.metadataResolver.getAnnotation();
        assertThat(annotation).isEqualTo(RequestQuery.class);
    }
}
