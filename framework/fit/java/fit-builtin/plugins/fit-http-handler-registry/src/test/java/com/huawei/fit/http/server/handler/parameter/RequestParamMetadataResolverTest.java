/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
 */

package com.huawei.fit.http.server.handler.parameter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.huawei.fit.http.annotation.RequestParam;
import com.huawei.fit.http.server.handler.PropertyValueMetadata;
import com.huawei.fitframework.ioc.annotation.AnnotationMetadata;
import com.huawei.fitframework.ioc.annotation.AnnotationMetadataResolver;
import com.huawei.fitframework.util.ReflectionUtils;
import com.huawei.fitframework.value.PropertyValue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.List;

/**
 * 表示 {@link RequestParamMetadataResolver} 的单元测试。
 *
 * @author bWX1068551
 * @since 2023-02-24
 */
@DisplayName("测试 RequestParamMetadataResolver 类")
class RequestParamMetadataResolverTest {
    private final AnnotationMetadataResolver annotationResolver = mock(AnnotationMetadataResolver.class);
    private final RequestParamMetadataResolver metadataResolver =
            new RequestParamMetadataResolver(this.annotationResolver);

    @Test
    @DisplayName("当提供参数时，返回参数中的元信息")
    void givenParamThenReturnParameterMetadata() {
        final Parameter parameter =
                ReflectionUtils.getDeclaredMethod(HttpParamTest.class, "requestParam", String.class).getParameters()[0];
        final AnnotationMetadata annotations = mock(AnnotationMetadata.class);
        final RequestParam requestParam = parameter.getAnnotation(RequestParam.class);
        when(annotations.getAnnotation(RequestParam.class)).thenReturn(requestParam);
        final List<PropertyValueMetadata> propertyValueMetadata =
                this.metadataResolver.resolve(PropertyValue.createParameterValue(parameter), annotations);
        assertThat(propertyValueMetadata.size()).isEqualTo(1);
        assertThat(propertyValueMetadata.get(0)).returns("p1", PropertyValueMetadata::name);
    }

    @Test
    @DisplayName("获取需要解析的注解的类型")
    void shouldReturnAnnotation() {
        final Class<? extends Annotation> annotation = this.metadataResolver.getAnnotation();
        assertThat(annotation).isEqualTo(RequestParam.class);
    }
}
