/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.fitable;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.huawei.fit.jane.task.util.PagedResultSet;
import com.huawei.fit.jane.task.util.PaginationResult;
import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.RangedResultSet;
import com.huawei.fit.jober.common.exceptions.BadRequestException;
import com.huawei.fit.jober.entity.InstanceInfo;
import com.huawei.fit.jober.entity.InstanceQueryFilter;
import com.huawei.fit.jober.entity.OperationContext;
import com.huawei.fit.jober.entity.instance.Instance;
import com.huawei.fit.jober.taskcenter.domain.TaskEntity;
import com.huawei.fit.jober.taskcenter.domain.TaskInstance;
import com.huawei.fit.jober.taskcenter.eventhandler.converter.InstanceConverter;
import com.huawei.fit.jober.taskcenter.service.TaskService;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * {@link InstanceFitable}对应测试类。
 *
 * @author 姚江
 * @since 2023-11-02 10:59
 */
@ExtendWith(MockitoExtension.class)
public class InstanceFitableTest {
    private InstanceFitable instanceFitable;

    @Mock
    private TaskInstance.Repo repo;

    @Mock
    private TaskService taskService;

    @Mock
    private InstanceConverter instanceConverter;

    @BeforeEach
    void beforeEach() {
        reset(this.repo);
        reset(this.taskService);
        reset(this.instanceConverter);
        instanceFitable = new InstanceFitable(taskService, this.repo, instanceConverter);
    }

    @Nested
    @DisplayName("测试list方法")
    class TestList {
        @Test
        @DisplayName("测试空taskId")
        void testNullTaskId() {
            // given
            // all args is null

            // when
            BadRequestException actualException = Assertions.assertThrows(BadRequestException.class,
                    () -> instanceFitable.list(null, null, 0, 0, false, null));

            // then
            assertEquals(ErrorCodes.TASK_ID_INVALID.getMessage(), actualException.getMessage());
        }

        @Test
        @DisplayName("测试空filter")
        void testNullFilter() {
            // given
            // all args is null

            // when
            BadRequestException actualException = Assertions.assertThrows(BadRequestException.class,
                    () -> instanceFitable.list("null", null, 0, 0, false, null));

            // then
            assertEquals(MessageFormat.format(ErrorCodes.INPUT_PARAM_IS_EMPTY.getMessage(), "filter"),
                    actualException.getMessage());
        }

        @Test
        @DisplayName("测试完整的list方法")
        void testFullList() {
            // given
            InstanceQueryFilter filter = mockFilter();
            when(repo.list(any(), any(), any(), any(), any(), any())).thenReturn(
                    mockInstanceRangedResultSet());
            when(taskService.retrieve(any(), any())).thenReturn(new TaskEntity());
            when(instanceConverter.convert(any(), any())).thenReturn(new Instance());

            // when
            RangedResultSet<Instance> set = instanceFitable.list("taskId", filter, 0L, 200, false, mockContext());

            // then
            assertEquals(0L, set.getRange().getOffset());
            assertEquals(200, set.getRange().getLimit());
            assertEquals(1L, set.getRange().getTotal());
        }

        private void mockAction() {
            when(taskService.retrieve(anyString(), any())).thenReturn(new TaskEntity());
            when(instanceConverter.convert(any(), any(TaskInstance.class))).thenReturn(new Instance());
            when(repo.list(any(), any(), any(), any(), any(), any())).thenReturn(
                    mockInstanceRangedResultSet());
        }

        private PagedResultSet<TaskInstance> mockInstanceRangedResultSet() {
            TaskInstance instance = TaskInstance.custom().build();
            return PagedResultSet.create(Collections.singletonList(instance), PaginationResult.create(0L, 200, 1L));
        }

        private InstanceQueryFilter mockFilter() {
            InstanceQueryFilter filter = new InstanceQueryFilter();
            filter.setTags(new ArrayList<>());
            filter.setCategories(null);
            filter.setIds(new ArrayList<>());
            filter.getIds().add("TEST");
            filter.setInfos(new HashMap<>());

            return filter;
        }
    }

    @Nested
    @DisplayName("测试delete方法")
    class TestDeleteTaskInstance {
        @Test
        @DisplayName("测试空taskId")
        void testNullTaskId() {
            // given
            // all args is null

            // when
            BadRequestException actualException = Assertions.assertThrows(BadRequestException.class,
                    () -> instanceFitable.deleteTaskInstance(null, null, null));

            // then
            assertEquals(ErrorCodes.TASK_ID_INVALID.getMessage(), actualException.getMessage());
        }

        @Test
        @DisplayName("测试空instanceId")
        void testNullInstanceId() {
            // given
            // all args is null

            // when
            BadRequestException actualException = Assertions.assertThrows(BadRequestException.class,
                    () -> instanceFitable.deleteTaskInstance("null", null, null));

            // then
            assertEquals(ErrorCodes.INSTANCE_ID_INVALID.getMessage(), actualException.getMessage());
        }

