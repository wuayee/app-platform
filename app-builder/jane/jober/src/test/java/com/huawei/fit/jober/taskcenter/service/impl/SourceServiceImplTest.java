/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jane.task.util.UndefinableValue;
import com.huawei.fit.jober.DatabaseBaseTest;
import com.huawei.fit.jober.taskcenter.dao.SourceMapper;
import com.huawei.fit.jober.taskcenter.dao.po.SourceObject;
import com.huawei.fit.jober.taskcenter.declaration.SourceDeclaration;
import com.huawei.fit.jober.taskcenter.domain.SourceType;
import com.huawei.fit.jober.taskcenter.domain.TaskInstance;
import com.huawei.fit.jober.taskcenter.service.InstanceEventService;
import com.huawei.fit.jober.taskcenter.service.SourceService;
import com.huawei.fit.jober.taskcenter.service.TaskService;
import com.huawei.fit.jober.taskcenter.service.TriggerService;
import com.huawei.fit.jober.taskcenter.service.adapter.RefreshInTimeSourceAdapter;
import com.huawei.fit.jober.taskcenter.service.adapter.ScheduleSourceAdapter;
import com.huawei.fit.jober.taskcenter.service.adapter.ThirdPartyPushSourceAdapter;
import com.huawei.fit.jober.taskcenter.util.DefaultDynamicSqlExecutor;
import com.huawei.fit.jober.taskcenter.util.Enums;
import com.huawei.fit.jober.taskcenter.validation.RelationshipValidator;
import com.huawei.fit.jober.taskcenter.validation.SourceValidator;
import com.huawei.fitframework.ioc.BeanContainer;
import com.huawei.fitframework.plugin.Plugin;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.util.Arrays;

/**
 * {@link SourceServiceImpl} 对应测试类。
 *
 * @author 陈镕希
 * @since 2023-08-15
 */
@ExtendWith(MockitoExtension.class)
class SourceServiceImplTest extends DatabaseBaseTest {
    @Mock
    SourceValidator sourceValidator;

    @Mock
    TriggerService triggerService;

    @Mock
    SourceMapper sourceMapper;

    @Mock
    ScheduleSourceAdapter scheduleSourceAdapter;

    @Mock
    RefreshInTimeSourceAdapter refreshSourceAdapter;

    @Mock
    ThirdPartyPushSourceAdapter thirdPartyPushSourceAdapter;

    @Mock
    EventPublishServiceImpl eventPublishService;

    private SourceService sourceService;

    @Mock
    private InstanceEventService instanceEventService;

    @Mock
    private RelationshipValidator relationshipValidator;

    @Mock
    private TaskInstance.Repo taskInstanceRepo;

    @Mock
    private Plugin plugin;

    @Mock
    private BeanContainer container;

    @Mock
    private BeanContainer.Beans beans;

    @Mock
    private TaskService taskService;

    @BeforeEach
    void before() {
        when(scheduleSourceAdapter.getType()).thenReturn(SourceType.SCHEDULE);
        when(refreshSourceAdapter.getType()).thenReturn(SourceType.REFRESH_IN_TIME);
        when(thirdPartyPushSourceAdapter.getType()).thenReturn(SourceType.THIRD_PARTY_PUSH);
        doNothing().when(relationshipValidator).validateSourceExistInTaskType(anyString(), anyString());
        doNothing().when(relationshipValidator).validateTaskExistInTenant(anyString(), anyString());
        doNothing().when(relationshipValidator).validateTaskTypeExistInTask(anyString(), anyString());
        doNothing().when(taskInstanceRepo).deleteBySource(any(), anyString(), any(OperationContext.class));
        when(beans.get(eq(TaskInstance.Repo.class))).thenReturn(taskInstanceRepo);
        when(container.beans()).thenReturn(beans);
        when(plugin.container()).thenReturn(container);

        this.sourceService = new SourceServiceImpl(sourceValidator, triggerService, sourceMapper,
                new DefaultDynamicSqlExecutor(sqlSessionManager), eventPublishService,
                Arrays.asList(scheduleSourceAdapter, refreshSourceAdapter, thirdPartyPushSourceAdapter),
                this.instanceEventService, this.relationshipValidator, plugin);
    }

    @Nested
    @DisplayName("测试Patch功能")
    class TestPatch {
        @Test
        @Disabled
        @DisplayName("参数正确，patch成功")
        void givenCorrectParamThenPatchSuccessfully() {
            // given
            executeSqlInFile("handler/taskTreeNode/saveData.sql");
            executeSqlInFile("handler/source/saveData.sql");
            when(sourceValidator.validateTaskId(any())).thenAnswer((Answer<String>) invocation -> {
                Object[] args = invocation.getArguments();
                return (String) args[0];
            });
            when(sourceValidator.validateSourceId(any())).thenAnswer((Answer<String>) invocation -> {
                Object[] args = invocation.getArguments();
                return (String) args[0];
            });
            SourceObject sourceObject = SourceObject.builder()
                    .id("a3a7df7e45f44451970a6a8e138382c2")
                    .taskId("a3a7df7e45f44451970a6a8e138382a2")
                    .type(Enums.toString(SourceType.SCHEDULE))
                    .build();
            when(sourceMapper.select(any())).thenReturn(sourceObject);
            SourceDeclaration sourceDeclaration = new SourceDeclaration();
            sourceDeclaration.setName(UndefinableValue.defined("patchName"));
            sourceDeclaration.setApp(UndefinableValue.defined("patchApp"));
            sourceDeclaration.setType(UndefinableValue.undefined());
            sourceDeclaration.setTriggers(UndefinableValue.undefined());
            // when
            OperationContext context = OperationContext.custom().build();
            Assertions.assertDoesNotThrow(
                    () -> sourceService.patch("a3a7df7e45f44451970a6a8e138382a2", null,
                            "a3a7df7e45f44451970a6a8e138382c2",
                            sourceDeclaration, context));
            // then
            Mockito.verify(scheduleSourceAdapter, times(1)).patchExtension(sourceObject, sourceDeclaration, context);
            Mockito.verify(refreshSourceAdapter, times(0)).patchExtension(any(), any(), any());
            Mockito.verify(thirdPartyPushSourceAdapter, times(0)).patchExtension(any(), any(), any());
            Mockito.verify(triggerService, times(0)).batchSave(any(), any(), any());
        }
    }
}