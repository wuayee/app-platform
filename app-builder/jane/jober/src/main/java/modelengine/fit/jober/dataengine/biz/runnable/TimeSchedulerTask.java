/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.dataengine.biz.runnable;

import com.alibaba.fastjson.JSON;

import modelengine.fit.jane.task.util.OperationContext;
import modelengine.fit.jober.common.Constant;
import modelengine.fit.jober.common.utils.HostUtil;
import modelengine.fit.jober.common.utils.SleepUtil;
import modelengine.fit.jober.dataengine.biz.converter.TaskInstanceMetaDataConverter;
import modelengine.fit.jober.dataengine.biz.service.DataFetcher;
import modelengine.fit.jober.dataengine.biz.service.EventPublishService;
import modelengine.fit.jober.dataengine.domain.aggregate.timescheduler.TimeScheduler;
import modelengine.fit.jober.dataengine.domain.aggregate.timescheduler.repo.TimeSchedulerRepo;
import modelengine.fit.jober.dataengine.rest.response.TaskInstanceMetaData;
import modelengine.fit.jober.entity.Filter;
import modelengine.fit.jober.entity.TaskEntity;
import modelengine.fit.waterflow.spi.lock.DistributedLockProvider;
import modelengine.fitframework.broker.client.BrokerClient;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.util.CollectionUtils;
import modelengine.fitframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 定时任务线程
 *
 * @author 晏钰坤
 * @since 2023/6/26
 */
public class TimeSchedulerTask implements Runnable {
    private static final Logger log = Logger.get(TimeSchedulerTask.class);

    private static final Pattern NUMBER_PATTERN = Pattern.compile("^[0-9,]+$");

    private final TimeSchedulerRepo timeSchedulerRepo;

    private final EventPublishService publishService;

    private final String schedulerId;

    private final TaskInstanceMetaDataConverter taskInstanceMetaDataConverter;

    private final BrokerClient brokerClient;

    private final DistributedLockProvider distributedLockProvider;

    private final OperationContext context =
            OperationContext.custom().operator("taskCenter-scheduler").sourcePlatform("jane-taskCenter").build();

    public TimeSchedulerTask(TimeSchedulerRepo timeSchedulerRepo, EventPublishService publishService,
            String schedulerId, TaskInstanceMetaDataConverter taskInstanceMetaDataConverter, BrokerClient brokerClient,
            DistributedLockProvider distributedLockProvider) {
        this.timeSchedulerRepo = timeSchedulerRepo;
        this.publishService = publishService;
        this.schedulerId = schedulerId;
        this.taskInstanceMetaDataConverter = taskInstanceMetaDataConverter;
        this.brokerClient = brokerClient;
        this.distributedLockProvider = distributedLockProvider;
    }

    @Override
    public void run() {
        log.info("[dataEngine]: Time scheduler is running at {}.", System.currentTimeMillis());
        TimeScheduler timeScheduler = timeSchedulerRepo.querySchedulerById(schedulerId);

        Lock lock = this.distributedLockProvider.get("lock:scheduler:" + schedulerId);
        boolean isLockAcquired = lock.tryLock();
        if (!isLockAcquired) {
            log.info("[dataEngine]timeSchedulerRunner(): Get lock:scheduler:{} failed, it is occupied by {}.",
                    schedulerId,
                    timeScheduler.getOwnerAddress());
            return;
        }
        log.info("[dataEngine]timeSchedulerRunner(): Get lock:scheduler:{} success.", schedulerId);

        try {
            if (checkAndSetSchedulerOwnerAddress(timeScheduler)) {
                log.info("[dataEngine]: Time Scheduler is modified at {}, skip this operation.",
                        timeScheduler.getModifyTime());
                return;
            }

            Filter dataBaseFilter = JSON.parseObject(timeScheduler.getFilter(), Filter.class);
            long startTime = isFirstTimeExecute(timeScheduler)
                    ? timeScheduler.getCreateTime()
                    : timeScheduler.getLatestExecutorTime();
            long endTime = isFirstTimeExecute(timeScheduler) ? timeScheduler.getEndTime() : System.currentTimeMillis();
            log.info("[dataEngine]: Start to fetch data, time period is {} - {}.", startTime, endTime);

            AtomicBoolean occurException = new AtomicBoolean(false);
            List<String> metaDataList;
            String filterMetaData = dataBaseFilter.getMetaData();
            if (!StringUtils.isBlank(filterMetaData)) {
                metaDataList = Collections.singletonList(filterMetaData);
            } else {
                metaDataList = DataFetcher.loopGetMetaData(brokerClient, timeScheduler, occurException);
            }
            if (CollectionUtils.isEmpty(metaDataList)) {
                log.warn("[dataEngine]: The metadata list is empty.");
                return;
            }

            log.info("[dataEngine]: Start to get taskEntity, time scheduler id is {}.", timeScheduler.getSchedulerId());
            for (String metaData : metaDataList) {
                Filter pullDataFilter = getPullFilter(metaData, startTime, endTime, dataBaseFilter);
                getTaskEntityAndSend(timeScheduler, pullDataFilter, occurException);
            }
            log.info("[dataEngine]: Finish to get taskEntity, time scheduler id is {}.",
                    timeScheduler.getSchedulerId());

            timeScheduler.setLatestExecutorTime(endTime);
            timeScheduler.setModifyTime(System.currentTimeMillis());
            timeScheduler.save();
            log.info("[dataEngine]: Time scheduler is finished at {}.", System.currentTimeMillis());
        } finally {
            lock.unlock();
            log.info("[dataEngine]timeSchedulerRunner(): Release lock:scheduler:{} success.", schedulerId);
        }
    }

