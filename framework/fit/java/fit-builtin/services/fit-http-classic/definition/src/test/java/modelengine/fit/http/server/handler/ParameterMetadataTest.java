/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.http.server.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * 表示 {@link PropertyValueMetadata} 的单元测试。
 *
 * @author 白鹏坤
 * @since 2023-02-21
 */
@DisplayName("测试 ParameterMetadata 类")
class ParameterMetadataTest {
    private final PropertyValueMetadata build = PropertyValueMetadata.builder().build();

    @Test
    @DisplayName("获取 Http 的参数元数据")
    void shouldReturnMetadata() {
        final PropertyValueMetadata parameterMetadata = Mockito.mock(PropertyValueMetadata.class);
        final PropertyValueMetadata.Builder builder = PropertyValueMetadata.builder(parameterMetadata);
        final PropertyValueMetadata metadata = builder.build();
        assertThat(metadata).usingRecursiveComparison().isEqualTo(this.build);
    }
}
