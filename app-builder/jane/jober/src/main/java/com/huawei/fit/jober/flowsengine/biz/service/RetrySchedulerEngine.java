/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.flowsengine.biz.service;

import com.huawei.fit.jober.common.utils.GlobalExecutorUtil;
import com.huawei.fit.service.FitablesRegisteredObserver;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Value;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.schedule.ExecutePolicy;
import com.huawei.fitframework.schedule.Task;

import java.time.Instant;

/**
 * 以定时任务方式重试流程可重试状态上下文
 *
 * @author l00862071
 * @since 2024/2/2
 */
@Component
public class RetrySchedulerEngine implements FitablesRegisteredObserver {
    private static final Logger log = Logger.get(RetrySchedulerEngine.class);

    private final FlowContextsService flowContextsService;

    private final long scheduleRate;

    public RetrySchedulerEngine(FlowContextsService flowContextsService,
            @Value("${jane.flowsEngine.retry.scheduleRate}") long scheduleRate) {
        this.flowContextsService = flowContextsService;
        this.scheduleRate = scheduleRate;
    }

    /**
     * 安排重试可重试状态上下文的定时任务
     */
    @Override
    public void onFitablesRegistered() {
        Task retryTask = Task.builder()
                .runnable(() -> flowContextsService.retryJober())
                .policy(ExecutePolicy.fixedRate(scheduleRate))
                .build();
        log.info("Start scheduling retry tasks");
        GlobalExecutorUtil.getInstance().getSchedulerPool().schedule(retryTask, Instant.now());
    }
}
