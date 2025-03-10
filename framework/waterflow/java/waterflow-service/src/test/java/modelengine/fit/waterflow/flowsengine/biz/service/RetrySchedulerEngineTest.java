/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.biz.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import modelengine.fit.waterflow.MethodNameLoggerExtension;
import modelengine.fit.waterflow.common.utils.GlobalExecutorUtil;
import modelengine.fit.waterflow.common.utils.SleepUtil;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowretry.FlowRetryRepo;
import modelengine.fitframework.exception.FitException;
import modelengine.fitframework.schedule.Task;
import modelengine.fitframework.schedule.ThreadPoolScheduler;
import modelengine.fitframework.schedule.support.FixedRateExecutePolicy;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.time.Instant;

/**
 * RetrySchedulerEngine测试类
 *
 * @author yangxiangyu
 * @since 2024/8/15
 */
@ExtendWith(MethodNameLoggerExtension.class)
class RetrySchedulerEngineTest {
    private RetrySchedulerEngine retrySchedulerEngine;

    private FlowRetryService flowRetryService;

    private FlowRetryRepo flowRetryRepo;

    private MockedStatic<GlobalExecutorUtil> globalExecutorUtilMockedStatic;

    private MockedStatic<SleepUtil> sleepUtilMockedStatic;

    @BeforeEach
    void setUp() {
        globalExecutorUtilMockedStatic = mockStatic(GlobalExecutorUtil.class);
        sleepUtilMockedStatic = mockStatic(SleepUtil.class);

        sleepUtilMockedStatic.when(() -> SleepUtil.sleep(anyInt())).then((invocation -> null));
        ThreadPoolScheduler threadPoolScheduler = Mockito.mock(ThreadPoolScheduler.class);
        doAnswer(invocationOnMock -> {
            Task runnable = invocationOnMock.getArgument(0);
            Assertions.assertTrue(runnable.policy() instanceof FixedRateExecutePolicy);
            runnable.call();
            return null;
        }).when(threadPoolScheduler).schedule(any(), isA(Instant.class));
        GlobalExecutorUtil globalExecutorUtil = Mockito.mock(GlobalExecutorUtil.class);
        when(globalExecutorUtil.getSchedulerPool()).thenReturn(threadPoolScheduler);
        globalExecutorUtilMockedStatic.when(() -> GlobalExecutorUtil.getInstance()).thenReturn(globalExecutorUtil);
    }

    @AfterEach
    void tearDown() {
        globalExecutorUtilMockedStatic.close();
        sleepUtilMockedStatic.close();
    }

    @Test
    @DisplayName("测试启动重试任务成功")
    public void retryStartSuccess() {
        flowRetryService = Mockito.mock(FlowRetryService.class);
        flowRetryRepo = Mockito.mock(FlowRetryRepo.class);
        retrySchedulerEngine = new RetrySchedulerEngine(flowRetryService, 1, flowRetryRepo);
        when(flowRetryRepo.hasRetryData()).thenReturn(10).thenReturn(0);
        when(flowRetryService.isRetryRunning()).thenReturn(false);

        retrySchedulerEngine.onFitablesRegistered();

        verify(flowRetryService, times(1)).retryTask();
    }

    @Test
    @DisplayName("测试启动重试异常")
    public void retryStartFail() {
        flowRetryRepo = Mockito.mock(FlowRetryRepo.class);
        flowRetryService = Mockito.mock(FlowRetryService.class);
        retrySchedulerEngine = new RetrySchedulerEngine(flowRetryService, 1, flowRetryRepo);
        when(flowRetryRepo.hasRetryData()).thenThrow(new FitException("retry failed."));

        retrySchedulerEngine.onFitablesRegistered();

        verify(flowRetryService, times(0)).retryTask();
    }
}