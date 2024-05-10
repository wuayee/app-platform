/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.dataengine.biz.service;

import static com.huawei.fit.jober.common.Constant.SOURCE_APP;
import static com.huawei.fit.jober.common.Constant.TASK_SOURCE_ID;
import static com.huawei.fit.jober.common.ErrorCodes.INPUT_PARAM_IS_EMPTY;
import static com.huawei.fit.jober.common.ErrorCodes.SCHEDULE_TASK_IS_EXISTED;

import com.huawei.fit.jane.task.gateway.DistributedLockProvider;
import com.huawei.fit.jober.common.exceptions.JobberException;
import com.huawei.fit.jober.common.exceptions.JobberParamException;
import com.huawei.fit.jober.common.utils.GlobalExecutorUtil;
import com.huawei.fit.jober.common.utils.HostUtil;
import com.huawei.fit.jober.dataengine.biz.converter.TaskInstanceMetaDataConverter;
import com.huawei.fit.jober.dataengine.biz.converter.TimeSchedulerConverter;
import com.huawei.fit.jober.dataengine.biz.runnable.TimeSchedulerTask;
import com.huawei.fit.jober.dataengine.domain.aggregate.timescheduler.TimeScheduler;
import com.huawei.fit.jober.dataengine.domain.aggregate.timescheduler.repo.TimeSchedulerRepo;
import com.huawei.fit.jober.dataengine.genericable.StaticDataEngine;
import com.huawei.fit.jober.dataengine.rest.request.StaticMetaDataTaskDTO;
import com.huawei.fitframework.annotation.Alias;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fitable;
import com.huawei.fitframework.annotation.Initialize;
import com.huawei.fitframework.broker.client.BrokerClient;
import com.huawei.fitframework.inspection.Validation;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.schedule.ExecutePolicy;
import com.huawei.fitframework.schedule.Task;
import com.huawei.fitframework.schedule.annotation.Scheduled;
import com.huawei.fitframework.transaction.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

/**
 * 以定时任务方式获取第三方数据实现类
 *
 * @author 00693950
 * @since 2023/6/12
 */
@Component
@Alias("scheduler")
public class TimeSchedulerDataEngine implements StaticDataEngine {
    private static final Logger log = Logger.get(TimeSchedulerDataEngine.class);

    private final BrokerClient brokerClient;

    private final TimeSchedulerRepo timeSchedulerRepo;

    private final EventPublishService publishService;

    private final TaskInstanceMetaDataConverter taskInstanceMetaDataConverter;

    private final TimeSchedulerConverter timeSchedulerConverter;

    private final DistributedLockProvider distributedLockProvider;

    private final Map<String, ScheduledFuture<?>> futureMap = new ConcurrentHashMap<>();

    public TimeSchedulerDataEngine(BrokerClient brokerClient, TimeSchedulerRepo timeSchedulerRepo,
            EventPublishService publishService, TaskInstanceMetaDataConverter taskInstanceMetaDataConverter,
            TimeSchedulerConverter timeSchedulerConverter, DistributedLockProvider distributedLockProvider) {
        this.brokerClient = brokerClient;
        this.timeSchedulerRepo = timeSchedulerRepo;
        this.publishService = publishService;
        this.taskInstanceMetaDataConverter = taskInstanceMetaDataConverter;
        this.timeSchedulerConverter = timeSchedulerConverter;
        this.distributedLockProvider = distributedLockProvider;
    }

    /**
     * 创建定时任务
     *
     * @param staticMetaDataTaskDTO 创建定时任务请求体
     */
    @Override
    @Transactional
    @Fitable(id = "d7495cf0160c499db83b9adf6c2cbd9c")
    public void create(StaticMetaDataTaskDTO staticMetaDataTaskDTO) {
        log.info("[dataEngine]: Start to create timeScheduler task to get data, the taskSourceId is {}.",
                staticMetaDataTaskDTO.getTaskSourceId());
        checkCreateParams(staticMetaDataTaskDTO);

        TimeScheduler timeScheduler = timeSchedulerConverter.converter(staticMetaDataTaskDTO);
        timeScheduler.setOwnerAddress(HostUtil.getHostAddress());
        TimeScheduler timeSchedulerSaved = timeScheduler.save();
        String schedulerId = timeSchedulerSaved.getSchedulerId();
        log.info("[dataEngine]: Success to save scheduler. SchedulerId is {}", schedulerId);
        log.info("[dataEngine]: Finish to create timeScheduler task, the taskSourceId is {}.",
                staticMetaDataTaskDTO.getTaskSourceId());
    }

