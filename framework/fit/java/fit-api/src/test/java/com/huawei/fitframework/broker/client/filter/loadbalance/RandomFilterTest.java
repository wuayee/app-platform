/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2024. All rights reserved.
 */

package com.huawei.fitframework.broker.client.filter.loadbalance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.huawei.fitframework.broker.FitableMetadata;
import com.huawei.fitframework.broker.GenericableMetadata;
import com.huawei.fitframework.broker.Target;
import com.huawei.fitframework.broker.client.Invoker;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * {@link RandomFilter} 的单元测试。
 *
 * @author 季聿阶 j00559309
 * @since 2022-03-18
 */
@DisplayName("验证随机选择的负载均衡的过滤器")
public class RandomFilterTest {
    private Invoker.Filter filter;
    private FitableMetadata fitable;

    @BeforeEach
    void setup() {
        this.filter = new RandomFilter();
        this.fitable = mock(FitableMetadata.class);
        GenericableMetadata genericable = mock(GenericableMetadata.class);
        when(this.fitable.genericable()).thenReturn(genericable);
        when(genericable.id()).thenReturn("gid");
        when(this.fitable.id()).thenReturn("fid");
    }

    @AfterEach
    void teardown() {
        this.filter = null;
        this.fitable = null;
    }

    @Nested
    @DisplayName("当服务实现为 Null 时")
    class GivenFitableIsNull {
        private final String workerId = "workerId";

        @Test
        @DisplayName("抛出参数异常")
        void throwIllegalArgumentException() {
            IllegalArgumentException exception = catchThrowableOfType(() -> RandomFilterTest.this.filter.filter(
                    RandomFilterTest.this.fitable, this.workerId, null, new HashMap<>()),
                    IllegalArgumentException.class);
            assertThat(exception).isNotNull()
                    .hasMessage("The targets to balance load cannot be null. [genericableId=gid, fitableId=fid]");
        }
    }

    @Nested
    @DisplayName("当服务实现不为 Null 时")
    class GivenFitableIsNotNull {
        @Nested
        @DisplayName("当本地进程唯一标识为空白字符串时")
        class GivenWorkerIsBlank {
            @Test
            @DisplayName("抛出参数异常")
            void throwIllegalArgumentException() {
                IllegalArgumentException exception = catchThrowableOfType(() -> RandomFilterTest.this.filter.filter(
                        RandomFilterTest.this.fitable, null, null, new HashMap<>()), IllegalArgumentException.class);
                assertThat(exception).isNotNull()
                        .hasMessage("The local worker id to balance load cannot be blank. [genericableId=gid, "
                                + "fitableId=fid]");
            }
        }

        @Nested
        @DisplayName("当本地进程唯一标识不为空白字符串时")
        class GivenWorkerIdIsNotBlank {
            private final String workerId = "workerId";

            @Test
            @DisplayName("当待过滤的服务地址列表为 Null 时，抛出参数异常")
            void givenToFilterTargetsIsNullThenThrowIllegalArgumentException() {
                IllegalArgumentException exception = catchThrowableOfType(() -> RandomFilterTest.this.filter.filter(
                        RandomFilterTest.this.fitable, this.workerId, null, new HashMap<>()),
                        IllegalArgumentException.class);
                assertThat(exception).isNotNull()
                        .hasMessage("The targets to balance load cannot be null. [genericableId=gid, fitableId=fid]");
            }

            @Test
            @DisplayName("当待过滤的服务地址列表不为空时，返回的服务地址列表不为空")
            void givenToFilterTargetsNotEmptyThenReturnTheTargetsNotEmpty() {
                List<Target> expected = Arrays.asList(Target.custom().workerId("w1").host("h1").build(),
                        Target.custom().workerId("w2").host("h2").build());
                List<Target> actual = RandomFilterTest.this.filter.filter(RandomFilterTest.this.fitable,
                        this.workerId,
                        expected,
                        new HashMap<>());
                assertThat(actual).isNotNull().hasSize(1);
            }
        }
    }
}
