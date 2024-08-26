/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.waterflow.flowsengine.biz.service;

import com.huawei.fit.service.FitablesRegisteredObserver;
import com.huawei.fit.waterflow.common.utils.GlobalExecutorUtil;
import com.huawei.fit.waterflow.flowsengine.domain.flows.context.repo.flowretry.FlowRetryRepo;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Value;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.schedule.ExecutePolicy;
import com.huawei.fitframework.schedule.Task;

import java.time.Instant;

/**
 * 以定时任务方式重试流程可重试状态上下文
 *
 * @author 李哲峰
 * @since 2024/2/2
 */
@Component
public class RetrySchedulerEngine implements FitablesRegisteredObserver {
    private static final Logger log = Logger.get(RetrySchedulerEngine.class);

    private final FlowContextsService flowContextsService;

    private final long scheduleRate;

    private final FlowRetryRepo retryRepo;

    public RetrySchedulerEngine(FlowContextsService flowContextsService,
                                @Value("${jane.flowsEngine.retry.scheduleRate}") long scheduleRate,
                                FlowRetryRepo retryRepo) {
        this.flowContextsService = flowContextsService;
        this.scheduleRate = scheduleRate;
        this.retryRepo = retryRepo;
    }

    /**
     * 定时检测是否有重试数据
     */
    @Override
    public void onFitablesRegistered() {
        Task retryTask = Task.builder()
                .runnable(this::retryCheck)
                .policy(ExecutePolicy.fixedRate(scheduleRate))
                .build();
        log.info("Start scheduling retry tasks");
        GlobalExecutorUtil.getInstance().getSchedulerPool().schedule(retryTask, Instant.now());
    }

    private void retryCheck() {
        try {
            int retryCount = retryRepo.hasRetryData();
            if (retryCount == 0 || flowContextsService.isRetryRunning()) {
                return;
            }
            flowContextsService.retryTask();
        } catch (Exception ex) {
            log.error("retryCheck failed, exception: ", ex);
        }
    }
}
