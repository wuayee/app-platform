/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.dataengine.biz.runnable;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import modelengine.fit.jober.common.Constant;
import modelengine.fit.jober.dataengine.biz.converter.TaskInstanceMetaDataConverter;
import modelengine.fit.jober.dataengine.biz.service.EventPublishService;
import modelengine.fit.jober.dataengine.domain.aggregate.timescheduler.TimeScheduler;
import modelengine.fit.jober.dataengine.domain.aggregate.timescheduler.repo.TimeSchedulerRepo;
import modelengine.fit.jober.dataengine.rest.response.TaskInstanceMetaData;

import modelengine.fit.jober.entity.Filter;
import modelengine.fit.jober.entity.TaskEntity;

import com.alibaba.fastjson.JSON;

import modelengine.fit.jober.DataService;
import modelengine.fit.waterflow.spi.lock.DistributedLockProvider;
import modelengine.fitframework.broker.client.BrokerClient;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * {@link TimeSchedulerTask} 对应测试类
 *
 * @author 晏钰坤
 * @since 2023/7/17
 */
@ExtendWith(MockitoExtension.class)
class TimeSchedulerTaskTest {
    @Mock
    DataService dataService;

    @Mock
    TimeSchedulerRepo timeSchedulerRepo;

    @Mock
    EventPublishService publishService;

    @Mock
    TaskInstanceMetaDataConverter taskInstanceMetaDataConverter;

    @Mock
    BrokerClient brokerClient;

    @Mock
    DistributedLockProvider distributedLockProvider;

    private TimeSchedulerTask timeSchedulerTask;

    private static final String SCHEDULER_ID = "cf70dac8b93149ccb943d9192553c55d";

    private static final String TASK_SOURCE_ID = "d046cba3c78e4347bdca792b96bf0457";

    @BeforeEach
    void before() {
        timeSchedulerTask = new TimeSchedulerTask(timeSchedulerRepo, publishService, "schedulerId",
                taskInstanceMetaDataConverter, brokerClient, distributedLockProvider);
    }

    @Nested
    @DisplayName("测试运行定时任务功能")
    class TestRunTimeScheduler {
        @Test
        @Disabled
        @DisplayName("入参正确，运行定时任务成功")
        void givenRightConditionThenRunSuccessfully() {
            TimeScheduler timeScheduler = getTimeScheduler();
            when(timeSchedulerRepo.querySchedulerById(any())).thenReturn(timeScheduler);
            when(dataService.getMetaDataList()).thenReturn(Collections.singletonList("2140790406"));
            when(dataService.getTasksByFilter(any(), any())).thenReturn(getTaskEntities());
            when(taskInstanceMetaDataConverter.converter(any(), any())).thenReturn(getTaskInstanceMetaData());

            timeSchedulerTask.run();

            Assertions.assertEquals(1689132266918L, timeScheduler.getLatestExecutorTime());
        }
    }

    private TaskInstanceMetaData getTaskInstanceMetaData() {
        return TaskInstanceMetaData.builder().taskEntity(getTaskEntity()).build();
    }

    private TaskEntity getTaskEntity() {
        return new TaskEntity("lisi", null, null, null, null, null, getProps());
    }

    private List<TaskEntity.TaskProperty> getProps() {
        List<TaskEntity.TaskProperty> list = new ArrayList<>();
        TaskEntity.TaskProperty taskProperty = new TaskEntity.TaskProperty("owner", "yyk", "uri");
        list.add(taskProperty);
        return list;
    }

    private List<TaskEntity> getTaskEntities() {
        return Stream.of(getTaskEntity()).collect(Collectors.toList());
    }

    private TimeScheduler getTimeScheduler() {
        return TimeScheduler.builder()
                .schedulerId(SCHEDULER_ID)
                .taskSourceId(TASK_SOURCE_ID)
                .createTime(1686569066000L)
                .endTime(1689132266918L)
                .schedulerInterval(6000)
                .filter(JSON.toJSONString(getFilter()))
                .timeSchedulerRepo(timeSchedulerRepo)
                .build();
    }

    private Filter getFilter() {
        return new Filter(1686569066000L, null, null, "IR", Arrays.asList("I", "A"), Constant.SYSTEM_FIELDS,
                new HashMap<String, String>() {{
                    put("dataFetchType", "9633348c80a740dca4e4a7ca51a6c447");
                }});
    }
}