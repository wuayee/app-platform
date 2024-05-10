/*
 *  Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.fitable;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.huawei.fit.jane.Undefinable;
import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jane.meta.multiversion.instance.InstanceDeclarationInfo;
import com.huawei.fit.jane.meta.multiversion.instance.MetaInstanceFilter;
import com.huawei.fit.jane.task.util.PagedResultSet;
import com.huawei.fit.jane.task.util.PaginationResult;
import com.huawei.fit.jober.common.ErrorCodes;
import com.huawei.fit.jober.common.exceptions.BadRequestException;
import com.huawei.fit.jober.taskcenter.domain.TaskEntity;
import com.huawei.fit.jober.taskcenter.domain.TaskInstance;
import com.huawei.fit.jober.taskcenter.domain.TaskType;
import com.huawei.fit.jober.taskcenter.eventhandler.converter.MetaInstanceConverter;
import com.huawei.fit.jober.taskcenter.service.TaskService;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class MetaInstanceFitableTest {
    private MetaInstanceFitable metaInstanceFitable;

    @Mock
    private TaskService taskService;

    @Mock
    private TaskInstance.Repo repo;

    @Mock
    MetaInstanceConverter converter;

    @BeforeEach
    void before() {
        this.metaInstanceFitable = new MetaInstanceFitable(this.taskService, this.repo, this.converter);
    }

    @Nested
    @DisplayName("测试create方法")
    class TestCreate {
        @Test
        @DisplayName("测试空metaId")
        void testCreateWithNullMetaId() {
            // when
            BadRequestException actualException = Assertions.assertThrows(BadRequestException.class,
                    () -> metaInstanceFitable.createMetaInstance(null, null, null));

            // then
            Assertions.assertEquals(ErrorCodes.TASK_ID_INVALID.getErrorCode(), actualException.getCode());
        }

        @Test
        @DisplayName("测试空instanceDeclarationInfo")
        void testCreateWithNullInstanceDeclarationInfo() {
            // when
            BadRequestException actualException = Assertions.assertThrows(BadRequestException.class,
                    () -> metaInstanceFitable.createMetaInstance("meta_id", null, null));

            // then
            Assertions.assertEquals(ErrorCodes.INPUT_PARAM_IS_EMPTY.getErrorCode(), actualException.getCode());
        }

        @Test
        @DisplayName("测试创建meta成功")
        void testCreateSuccessfully() {
            // given
            when(taskService.retrieve(anyString(), any())).thenReturn(mockEntity());
            when(converter.convert(any(InstanceDeclarationInfo.class)))
                    .thenReturn(TaskInstance.Declaration.custom().build());
            when(repo.create(any(), any(), any())).thenReturn(TaskInstance.custom().build());

            // when
            Assertions.assertDoesNotThrow(
                    () -> metaInstanceFitable.createMetaInstance("meta_id", mockInstanceDeclarationInfo(),
                            mockContext()));

            // then
            verify(converter, times(1)).convert(any(), any());
        }
    }

    @Nested
    @DisplayName("测试patch方法")
    class TestPatch {
        @Test
        @DisplayName("测试空metaId")
        void testPatchWithNullMetaId() {
            // when
            BadRequestException actualException = Assertions.assertThrows(BadRequestException.class,
                    () -> metaInstanceFitable.patchMetaInstance(null, null, null, null));

            // then
            Assertions.assertEquals(ErrorCodes.TASK_ID_INVALID.getErrorCode(), actualException.getCode());
        }

        @Test
        @DisplayName("测试空InstanceId")
        void testPatchWithNullInstance() {
            // when
            BadRequestException actualException = Assertions.assertThrows(BadRequestException.class,
                    () -> metaInstanceFitable.patchMetaInstance("meta_id", null, null, null));

            // then
            Assertions.assertEquals(ErrorCodes.INPUT_PARAM_IS_EMPTY.getErrorCode(), actualException.getCode());
        }

        @Test
        @DisplayName("测试空instanceDeclarationInfo")
        void testPatchWithNullDeclaration() {
            // when
            BadRequestException actualException = Assertions.assertThrows(BadRequestException.class,
                    () -> metaInstanceFitable.patchMetaInstance("meta_id", "instance_id", null, null));

            // then
            Assertions.assertEquals(ErrorCodes.INPUT_PARAM_IS_EMPTY.getErrorCode(), actualException.getCode());
        }

        @Test
        @DisplayName("测试patch流程")
        void testPatchSuccessfully() {
            // given
            when(converter.convert(any(InstanceDeclarationInfo.class)))
                    .thenReturn(TaskInstance.Declaration.custom().build());
            doNothing().when(repo).patch(any(), anyString(), any(), any());

            // when
            Assertions.assertDoesNotThrow(
                    () -> metaInstanceFitable.patchMetaInstance("meta_id", "instance_id", mockInstanceDeclarationInfo(),
                            mockContext()));
        }
    }

    @Nested
    @DisplayName("测试delete方法")
    class TestDelete {
        @Test
        @DisplayName("测试空metaId")
        void testDeleteWithNullMetaId() {
            // when
            BadRequestException actualException = Assertions.assertThrows(BadRequestException.class,
                    () -> metaInstanceFitable.deleteMetaInstance(null, null, null));

            // then
            Assertions.assertEquals(ErrorCodes.TASK_ID_INVALID.getErrorCode(), actualException.getCode());
        }

        @Test
        @DisplayName("测试空instanceId")
        void testDeleteWithNullInstanceId() {
            // when
            BadRequestException actualException = Assertions.assertThrows(BadRequestException.class,
                    () -> metaInstanceFitable.deleteMetaInstance("meta_id", null, null));

            // then
            Assertions.assertEquals(ErrorCodes.INPUT_PARAM_IS_EMPTY.getErrorCode(), actualException.getCode());
        }

        @Test
        @DisplayName("测试delete流程")
        void testDeleteSuccessfully() {
            // given
            doNothing().when(repo).delete(any(), anyString(), any());

            // when
            Assertions.assertDoesNotThrow(
                    () -> metaInstanceFitable.deleteMetaInstance("meta_id", "instance_id", mockContext()));
        }
    }

    @Nested
    @DisplayName("测试list方法")
    class TestList {
        @Test
        @DisplayName("测试空metaId")
        void testDeleteWithNullMetaId() {
            // when
            BadRequestException actualException = Assertions.assertThrows(BadRequestException.class,
                    () -> metaInstanceFitable.list(null, null, 0L, 1, null));

            // then
            Assertions.assertEquals(ErrorCodes.TASK_ID_INVALID.getErrorCode(), actualException.getCode());
        }

        @Test
        @DisplayName("测试空filter")
        void testDeleteWithNullFilter() {
            // when
            BadRequestException actualException = Assertions.assertThrows(BadRequestException.class,
                    () -> metaInstanceFitable.list("meta_id", null, 0L, 1, null));

            // then
            Assertions.assertEquals(ErrorCodes.INPUT_PARAM_IS_EMPTY.getErrorCode(), actualException.getCode());
        }

        @Test
        @DisplayName("测试list流程")
        void testListSuccessfully() {
            // given
            when(taskService.retrieve(anyString(), any())).thenReturn(mockEntity());
            when(repo.list(any(), any(), any(), any(), any(), any())).thenReturn(mockResultSet());
            when(converter.convert(any(MetaInstanceFilter.class))).thenReturn(TaskInstance.Filter.custom().build());

            // when
            Assertions.assertDoesNotThrow(
                    () -> metaInstanceFitable.list("meta_id", new MetaInstanceFilter(), 0L, 10, mockContext()));

            // then
            verify(taskService, times(1)).retrieve(anyString(), any());
            verify(converter, times(1)).convert(any(TaskEntity.class), any(TaskInstance.class));
        }
    }

    private OperationContext mockContext() {
        return new OperationContext("TEST_TENANT", "TEST_USER", "", "", "", "", "", "", "");
    }

    private TaskEntity mockEntity() {
        TaskEntity task = new TaskEntity();
        TaskType taskType = TaskType.custom().build();
        task.setName("testType");
        task.setId("meta_id");
        task.setTypes(Collections.singletonList(taskType));
        return task;
    }

    private InstanceDeclarationInfo mockInstanceDeclarationInfo() {
        InstanceDeclarationInfo instanceDeclarationInfo = new InstanceDeclarationInfo();
        Undefinable<Map<String, Object>> info = new Undefinable<>(true, new HashMap<>());
        Undefinable<List<String>> tags = new Undefinable<>(true, new ArrayList<>());
        instanceDeclarationInfo.setInfo(info);
        instanceDeclarationInfo.setTags(tags);
        return instanceDeclarationInfo;
    }

    private TaskInstance mockInstanceEntity() {
        return TaskInstance.custom().id("instance_id").build();
    }

    private PagedResultSet<TaskInstance> mockResultSet() {
        List<TaskInstance> instances = new ArrayList<>();
        instances.add(this.mockInstanceEntity());
        return PagedResultSet.create(instances, PaginationResult.create(0L, 10, 1L));
    }
}
