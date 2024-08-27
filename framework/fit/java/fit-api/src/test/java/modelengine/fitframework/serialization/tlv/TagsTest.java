/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fitframework.serialization.tlv;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 表示 {@link Tags} 的单元测试。
 *
 * @author 季聿阶
 * @since 2024-05-09
 */
@DisplayName("测试 Tags")
public class TagsTest {
    @DisplayName("当获取常量类时，校验通过")
    @Test
    void shouldNotThrowException() {
        // noinspection ResultOfMethodCallIgnored
        Assertions.assertThatNoException().isThrownBy(Tags::getWorkerIdTag);
        // noinspection ResultOfMethodCallIgnored
        Assertions.assertThatNoException().isThrownBy(Tags::getWorkerInstanceIdTag);
        // noinspection ResultOfMethodCallIgnored
        Assertions.assertThatNoException().isThrownBy(Tags::getExceptionPropertiesTag);
    }
}
