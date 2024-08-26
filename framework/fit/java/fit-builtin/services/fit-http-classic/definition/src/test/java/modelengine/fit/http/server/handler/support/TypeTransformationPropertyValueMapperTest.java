/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
 */

package modelengine.fit.http.server.handler.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fit.http.annotation.DefaultValue;
import modelengine.fit.http.server.handler.MockHttpClassicServerRequest;
import modelengine.fit.http.server.handler.PropertyValueMapper;
import modelengine.fit.http.server.handler.RequestMappingException;
import modelengine.fit.http.server.handler.SourceFetcher;
import modelengine.fit.http.server.support.DefaultHttpClassicServerRequest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Parameter;

/**
 * 表示 {@link TypeTransformationPropertyValueMapper} 的单元测试。
 *
 * @author 白鹏坤
 * @since 2023-02-21
 */
@DisplayName("测试 TypeTransformationParameterMapper 类")
class TypeTransformationPropertyValueMapperTest {
    private final DefaultHttpClassicServerRequest request = new MockHttpClassicServerRequest().getRequest();

    @Test
    @DisplayName("当提供处理器不能处理的参数，类型是字符串，默认值不为空，返回默认值")
    void givenCanNotResolvedParameterAndTypeIsStringAndNotRequireThenReturnDefault() {
        TypeTransformationPropertyValueMapper typeTransformationHttpMapper =
                this.getTypeTransformationParameterMapper(String.class, "default");
        final Object value = typeTransformationHttpMapper.map(this.request, null, null);
        assertThat(value).isEqualTo("default");
    }

    @Test
    @DisplayName("当提供处理器不能处理的参数，类型是字符串，默认值为空字符串，抛出异常")
    void givenCanNotResolvedParameterAndTypeIsStringAndNotRequireThenReturnDefault1() {
        TypeTransformationPropertyValueMapper typeTransformationHttpMapper =
                this.getTypeTransformationParameterMapper(String.class, DefaultValue.VALUE);
        assertThatThrownBy(() -> typeTransformationHttpMapper.map(this.request, null, null)).isInstanceOf(
                RequestMappingException.class);
    }

    @Test
    @DisplayName("当提供处理器不能处理的参数，类型是布尔，默认值不为空，抛出异常")
    void givenCanNotResolvedParameterAndTypeIsStringAndRequireThenReturnDefault() {
        TypeTransformationPropertyValueMapper typeTransformationHttpMapper =
                this.getTypeTransformationParameterMapper(boolean.class, "default");
        assertThatThrownBy(() -> typeTransformationHttpMapper.map(this.request, null, null)).isInstanceOf(
                IllegalStateException.class);
    }

    private TypeTransformationPropertyValueMapper getTypeTransformationParameterMapper(Class<?> clazz,
            Object defaultValue) {
        SourceFetcher headerFetcher = new HeaderFetcher("k1");
        PropertyValueMapper mapper = new UniqueSourcePropertyValueMapper(headerFetcher, false);
        final Parameter parameter = mock(Parameter.class);
        when(parameter.getParameterizedType()).thenAnswer(ans -> clazz);
        return new TypeTransformationPropertyValueMapper(mapper, parameter.getParameterizedType(), true, defaultValue);
    }
}
