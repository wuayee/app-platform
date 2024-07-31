/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.manager.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.huawei.fitframework.test.annotation.FitTestWithJunit;
import com.huawei.fitframework.test.annotation.Mocked;
import com.huawei.jade.app.engine.eval.manager.EvalDatasetVersionManager;
import com.huawei.jade.app.engine.uid.UidGenerator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 表示 {@link EvalVersionManagerImpl} 的测试用例。
 *
 * @author 何嘉斌
 * @since 2024-07-24
 */
@FitTestWithJunit(classes = {EvalVersionManagerImpl.class})
@DisplayName("测试 EvalVersionManagerImpl")
public class EvalVersionManagerImplTest {
    @Mocked
    private UidGenerator generator;

    @Test
    @DisplayName("获取新版本成功")
    void shouldOkWhenApplyVersion() {
        when(this.generator.getUid()).thenReturn(0L, 1L, 2L);
        for (int i = 0; i < 3; i++) {
            EvalDatasetVersionManager versionManager = new EvalVersionManagerImpl(generator);
            assertThat(versionManager.applyVersion()).isEqualTo(i);
        }
    }
}
