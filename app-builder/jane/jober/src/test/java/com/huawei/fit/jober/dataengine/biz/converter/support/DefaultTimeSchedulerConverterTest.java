/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.dataengine.biz.converter.support;

import com.huawei.fit.jober.common.Constant;
import com.huawei.fit.jober.dataengine.domain.aggregate.timescheduler.TimeScheduler;
import com.huawei.fit.jober.dataengine.domain.aggregate.timescheduler.repo.TimeSchedulerRepo;
import com.huawei.fit.jober.dataengine.rest.request.StaticMetaDataTaskDTO;
import com.huawei.fit.jober.entity.Filter;

import com.alibaba.fastjson.JSON;

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
import java.util.Map;

/**
 * {@link DefaultTimeSchedulerConverter} 对应测试类
 *
 * @author 00693950
 * @since 2023/7/12
 */
@ExtendWith(MockitoExtension.class)
class DefaultTimeSchedulerConverterTest {
    @Mock
    TimeSchedulerRepo timeSchedulerRepo;

    private DefaultTimeSchedulerConverter timeSchedulerConverter;

    @BeforeEach
    void before() {
        timeSchedulerConverter = new DefaultTimeSchedulerConverter(timeSchedulerRepo);
    }

    @Nested
    @DisplayName("测试TimeSchedulerConverter的功能")
    class TestTaskDTOTOTimeScheduler {
        private static final String TASK_SOURCE_ID = "d046cba3c78e4347bdca792b96bf0457";

        private static final String TASK_DEFINITION_ID = "e2ec01e0192843fb8f249a6e25f0a74b";

        private static final String GET_METADATA_METHOD_GENERICABLE_ID = "2b7e619019774a4ea69165bb42071f0c";

        private static final String SOURCE_APP = "libing";

        @Test
        @DisplayName("将MetaDataTaskDTO转换成TimeScheduler")
        void givenMetaDataTaskDTOThenConverterSuccessfully() {
            StaticMetaDataTaskDTO metaDataTaskDTO = StaticMetaDataTaskDTO.builder()
                    .taskDefinitionId(TASK_DEFINITION_ID)
                    .taskSourceId(TASK_SOURCE_ID)
                    .sourceApp(SOURCE_APP)
                    .filter(getFilter())
                    .properties(getProperties())
                    .build();

            TimeScheduler converter = timeSchedulerConverter.converter(metaDataTaskDTO);

            Assertions.assertEquals("d046cba3c78e4347bdca792b96bf0457", converter.getTaskSourceId());
            Assertions.assertEquals("libing", converter.getSourceApp());
            Assertions.assertEquals("IR", JSON.parseObject(converter.getFilter(), Filter.class).getCategory());
            Assertions.assertEquals("2b7e619019774a4ea69165bb42071f0c",
                    converter.getProperties().get(Constant.DATA_FETCH_TYPE));
        }

        private Map<String, String> getProperties() {
            Map<String, String> map = new HashMap<>();
            map.put(Constant.SCHEDULER_INTERVAL, "300000");
            map.put(Constant.DATA_FETCH_TYPE, GET_METADATA_METHOD_GENERICABLE_ID);
            return map;
        }

        private Filter getFilter() {
            return new Filter(1686569066000L, null, null, "IR", Arrays.asList("I", "A"), Constant.SYSTEM_FIELDS, null);
        }
    }
}