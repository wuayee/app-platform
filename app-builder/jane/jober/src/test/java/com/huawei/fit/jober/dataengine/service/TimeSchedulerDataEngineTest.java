/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.dataengine.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.huawei.fit.jane.task.gateway.DistributedLockProvider;
import com.huawei.fit.jober.common.Constant;
import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.exceptions.JobberException;
import com.huawei.fit.jober.dataengine.biz.converter.TaskInstanceMetaDataConverter;
import com.huawei.fit.jober.dataengine.biz.converter.TimeSchedulerConverter;
import com.huawei.fit.jober.dataengine.biz.service.EventPublishService;
import com.huawei.fit.jober.dataengine.biz.service.TimeSchedulerDataEngine;
import com.huawei.fit.jober.dataengine.domain.aggregate.timescheduler.TimeScheduler;
import com.huawei.fit.jober.dataengine.domain.aggregate.timescheduler.repo.TimeSchedulerRepo;
import com.huawei.fit.jober.dataengine.rest.request.StaticMetaDataTaskDTO;
import com.huawei.fit.jober.entity.Filter;
import com.huawei.fitframework.broker.client.BrokerClient;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashMap;

/**
 * {@link TimeSchedulerDataEngine} 对应测试类
 *
 * @author 00693950
 * @since 2023/6/15
 */
@ExtendWith(MockitoExtension.class)
public class TimeSchedulerDataEngineTest {
    private static final String TASK_SOURCE_ID = "d046cba3c78e4347bdca792b96bf0457";

    private static final String TASK_DEFINITION_ID = "e2ec01e0192843fb8f249a6e25f0a74b";

    private static final String SOURCE_APP = "libing";

    private static final String SCHEDULER_ID = "cf70dac8b93149ccb943d9192553c55d";

    @Mock
    BrokerClient brokerClient;

    @Mock
    TimeSchedulerRepo timeSchedulerRepo;

    @Mock
    EventPublishService publishService;

    @Mock
    TaskInstanceMetaDataConverter taskInstanceMetaDataConverter;

    @Mock
    TimeSchedulerConverter timeSchedulerConverter;

    @Mock
    DistributedLockProvider distributedLockProvider;

    private TimeSchedulerDataEngine timeSchedulerDataEngine;

    @BeforeEach
    void before() {
        timeSchedulerDataEngine = new TimeSchedulerDataEngine(brokerClient, timeSchedulerRepo, publishService,
                taskInstanceMetaDataConverter, timeSchedulerConverter, distributedLockProvider);
    }

    @Nested
    @DisplayName("测试创建定时任务功能")
    class TestCreateTask {
        @Test
        @DisplayName("入参正确时，创建定时任务成功")
        void givenRightParamThenCreateTaskSuccessfully() {
            StaticMetaDataTaskDTO createEntity = getCreateEntity();

            TimeScheduler timeScheduler = getTimeScheduler();
            when(timeSchedulerConverter.converter(any())).thenReturn(timeScheduler);
            when(timeScheduler.save()).thenReturn(timeScheduler);

            timeSchedulerDataEngine.create(createEntity);
        }

        @Test
        @DisplayName("入参中taskSourceId字段为空字符串时，抛出异常信息")
        void givenBlankNullTaskSourceIdThenFailedAndThrowException() {
            StaticMetaDataTaskDTO createEntity = getCreateEntity();
            createEntity.setTaskSourceId("");
            JobberException jobberException = Assertions.assertThrows(JobberException.class,
                    () -> timeSchedulerDataEngine.create(createEntity));
            Assertions.assertEquals(ErrorCodes.INPUT_PARAM_IS_EMPTY.getErrorCode(), jobberException.getCode());
            Assertions.assertEquals("Input param is empty, empty param is taskSourceId.", jobberException.getMessage());
        }

