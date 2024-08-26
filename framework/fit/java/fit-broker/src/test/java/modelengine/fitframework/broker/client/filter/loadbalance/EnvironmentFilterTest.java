/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fitframework.broker.client.filter.loadbalance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import modelengine.fitframework.broker.FitableMetadata;
import modelengine.fitframework.broker.Genericable;
import modelengine.fitframework.broker.Target;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

/**
 * 表示 {@link EnvironmentFilter} 的单元测试。
 *
 * @author 季聿阶
 * @since 2024-07-14
 */
@DisplayName("测试 EnvironmentFilter")
public class EnvironmentFilterTest {
    private EnvironmentFilter filter;
    private FitableMetadata fitable;

    @BeforeEach
    void setup() {
        this.filter = new EnvironmentFilter("dev");
        this.fitable = mock(FitableMetadata.class);
        Genericable genericable = mock(Genericable.class);
        when(this.fitable.genericable()).thenReturn(genericable);
        when(genericable.id()).thenReturn("g");
        when(this.fitable.id()).thenReturn("f");
    }

    @AfterEach
    void teardown() {
        this.filter = null;
        this.fitable = null;
    }

    @Test
    @DisplayName("可以过滤携带正确环境标的地址")
    void shouldReturnCorrectTargetsWithSpecifiedEnvironment() {
        List<Target> actual = this.filter.filter(this.fitable,
                "local",
                Arrays.asList(Target.custom().environment("dev").build(), Target.custom().environment("test").build()),
                null);
        assertThat(actual).hasSize(1);
    }
}
