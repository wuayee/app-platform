/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.service.adapter;

import static org.mockito.Mockito.when;

import modelengine.fit.jane.task.util.OperationContext;
import modelengine.fit.jober.DatabaseBaseTest;
import modelengine.fit.jober.taskcenter.dao.TaskSourceScheduleMapper;
import modelengine.fit.jober.taskcenter.dao.po.SourceObject;
import modelengine.fit.jober.taskcenter.dao.po.TaskSourceScheduleObject;
import modelengine.fit.jober.taskcenter.domain.ScheduleSourceEntity;
import modelengine.fit.jober.taskcenter.domain.SourceEntity;
import modelengine.fit.jober.taskcenter.domain.SourceType;
import modelengine.fit.jober.taskcenter.util.DefaultDynamicSqlExecutor;
import modelengine.fit.jober.taskcenter.util.Enums;

import modelengine.fitframework.serialization.ObjectSerializer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * {@link ScheduleSourceAdapter}对应测试类
 *
 * @author 陈镕希
 * @since 2023-08-15
 */
@ExtendWith(MockitoExtension.class)
class ScheduleSourceAdapterTest extends DatabaseBaseTest {
    @Mock
    TaskSourceScheduleMapper taskSourceScheduleMapper;

    @Mock
    ObjectSerializer objectSerializer;

    private ScheduleSourceAdapter scheduleSourceAdapter;

    @BeforeEach
    void before() {
        scheduleSourceAdapter = new ScheduleSourceAdapter(taskSourceScheduleMapper, objectSerializer,
                new DefaultDynamicSqlExecutor(sqlSessionManager));
    }

    @Nested
    @DisplayName("测试检索附加信息")
    class TestRetrieveExtension {
        @Test
        @DisplayName("测试传入正确的SourceEntity，随后正常组装ScheduleSourceEntity")
        void retrieveExtension() {
            // given
            when(taskSourceScheduleMapper.select("id")).thenReturn(
                    TaskSourceScheduleObject.builder().fitableId("scheduleFitableId").interval(1).filter("{}").build());
            // when
            SourceObject sourceObject = new SourceObject();
            sourceObject.setId("id");
            sourceObject.setName("name");
            sourceObject.setApp("app");
            sourceObject.setType(Enums.toString(SourceType.SCHEDULE));
            SourceEntity actual = scheduleSourceAdapter.retrieveExtension(sourceObject,
                    OperationContext.custom().build());
            // then
            Assertions.assertTrue(actual instanceof ScheduleSourceEntity);
            ScheduleSourceEntity scheduleActual = (ScheduleSourceEntity) actual;
            Assertions.assertEquals("id", scheduleActual.getId());
            Assertions.assertEquals("name", scheduleActual.getName());
            Assertions.assertEquals("app", scheduleActual.getApp());
            Assertions.assertEquals(SourceType.SCHEDULE, scheduleActual.getType());
            Assertions.assertEquals("scheduleFitableId", scheduleActual.getFitableId());
            Assertions.assertEquals(1, scheduleActual.getInterval());
            Assertions.assertEquals(0, scheduleActual.getFilter().size());
        }
    }
}