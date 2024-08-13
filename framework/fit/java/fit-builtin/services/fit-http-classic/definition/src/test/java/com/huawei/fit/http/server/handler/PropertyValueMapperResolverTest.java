/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.http.server.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.huawei.fit.http.server.HttpClassicServerRequest;
import com.huawei.fit.http.server.handler.support.UniqueSourcePropertyValueMapper;
import com.huawei.fitframework.value.PropertyValue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import javax.swing.text.html.parser.Entity;

/**
 * 表示 {@link PropertyValueMapperResolver} 的单元测试。
 *
 * @author 白鹏坤
 * @since 2023-02-21
 */
@DisplayName("测试 PropertyValueMapperResolver 类")
class PropertyValueMapperResolverTest {
    private final PropertyValueMapperResolver mapperResolver = PropertyValueMapperResolver.defaultResolver();

    @Test
    @DisplayName("提供默认的属性值映射解析器可以解析的参数，返回对应的属性值映射解析器")
    void givenCanResolvedParameterThenReturnResolver() {
        final PropertyValue propertyValue = mock(PropertyValue.class);
        when(propertyValue.getParameterizedType()).thenAnswer(ans -> HttpClassicServerRequest.class);
        final Optional<PropertyValueMapper> resolve = this.mapperResolver.resolve(propertyValue);
        assertThat(resolve).isPresent().get().isInstanceOf(UniqueSourcePropertyValueMapper.class);
    }

    @Test
    @DisplayName("提供默认的属性值映射解析器不可以解析的参数，返回 Optional 的空对象")
    void givenCanNotResolvedParameterThenReturnEmpty() {
        final PropertyValue propertyValue = mock(PropertyValue.class);
        when(propertyValue.getType()).thenAnswer(ans -> Entity.class);
        final Optional<PropertyValueMapper> resolve = this.mapperResolver.resolve(propertyValue);
        assertThat(resolve).isNotPresent();
    }
}
