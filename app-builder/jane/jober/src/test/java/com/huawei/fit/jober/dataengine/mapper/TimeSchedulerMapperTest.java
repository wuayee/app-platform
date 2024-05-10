/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.dataengine.mapper;

import com.huawei.fit.jober.DatabaseBaseTest;
import com.huawei.fit.jober.common.Constant;
import com.huawei.fit.jober.dataengine.persist.mapper.TimeSchedulerMapper;
import com.huawei.fit.jober.dataengine.persist.po.TimeSchedulerPO;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * {@link TimeSchedulerMapper} 对应测试类
 *
 * @author 00693950
 * @since 2023/6/15
 */
@Disabled
public class TimeSchedulerMapperTest extends DatabaseBaseTest {
    public static final String DATA_SQL = "handler/timeScheduler/saveData.sql";

    private static final String SCHEDULER_ID = "d06863c4bbcb46aeb322903edeb3c43b";

    private static final String TASK_SOURCE_ID = "d046cba3c78e4347bdca792b96bf0457";

    private static final String TASK_DEFINITION_ID = "e2ec01e0192843fb8f249a6e25f0a74b";

    private static final String SOURCE_APP = "libing";

    private TimeSchedulerMapper schedulerMapper;

    @BeforeEach
    void before() {
        schedulerMapper = sqlSessionManager.openSession(true).getMapper(TimeSchedulerMapper.class);
    }

    @Override
    protected void cleanTable() {
        executeSqlInFile("handler/timeScheduler/cleanData.sql");
    }

    @Nested
    @DisplayName("测试定时任务数据库操作功能")
    class TestTimeSchedulerRepo {
        @Test
        @DisplayName("保存定时任务成功")
        void givenRightParamsThenSaveAndFindSuccessfully() {
            schedulerMapper.create(getSchedulerPO());

            TimeSchedulerPO actual = schedulerMapper.find(SCHEDULER_ID);

            Assertions.assertEquals(TASK_SOURCE_ID, actual.getTaskSourceId());
            Assertions.assertEquals(TASK_DEFINITION_ID, actual.getTaskDefinitionId());
            Assertions.assertEquals(SOURCE_APP, actual.getSourceApp());
        }

        @Test
        @DisplayName("更新定时任务成功")
        void givenExisingSchedulerIdThenUpdateSuccessfully() {
            executeSqlInFile(DATA_SQL);

            schedulerMapper.update(getSchedulerPO());
            TimeSchedulerPO actual = schedulerMapper.find(SCHEDULER_ID);

            Assertions.assertEquals(TASK_SOURCE_ID, actual.getTaskSourceId());
            Assertions.assertEquals(TASK_DEFINITION_ID, actual.getTaskDefinitionId());
            Assertions.assertEquals(SOURCE_APP, actual.getSourceApp());
            Assertions.assertEquals(1, actual.getModifyTime());
            Assertions.assertEquals("1.1.1.0", actual.getOwnerAddress());
            Assertions.assertEquals("66668", actual.getTaskTypeId());
        }

        @Test
        @DisplayName("删除定时任务成功")
        void givenExistingSchedulerIdThenDeleteSuccessfully() {
            executeSqlInFile(DATA_SQL);

            Assertions.assertDoesNotThrow(() -> schedulerMapper.find(SCHEDULER_ID));
            schedulerMapper.delete(SCHEDULER_ID);
            TimeSchedulerPO actual = schedulerMapper.find(SCHEDULER_ID);

            Assertions.assertNull(actual);
        }

        @Test
        @DisplayName("查询所有的定时任务成功")
        void givenExistingSchedulerThenQueryAllSuccessfully() {
            executeSqlInFile(DATA_SQL);

            List<TimeSchedulerPO> actual = schedulerMapper.findAll();

            Assertions.assertEquals(2, actual.size());
            Assertions.assertEquals("11", actual.get(0).getTaskDefinitionId());
            Assertions.assertEquals("33", actual.get(1).getTaskDefinitionId());
            Assertions.assertEquals("666", actual.get(0).getTaskTypeId());
        }

        @Test
        @DisplayName("通过taskSourceId查询定时任务成功")
        void givenExistingSchedulerThenQueryByTaskSourceIdSuccessfully() {
            executeSqlInFile(DATA_SQL);

            TimeSchedulerPO actual = schedulerMapper.queryByTaskSourceId("22");

            Assertions.assertEquals("11", actual.getTaskDefinitionId());
            Assertions.assertEquals("22", actual.getTaskSourceId());
            Assertions.assertEquals("666", actual.getTaskTypeId());
        }

        private TimeSchedulerPO getSchedulerPO() {
            return TimeSchedulerPO.builder()
                    .schedulerId(SCHEDULER_ID)
                    .taskSourceId(TASK_SOURCE_ID)
                    .taskDefinitionId(TASK_DEFINITION_ID)
                    .taskTypeId("66668")
                    .schedulerDataType(Constant.SCHEDULE)
                    .sourceApp(SOURCE_APP)
                    .createTime(1683519180000L)
                    .schedulerInterval(1000L)
                    .latestExecutorTime(1686820380000L)
                    .modifyTime(1L)
                    .endTime(0L)
                    .ownerAddress("1.1.1.0")
                    .build();
        }
    }
}