    private boolean checkAndSetSchedulerOwnerAddress(TimeScheduler timeScheduler) {
        String curAddress = HostUtil.getHostAddress();
        String savedAddress = timeScheduler.getOwnerAddress();
        if (!Objects.equals(curAddress, savedAddress)) {
            if ((System.currentTimeMillis() - timeScheduler.getModifyTime()) <= timeScheduler.getSchedulerInterval()) {
                return true;
            }
            timeScheduler.setOwnerAddress(curAddress);
        }
        return false;
    }

    private boolean isFirstTimeExecute(TimeScheduler timeScheduler) {
        return timeScheduler.getLatestExecutorTime() == 0;
    }

    private void getTaskEntityAndSend(TimeScheduler timeScheduler, Filter pullDataFilter,
            AtomicBoolean occurException) {
        int pageSize = Integer.parseInt(Constant.PAGE_SIZE);
        int pageNo = 1;
        while (true) {
            try {
                List<TaskEntity> taskEntities = DataFetcher.loopGetTaskEntity(brokerClient,
                        timeScheduler,
                        pullDataFilter,
                        pageSize,
                        pageNo,
                        occurException);
                pageNo++;
                taskEntities.stream()
                        .map(taskEntity -> taskInstanceMetaDataConverter.converter(timeScheduler, taskEntity))
                        .filter(this::filterByOwner)
                        .forEach(taskInfo -> publishService.sendData(taskInfo, context));
                SleepUtil.sleep(500);
                if (taskEntities.size() < Integer.parseInt(Constant.PAGE_SIZE)) {
                    break;
                }
            } catch (NumberFormatException e) {
                occurException.set(true);
                log.error("Number format error.");
                return;
            } catch (Exception e) {
                occurException.set(true);
                log.error(
                        "Fail to get tasks by filter, the metaData is {}, page no is {}, error message: {}, cause: {}.",
                        pullDataFilter.getMetaData(),
                        pageNo,
                        e.getMessage(),
                        getCauseMessage(e));
                return;
            }
        }
    }

    private boolean filterByOwner(TaskInstanceMetaData taskInfo) {
        return StringUtils.isNotBlank(taskInfo.getOwner()) && !NUMBER_PATTERN.matcher(taskInfo.getOwner()).matches();
    }

    private Filter getPullFilter(String metaData, long startTime, long endTime, Filter dataBaseFilter) {
        return new Filter(startTime,
                endTime,
                metaData,
                dataBaseFilter.getCategory(),
                dataBaseFilter.getStatus(),
                generateReturnFields(dataBaseFilter),
                null);
    }

    private List<String> generateReturnFields(Filter dataBaseFilter) {
        List<String> returnField = new ArrayList<>(Constant.SYSTEM_FIELDS);
        List<String> additionalReturnFields =
                Optional.ofNullable(dataBaseFilter.getReturnField()).orElse(Collections.emptyList());

        returnField.addAll(additionalReturnFields);
        return returnField.stream().distinct().collect(Collectors.toList());
    }

    private String getCauseMessage(Exception e) {
        return Optional.ofNullable(e.getCause()).map(Throwable::getMessage).orElse("");
    }
}
