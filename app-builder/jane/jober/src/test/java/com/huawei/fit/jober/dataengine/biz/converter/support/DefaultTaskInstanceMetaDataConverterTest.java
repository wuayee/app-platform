/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.dataengine.biz.converter.support;

import com.huawei.fit.jober.dataengine.biz.converter.TaskInstanceMetaDataConverter;
import com.huawei.fit.jober.dataengine.domain.aggregate.timescheduler.TimeScheduler;
import com.huawei.fit.jober.dataengine.rest.response.TaskInstanceMetaData;
import com.huawei.fit.jober.entity.TaskEntity;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link DefaultTaskInstanceMetaDataConverter} 对应测试类
 *
 * @author 00693950
 * @since 2023/7/17
 */
@ExtendWith(MockitoExtension.class)
class DefaultTaskInstanceMetaDataConverterTest {
    private static final String SCHEDULER_ID = "cf70dac8b93149ccb943d9192553c55d";

    private static final String TASK_SOURCE_ID = "d046cba3c78e4347bdca792b96bf0457";

    private static final String TASK_DEFINITION_ID = "e2ec01e0192843fb8f249a6e25f0a74b";

    private TaskInstanceMetaDataConverter taskInstanceMetaDataConverter;

    @BeforeEach
    void before() {
        taskInstanceMetaDataConverter = new DefaultTaskInstanceMetaDataConverter();
    }

    @Nested
    @DisplayName("测试DefaultTaskInstanceMetaDataConverter的功能")
    class TestConvertTOTaskInstanceMetaData {
        @Test
        void givenRightParamsThenConverterSuccessfully() {
            TimeScheduler timeScheduler = getTimeScheduler();
            TaskEntity taskEntity = getTaskEntity();

            TaskInstanceMetaData actual = taskInstanceMetaDataConverter.converter(timeScheduler, taskEntity);

            Assertions.assertEquals("d046cba3c78e4347bdca792b96bf0457", actual.getTaskSourceId());
            Assertions.assertEquals("e2ec01e0192843fb8f249a6e25f0a74b", actual.getTaskDefinitionId());
            Assertions.assertEquals("lisi", actual.getTaskEntity().getName());
            Assertions.assertEquals("requirement_id", actual.getRequirementId());
            Assertions.assertEquals("yyk", actual.getOwner());
            Assertions.assertEquals("working", actual.getState());
        }
    }

    private TimeScheduler getTimeScheduler() {
        return TimeScheduler.builder()
                .schedulerId(SCHEDULER_ID)
                .taskSourceId(TASK_SOURCE_ID)
                .taskDefinitionId(TASK_DEFINITION_ID)
                .build();
    }

    private TaskEntity getTaskEntity() {
        return new TaskEntity("lisi", null, null, null, null, null, getProps());
    }

    private List<TaskEntity.TaskProperty> getProps() {
        List<TaskEntity.TaskProperty> list = new ArrayList<>();
        list.add(new TaskEntity.TaskProperty("owner", "yyk", "uri"));
        list.add(new TaskEntity.TaskProperty("id", "requirement_id", "uri"));
        list.add(new TaskEntity.TaskProperty("state", "working", "uri"));
        return list;
    }
}