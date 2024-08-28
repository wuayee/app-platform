/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package modelengine.fitframework.util.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * {@link PropertyKey} 的单元测试。
 *
 * @author 季聿阶
 * @since 2022-01-30
 */
public class PropertyKeyTest {
    @Test
    @DisplayName("Given key is a[0] then it is array")
    void givenKeyIsArrayThenPropertyKeyIsArray() {
        PropertyKey actual = new PropertyKey("a[0]");
        assertThat(actual.getActualKey()).isEqualTo("a");
        assertThat(actual.isArray()).isEqualTo(true);
        assertThat(actual.getArrayIndex()).isEqualTo(0);
    }

    @Test
    @DisplayName("Given key is a then it is not array")
    void givenKeyIsNotArrayThenPropertyKeyIsNotArray() {
        PropertyKey actual = new PropertyKey("a");
        assertThat(actual.getActualKey()).isEqualTo("a");
        assertThat(actual.isArray()).isEqualTo(false);
        assertThat(actual.getArrayIndex()).isEqualTo(-1);
    }

    @Test
    @DisplayName("Given key is null then throw IllegalArgumentException")
    void givenKeyIsNullThenThrowException() {
        IllegalArgumentException exception = catchThrowableOfType(() -> new PropertyKey(null),
                IllegalArgumentException.class);
        assertThat(exception).isNotNull().hasMessage("Property key cannot be blank.");
    }

    @Test
    @DisplayName("Given key is a[-1] then throw IllegalArgumentException")
    void givenKeyIsNotMatchPatternThenThrowException() {
        IllegalArgumentException exception = catchThrowableOfType(() -> new PropertyKey("a[-1]"),
                IllegalArgumentException.class);
        assertThat(exception).isNotNull().hasMessage("Illegal property key pattern. [key=a[-1]]");
    }
}