        @Test
        @DisplayName("测试整个delete方法")
        void testFullDeleteTaskInstance() {
            doNothing().when(repo).delete(any(), any(), any());
            assertDoesNotThrow(() -> instanceFitable.deleteTaskInstance("null", "null", mockContext()));
        }
    }

    @Nested
    @DisplayName("测试patch方法")
    class TestPatchTaskInstance {
        @Test
        @DisplayName("测试空taskId")
        void testNullTaskId() {
            // when
            BadRequestException actualException = Assertions.assertThrows(BadRequestException.class,
                    () -> instanceFitable.patchTaskInstance(null, null, null, null));

            // then
            assertEquals(ErrorCodes.TASK_ID_INVALID.getMessage(), actualException.getMessage());
        }

        @Test
        @DisplayName("测试空instanceId")
        void testNullInstanceId() {
            // when
            BadRequestException actualException = Assertions.assertThrows(BadRequestException.class,
                    () -> instanceFitable.patchTaskInstance("null", null, null, null));

            // then
            assertEquals(ErrorCodes.INSTANCE_ID_INVALID.getMessage(), actualException.getMessage());
        }

        @Test
        @DisplayName("测试空instanceInfo")
        void testNullInstanceInfo() {
            // when
            BadRequestException actualException = Assertions.assertThrows(BadRequestException.class,
                    () -> instanceFitable.patchTaskInstance("null", "null", null, null));

            // then
            assertEquals(MessageFormat.format(ErrorCodes.INPUT_PARAM_IS_EMPTY.getMessage(), "instanceInfo"),
                    actualException.getMessage());
        }

        @Test
        @DisplayName("测试整个patch方法")
        void testFullPatchTaskInstance() {
            doNothing().when(repo).patch(any(), anyString(), any(), any());
            assertDoesNotThrow(
                    () -> instanceFitable.patchTaskInstance("null", "null", new InstanceInfo(), mockContext()));
        }
    }

    @Nested
    @DisplayName("测试create方法")
    class TestCreateTaskInstance {
        @Test
        @DisplayName("测试空taskId")
        void testNullTaskId() {
            // given
            // all args is null

            // when
            BadRequestException actualException = Assertions.assertThrows(BadRequestException.class,
                    () -> instanceFitable.createTaskInstance(null, null, null));

            // then
            assertEquals(ErrorCodes.TASK_ID_INVALID.getMessage(), actualException.getMessage());
        }

        @Test
        @DisplayName("测试空instanceInfo")
        void testNullInstanceInfo() {
            // given
            // all args is null

            // when
            BadRequestException actualException = Assertions.assertThrows(BadRequestException.class,
                    () -> instanceFitable.createTaskInstance("null", null, null));

            // then
            assertEquals(MessageFormat.format(ErrorCodes.INPUT_PARAM_IS_EMPTY.getMessage(), "instanceInfo"),
                    actualException.getMessage());
        }

        @Test
        @DisplayName("测试整个create方法")
        void testFullCreateTaskInstance() {
            // given
            when(repo.create(any(), any(), any())).thenReturn(TaskInstance.custom().build());
            when(taskService.retrieve(anyString(), any())).thenReturn(new TaskEntity());
            when(instanceConverter.convert(any(), any(TaskInstance.class))).thenReturn(new Instance());
            // when
            assertDoesNotThrow(
                    () -> instanceFitable.createTaskInstance("null", mockInstanceInfo(), mockContext()));
            // then
            verify(instanceConverter, times(1)).convert(any(), any(TaskInstance.class));
        }

        private InstanceInfo mockInstanceInfo() {
            return new InstanceInfo("1", "2", new HashMap<>(), new ArrayList<>());
        }
    }

    @Nested
    @DisplayName("测试recover方法")
    class TestRecoverTaskInstance {
        @Test
        @DisplayName("测试空taskId")
        void testNullTaskId() {
            // given
            // all args is null

            // when
            BadRequestException actualException = Assertions.assertThrows(BadRequestException.class,
                    () -> instanceFitable.recoverTaskInstance(null, null, null));

            // then
            assertEquals(ErrorCodes.TASK_ID_INVALID.getMessage(), actualException.getMessage());
        }

        @Test
        @DisplayName("测试空instanceId")
        void testNullInstanceId() {
            // given
            // all args is null

            // when
            BadRequestException actualException = Assertions.assertThrows(BadRequestException.class,
                    () -> instanceFitable.recoverTaskInstance("null", null, null));

            // then
            assertEquals(ErrorCodes.INSTANCE_ID_INVALID.getMessage(), actualException.getMessage());
        }

        @Test
        @DisplayName("测试整个recover方法")
        void testFullDeleteTaskInstance() {
            doNothing().when(repo).recover(any(), anyString(), any());
            assertDoesNotThrow(() -> instanceFitable.recoverTaskInstance("null", "null", mockContext()));
        }
    }

    private OperationContext mockContext() {
        return new OperationContext("TEST_TENANT", "TEST", "192.168.1.11", null, null);
    }
}
