/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.data.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.huawei.fitframework.util.ThreadUtils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 表示 {@link DefaultDataRepository} 的单元测试。
 *
 * @author 季聿阶
 * @since 2024-04-04
 */
@DisplayName("测试 DefaultDataRepository")
public class DefaultDataRepositoryTest {
    @Test
    @DisplayName("超过超时时间，缓存会被删除")
    void shouldDelete() {
        DefaultDataRepository repository = new DefaultDataRepository(1, 0, 1, "", 0, false);
        repository.save("id", "OK");
        ThreadUtils.sleep(3000);
        String id = repository.getString("id");
        assertThat(id).isNull();
    }

    @Test
    @DisplayName("未超过超时时间，缓存不会被删除")
    void shouldNotDelete() {
        DefaultDataRepository repository = new DefaultDataRepository(10, 0, 1, "", 0, false);
        repository.save("id", "OK");
        ThreadUtils.sleep(3000);
        String id = repository.getString("id");
        assertThat(id).isEqualTo("OK");
    }
}
