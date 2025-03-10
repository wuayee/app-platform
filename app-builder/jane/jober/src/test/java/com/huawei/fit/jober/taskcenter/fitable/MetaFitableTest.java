/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.fitable;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import modelengine.fit.jane.Undefinable;

import modelengine.fit.jane.common.entity.OperationContext;
import modelengine.fit.jane.meta.definition.Meta;
import modelengine.fit.jane.meta.definition.MetaDeclarationInfo;
import modelengine.fit.jane.meta.definition.MetaFilter;
import modelengine.fit.jane.meta.property.MetaPropertyDeclarationInfo;
import modelengine.fit.jane.task.domain.TaskProperty;

import modelengine.fit.jober.common.ErrorCodes;
import modelengine.fit.jober.common.exceptions.BadRequestException;
import modelengine.fit.jober.taskcenter.declaration.TaskDeclaration;
import modelengine.fit.jober.taskcenter.domain.TaskEntity;
import modelengine.fit.jober.taskcenter.domain.TaskType;
import modelengine.fit.jober.taskcenter.eventhandler.converter.MetaConverter;
import modelengine.fit.jober.taskcenter.eventhandler.converter.MetaPropertyConverter;
import modelengine.fit.jober.taskcenter.service.TaskService;

import modelengine.fitframework.model.RangedResultSet;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class MetaFitableTest {
    private MetaFitable metaFitable;

    @Mock
    TaskService taskService;

    @Mock
    TaskProperty.Repo repo;

    @Mock
    MetaConverter metaConverter;

    @Mock
    MetaPropertyConverter metaPropertyConverter;

    @Mock
    TaskType.Repo taskType;

    @BeforeEach
    void before() {
        this.metaFitable = new MetaFitable(taskService, repo, metaConverter, metaPropertyConverter, taskType);
    }

    @Nested
    @DisplayName("测试create方法")
    class TestCreate {
        @Test
        @DisplayName("测试空metaDeclarationInfo")
        void testCreateWithNullDeclaration() {
            // when
            BadRequestException actualException = Assertions.assertThrows(BadRequestException.class,
                    () -> metaFitable.create(null, null));

            // then
            Assertions.assertEquals(ErrorCodes.INPUT_PARAM_IS_EMPTY.getErrorCode(), actualException.getCode());
        }

        @Test
        @DisplayName("测试创建meta成功")
        void testCreateSuccessfully() {
            // given
            when(taskService.create(any(), any())).thenReturn(mockEntity());
            when(taskType.create(any(), any(), any())).thenReturn(TaskType.custom().build());
            when(metaConverter.convert(any(MetaDeclarationInfo.class))).thenReturn(new TaskDeclaration());
            when(metaConverter.convert(any(), any())).thenReturn(new Meta());

            // when
            Assertions.assertDoesNotThrow(() -> metaFitable.create(mockDeclarationInfo(), mockContext()));

            // then
            verify(metaConverter, times(1)).convert(any(), any());
        }
    }

    @Nested
    @DisplayName("测试patch方法")
    class TestPatch {
        @Test
        @DisplayName("测试空metaDeclarationInfo")
        void testPatchWithNullDeclaration() {
            // when
            BadRequestException actualException = Assertions.assertThrows(BadRequestException.class,
                    () -> metaFitable.patch("test_id", null, null));

            // then
            Assertions.assertEquals(ErrorCodes.INPUT_PARAM_IS_EMPTY.getErrorCode(), actualException.getCode());
        }

        @Test
        @DisplayName("测试patch流程")
        void testPatchSuccessfully() {
            // given
            when(metaConverter.convert(any(MetaDeclarationInfo.class))).thenReturn(new TaskDeclaration());

            // when
            Assertions.assertDoesNotThrow(() -> metaFitable.patch("test_id", mockDeclarationInfo(), mockContext()));
        }
    }

    @Nested
    @DisplayName("测试delete方法")
    class TestDelete {
        @Test
        @DisplayName("测试delete流程")
        void testDeleteSuccessfully() {
            // given
            doNothing().when(taskType).deleteByTasks(anyString(), any());
            doNothing().when(taskService).delete(anyString(), any());

            // when
            Assertions.assertDoesNotThrow(() -> metaFitable.delete("test_id", mockContext()));
        }
    }

    @Nested
    @DisplayName("测试list方法")
    class TestList {
        @Test
        @DisplayName("测试list流程")
        void testListSuccessfully() {
            // given
            when(taskService.list(any(), anyLong(), anyInt(), any())).thenReturn(mockResultSet());

            // when
            Assertions.assertDoesNotThrow(() -> metaFitable.list(new MetaFilter(), 0L, 10, mockContext()));

            // then
            verify(taskService, times(1)).list(any(), anyLong(), anyInt(), any());
            verify(metaConverter, times(1)).convert(any(), any());
        }
    }

    @Nested
    @DisplayName("测试retrieve方法")
    class TestRetrieve {
        @Test
        @DisplayName("测试retrieve流程")
        void testDeleteSuccessfully() {
            // given
            when(taskService.retrieve(anyString(), any())).thenReturn(new TaskEntity());

            // when
            Assertions.assertDoesNotThrow(() -> metaFitable.retrieve("test_id", mockContext()));

            // then
            verify(metaConverter, times(1)).convert(any(), any());
        }
    }

    @Nested
    @DisplayName("测试Property的创建方法")
    class TestCreateProperty {
        @Test
        @DisplayName("测试正常创建")
        void testCreateSuccessfully() {
            // given

            // when
            metaFitable.createProperty("test_id", mockPropertyDeclaration(), mockContext());
            // then
        }
    }

    @Nested
    @DisplayName("测试Property的创建方法")
    class TestPatchProperty {
        @Test
        @DisplayName("测试正常创建")
        void testPatchSuccessfully() {
            // given

            // when
            metaFitable.patchProperty("test_id", "test_property_id", mockPropertyDeclaration(), mockContext());
            // then
        }
    }

    @Nested
    @DisplayName("测试Property的创建方法")
    class TestDeleteProperty {
        @Test
        @DisplayName("测试正常创建")
        void testDeleteSuccessfully() {
            // given

            // when
            metaFitable.deleteProperty("test_id", "test_property_id", mockContext());
            // then
        }
    }

    private MetaPropertyDeclarationInfo mockPropertyDeclaration() {
        return new MetaPropertyDeclarationInfo(Undefinable.defined("name"), Undefinable.defined("dataType"),
                Undefinable.defined("description"), Undefinable.defined(false), Undefinable.defined(false),
                Undefinable.defined("scope"), Undefinable.defined(new HashMap<>()));
    }

    private OperationContext mockContext() {
        return new OperationContext("TEST_TENANT", "TEST_USER", "", "", "", "", "", "", "");
    }

    private MetaDeclarationInfo mockDeclarationInfo() {
        MetaDeclarationInfo metaDeclarationInfo = new MetaDeclarationInfo();
        Undefinable<String> name = new Undefinable<>(true, "test_meta");
        Undefinable<Map<String, Object>> attributes = new Undefinable<>(true, new HashMap<>());
        Undefinable<List<MetaPropertyDeclarationInfo>> properties = new Undefinable<>(true, new ArrayList<>());
        metaDeclarationInfo.setName(name);
        metaDeclarationInfo.setAttributes(attributes);
        metaDeclarationInfo.setProperties(properties);
        return metaDeclarationInfo;
    }

    private TaskEntity mockEntity() {
        TaskEntity task = new TaskEntity();
        task.setName("test_meta");
        task.setId("meta_id");
        return task;
    }

    private RangedResultSet<TaskEntity> mockResultSet() {
        List<TaskEntity> entityList = new ArrayList<>();
        entityList.add(this.mockEntity());
        return RangedResultSet.create(entityList, 0, 10, 1);
    }
}