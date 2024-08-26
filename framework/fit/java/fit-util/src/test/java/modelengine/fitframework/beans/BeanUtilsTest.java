/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fitframework.beans;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * 表示 {@link BeanUtils} 的单元测试。
 *
 * @author 季聿阶
 * @since 2023-02-07
 */
@DisplayName("测试 BeanUtils 类")
public class BeanUtilsTest {
    @Nested
    @DisplayName("测试方法：copyProperties(Object source, Object target)")
    class TestCopyProperties {
        @Test
        @DisplayName("当来源与目标类型的属性一致时，拷贝属性成功")
        void givenTheSamePropertiesInBothSidesThenCopyCorrectly() {
            Object1 o1 = new Object1();
            o1.setF1("Hello");
            o1.setF2(1);
            Object2 o2 = new Object2();
            BeanUtils.copyProperties(o1, o2);
            assertThat(o2).returns("Hello", Object2::getF1).returns(1, Object2::getF2);
        }
    }
}