        @Test
        @DisplayName("入参中taskSourceId字段为null时，抛出异常信息")
        void givenNullTaskSourceIdThenFailedAndThrowException() {
            StaticMetaDataTaskDTO createEntity = getCreateEntity();
            createEntity.setTaskSourceId(null);
            JobberException jobberException = Assertions.assertThrows(JobberException.class,
                    () -> timeSchedulerDataEngine.create(createEntity));
            Assertions.assertEquals(ErrorCodes.INPUT_PARAM_IS_EMPTY.getErrorCode(), jobberException.getCode());
            Assertions.assertEquals("Input param is empty, empty param is taskSourceId.", jobberException.getMessage());
        }

        @Test
        @DisplayName("入参中sourceApp字段为空字符串时，抛出异常信息")
        void givenBlankSourceAppThenFailedAndThrowException() {
            StaticMetaDataTaskDTO createEntity = getCreateEntity();
            createEntity.setSourceApp("");
            JobberException jobberException = Assertions.assertThrows(JobberException.class,
                    () -> timeSchedulerDataEngine.create(createEntity));
            Assertions.assertEquals(ErrorCodes.INPUT_PARAM_IS_EMPTY.getErrorCode(), jobberException.getCode());
            Assertions.assertEquals("Input param is empty, empty param is sourceApp.", jobberException.getMessage());
        }

        @Test
        @DisplayName("入参中sourceApp字段为null时，抛出异常信息")
        void givenNullSourceAppThenFailedAndThrowException() {
            StaticMetaDataTaskDTO createEntity = getCreateEntity();
            createEntity.setSourceApp(null);
            JobberException jobberException = Assertions.assertThrows(JobberException.class,
                    () -> timeSchedulerDataEngine.create(createEntity));
            Assertions.assertEquals(ErrorCodes.INPUT_PARAM_IS_EMPTY.getErrorCode(), jobberException.getCode());
            Assertions.assertEquals("Input param is empty, empty param is sourceApp.", jobberException.getMessage());
        }
    }

    @Nested
    @DisplayName("测试删除定时任务功能")
    class TestDeleteTask {
        @Test
        @DisplayName("入参正确时，删除定时任务成功")
        void givenRightParamThenDeleteSuccessfully() {
            TimeScheduler timeScheduler = getTimeScheduler();
            when(timeSchedulerConverter.converter(any())).thenReturn(timeScheduler);
            when(timeScheduler.save()).thenReturn(timeScheduler);
            timeSchedulerDataEngine.create(getCreateEntity());
            when(timeSchedulerRepo.queryByTaskSourceId(any())).thenReturn(timeScheduler);

            timeSchedulerDataEngine.delete(getCreateEntity().getTaskSourceId());
        }
    }

    @Nested
    @DisplayName("测试更新定时任务功能")
    class TestUpdateTask {
        @Test
        @DisplayName("入参正确时，更新定时任务成功")
        void givenRightParamThenUpdateSuccessfully() {
            TimeScheduler timeScheduler = getTimeScheduler();
            when(timeSchedulerConverter.converter(any())).thenReturn(timeScheduler);
            when(timeScheduler.save()).thenReturn(timeScheduler);

            timeSchedulerDataEngine.update(getCreateEntity());

            verify(timeSchedulerRepo, times(1)).delete(any());
            verify(timeSchedulerRepo, times(1)).queryByTaskSourceId(any());
            verify(timeSchedulerConverter, times(1)).converter(any());
        }
    }

    private Filter getFilter() {
        return new Filter(null, null, null, "IR", Arrays.asList("I", "A"), Constant.SYSTEM_FIELDS, null);
    }

    private TimeScheduler getTimeScheduler() {
        return TimeScheduler.builder()
                .schedulerId(SCHEDULER_ID)
                .schedulerInterval(6000)
                .filter("filter")
                .timeSchedulerRepo(timeSchedulerRepo)
                .build();
    }

    private StaticMetaDataTaskDTO getCreateEntity() {
        HashMap<String, String> map = new HashMap<>();
        map.put(Constant.SCHEDULER_INTERVAL, String.valueOf(5000));
        return StaticMetaDataTaskDTO.builder()
                .taskSourceId(TASK_SOURCE_ID)
                .taskDefinitionId(TASK_DEFINITION_ID)
                .sourceApp(SOURCE_APP)
                .filter(new Filter(10000L, null, null, null, null, null, null))
                .properties(map)
                .build();
    }
}