    /**
     * timeSchedulerRunner
     */
    // @Scheduled(strategy = Scheduled.Strategy.FIXED_RATE, value = "5000")
    public void timeSchedulerRunner() {
        executeSchedulerWithDistributedLock();
    }

    private void executeSchedulerWithDistributedLock() {
        List<TimeScheduler> timeSchedulers = timeSchedulerRepo.queryAllScheduler();
        for (TimeScheduler timeScheduler : timeSchedulers) {
            String schedulerId = timeScheduler.getSchedulerId();
            String taskSourceId = timeScheduler.getTaskSourceId();

            if (futureMap.containsKey(taskSourceId)) {
                continue;
            }

            Task task = Task.builder()
                    .runnable(
                            new TimeSchedulerTask(timeSchedulerRepo, publishService, schedulerId,
                                    taskInstanceMetaDataConverter,
                                    brokerClient, distributedLockProvider))
                    .policy(ExecutePolicy.fixedRate(timeScheduler.getSchedulerInterval()))
                    .uncaughtExceptionHandler((thread, throwable) -> {
                        log.error("The thread pool run failed, error cause: {}, message: {}.", throwable.getCause(),
                                throwable.getMessage());
                        log.error("The thread pool run failed details: ", throwable);
                        // TODO:需要抛出异常吗
                    })
                    .build();
            ScheduledFuture<?> schedule = GlobalExecutorUtil.getInstance()
                    .getSchedulerPool()
                    .schedule(task, Instant.now());
            futureMap.put(taskSourceId, schedule);
            log.info("[dataEngine]: Time Scheduler is exist in futureMap, the task source ID: {}.", taskSourceId);
        }
    }

    @Override
    @Transactional
    @Fitable(id = "c45ce1975aa7486c8fce18884999cf9f")
    public void update(StaticMetaDataTaskDTO staticMetaDataTaskDTO) {
        log.info("[dataEngine]: Start to update timeScheduler task, the taskSourceId is {}.",
                staticMetaDataTaskDTO.getTaskSourceId());

        delete(staticMetaDataTaskDTO.getTaskSourceId());
        create(staticMetaDataTaskDTO);

        log.info("[dataEngine]: Update timeScheduler task success, the taskSourceId is {}.",
                staticMetaDataTaskDTO.getTaskSourceId());
    }

    @Override
    @Transactional
    @Fitable(id = "e294d8dc86c647c0a0d868a1ee3df610")
    public void delete(String taskSourceId) {
        log.info("[dataEngine]: Start to delete timeScheduler task, the taskSourceId is {}.", taskSourceId);

        TimeScheduler timeScheduler = timeSchedulerRepo.queryByTaskSourceId(taskSourceId);
        timeSchedulerRepo.delete(timeScheduler);
        Optional.ofNullable(futureMap.get(taskSourceId)).ifPresent(future -> future.cancel(false));
        futureMap.remove(taskSourceId);

        log.info("[dataEngine]: Delete timeScheduler task success, the taskSourceId is {}.", taskSourceId);
    }

    // @Initialize
    private void breakRecovery() {
        log.info("[dataEngine]: Start to recovery all scheduled tasks.");
        executeSchedulerWithDistributedLock();
        log.info("[dataEngine]: Finish to recovery all scheduled tasks.");
    }

    private void checkCreateParams(StaticMetaDataTaskDTO staticMetaDataTaskDTO) {
        Validation.notBlank(staticMetaDataTaskDTO.getTaskSourceId(),
                () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, TASK_SOURCE_ID));
        Validation.notBlank(staticMetaDataTaskDTO.getSourceApp(),
                () -> new JobberParamException(INPUT_PARAM_IS_EMPTY, SOURCE_APP));
        if (futureMap.containsKey(staticMetaDataTaskDTO.getTaskSourceId())) {
            throw new JobberException(SCHEDULE_TASK_IS_EXISTED);
        }
    }
}
