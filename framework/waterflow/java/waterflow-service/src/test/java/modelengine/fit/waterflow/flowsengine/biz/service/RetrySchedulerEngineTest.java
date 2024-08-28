/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.waterflow.flowsengine.biz.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import modelengine.fit.waterflow.common.utils.SleepUtil;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.repo.flowretry.FlowRetryRepo;
import modelengine.fitframework.exception.FitException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * RetrySchedulerEngine测试类
 *
 * @author yangxiangyu
 * @since 2024/8/15
 */
class RetrySchedulerEngineTest {
    private RetrySchedulerEngine retrySchedulerEngine;

    private FlowContextsService flowContextsService;

    private FlowRetryRepo flowRetryRepo;

    @Test
    @DisplayName("测试启动重试任务成功")
    public void retryStartSuccess() {
        flowContextsService = Mockito.mock(FlowContextsService.class);
        flowRetryRepo = Mockito.mock(FlowRetryRepo.class);
        retrySchedulerEngine = new RetrySchedulerEngine(flowContextsService, 1, flowRetryRepo);
        when(flowRetryRepo.hasRetryData()).thenReturn(10).thenReturn(0);
        when(flowContextsService.isRetryRunning()).thenReturn(false);

        retrySchedulerEngine.onFitablesRegistered();
        SleepUtil.sleep(10);

        verify(flowContextsService).retryTask();
    }

    @Test
    @DisplayName("测试启动重试异常")
    public void retryStartFail() {
        flowRetryRepo = Mockito.mock(FlowRetryRepo.class);
        flowContextsService = Mockito.mock(FlowContextsService.class);
        retrySchedulerEngine = new RetrySchedulerEngine(flowContextsService, 1, flowRetryRepo);
        when(flowRetryRepo.hasRetryData()).thenThrow(new FitException("retry failed."));

        retrySchedulerEngine.onFitablesRegistered();
        SleepUtil.sleep(10);

        verify(flowContextsService, times(0)).retryTask();
    }
}