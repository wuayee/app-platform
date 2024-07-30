/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.uid;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.huawei.fitframework.annotation.Fit;
import com.huawei.fitframework.test.annotation.FitTestWithJunit;
import com.huawei.fitframework.test.annotation.Mocked;
import com.huawei.jade.app.engine.uid.mapper.WorkerGeneratorMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 表示 {@link WorkerGenerator} 的测试用例。
 *
 * @author 何嘉斌
 * @since 2024-07-29
 */
@FitTestWithJunit(classes = {WorkerGenerator.class})
@DisplayName("测试 WorkerGenerator")
public class WorkerGeneratorTest {
    @Fit
    private WorkerGenerator workerGenerator;

    @Mocked
    private WorkerGeneratorMapper workerGeneratorMapper;

    @BeforeEach
    void setUp() {
        clearInvocations(this.workerGeneratorMapper);
    }

    @Test
    @DisplayName("获取机器ID成功")
    void shouldOkWhenGetWorkerId() {
        doNothing().when(this.workerGeneratorMapper).getWorkerId(any());
        this.workerGenerator.getWorkerId();
        verify(this.workerGeneratorMapper, times(1)).getWorkerId(any());
    }
}
