/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.fitable;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.huawei.fit.jober.entity.OperationContext;
import com.huawei.fit.jober.entity.TaskFilter;
import com.huawei.fit.jober.taskcenter.domain.TaskEntity;
import com.huawei.fit.jober.taskcenter.eventhandler.converter.TaskConverter;
import com.huawei.fit.jober.taskcenter.service.TaskService;
import com.huawei.fitframework.model.RangedResultSet;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link TaskFitable}对应测试类。
 *
 * @author 姚江
 * @since 2023-11-02 14:30
 */
@ExtendWith(MockitoExtension.class)
public class TaskFitableTest {
    @Mock
    TaskService taskService;

    @Mock
    TaskConverter taskConverter;

    private TaskFitable taskFitable;

    @BeforeEach
    void before() {
        reset(taskService);
        reset(taskConverter);
        when(taskService.list(any(), anyLong(), anyInt(), any())).thenReturn(mockResultSet());
        when(taskConverter.convert(any(), any())).thenReturn(null);
        taskFitable = new TaskFitable(taskService, taskConverter);
    }

    @Test
    @DisplayName("测试list方法1：filter里没有值")
    void testFilterNoValue() {
        // given
        OperationContext context = mockContext();

        // when
        Assertions.assertDoesNotThrow(() -> taskFitable.list(new TaskFilter(), 0L, 200, context));

        // then

        verify(taskService, times(1)).list(any(), anyLong(), anyInt(), any());
        verify(taskConverter, times(1)).convert(any(), any());
    }

    @Test
    @DisplayName("测试list方法2：filter里有值")
    void testFilterHasValue() {
        // given
        OperationContext context = mockContext();
        TaskFilter filter = mockFilter();

        // when
        Assertions.assertDoesNotThrow(() -> taskFitable.list(filter, 0L, 200, context));

        // then

        verify(taskService, times(1)).list(any(), anyLong(), anyInt(), any());
        verify(taskConverter, times(1)).convert(any(), any());
    }

    private RangedResultSet<TaskEntity> mockResultSet() {
        List<TaskEntity> entityList = new ArrayList<>();
        entityList.add(new TaskEntity());

        return RangedResultSet.create(entityList, 0, 200, 1);
    }

    private OperationContext mockContext() {
        return new OperationContext("TEST_TENANT", "TEST", "192.168.1.11", null, null);
    }

    private TaskFilter mockFilter() {
        TaskFilter taskFilter = new TaskFilter();
        taskFilter.setIds(new ArrayList<>());
        taskFilter.getIds().add("ID");
        taskFilter.setNames(new ArrayList<>());
        taskFilter.getNames().add("NAME");

        return taskFilter;
    }
}
