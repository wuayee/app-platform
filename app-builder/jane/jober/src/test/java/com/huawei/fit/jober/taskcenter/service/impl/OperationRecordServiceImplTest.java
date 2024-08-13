/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import com.huawei.fit.jane.task.domain.TaskProperty;
import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jane.task.util.UndefinableValue;
import com.huawei.fit.jober.common.ServerInternalException;
import com.huawei.fit.jober.common.exceptions.BadRequestException;
import com.huawei.fit.jober.taskcenter.declaration.OperationRecordDeclaration;
import com.huawei.fit.jober.taskcenter.domain.OperationRecordEntity;
import com.huawei.fit.jober.taskcenter.domain.TaskEntity;
import com.huawei.fit.jober.taskcenter.filter.OperationRecordFilter;
import com.huawei.fit.jober.taskcenter.service.OperationRecordService;
import com.huawei.fit.jober.taskcenter.service.TaskService;
import com.huawei.fit.jober.taskcenter.util.DynamicSqlExecutor;
import com.huawei.fit.jober.taskcenter.validation.TaskValidator;
import com.huawei.fitframework.model.RangedResultSet;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link OperationRecordServiceImpl}对应测试类
 *
 * @author 姚江
 * @since 2023-11-21 18:57
 */
@ExtendWith(MockitoExtension.class)
public class OperationRecordServiceImplTest {
    @Mock
    TaskValidator taskValidator;

    @Mock
    DynamicSqlExecutor executor;

    @Mock
    TaskService taskService;

    OperationRecordService service;

    OperationRecordDeclaration declaration;

    OperationContext context;

    @BeforeEach
    public void before() {
        reset(taskValidator);
        reset(executor);

        service = new OperationRecordServiceImpl(executor, taskValidator, taskService);

        declaration = new OperationRecordDeclaration();
        declaration.setMessage(UndefinableValue.defined("test"));
        declaration.setOperate(UndefinableValue.defined("CREATE"));
        declaration.setObjectType(UndefinableValue.defined("TASK"));
        declaration.setObjectId(UndefinableValue.defined("fad639199a4c4133b22a40cae0c6c096"));

        context = OperationContext.custom().operator("UT").operatorIp("127.0.0.1").build();
    }

    @Nested
    @DisplayName("测试 create 方法")
    class TestCreate {
        @DisplayName("测试数据库插入失败")
        @Test
        void testInsertFailed() {
            // given
            when(executor.executeUpdate(anyString(), anyList())).thenReturn(0);

            // when
            ServerInternalException e = Assertions.assertThrows(ServerInternalException.class,
                    () -> service.create(declaration, context));

            Assertions.assertEquals("Failed to insert operation record into database.", e.getMessage());
        }

        @DisplayName("测试数据库插入成功")
        @Test
        void testInsertSuccess() {
            // given
            when(executor.executeUpdate(anyString(), anyList())).thenReturn(1);

            // when
            Assertions.assertDoesNotThrow(() -> service.create(declaration, context));
        }

        @DisplayName("测试 declaration 为空的情况")
        @Test
        void throwExceptionWhenDeclarationIsNull() {
            BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                    () -> service.create(null, context));

            Assertions.assertEquals("Operation record declaration is null.", exception.getMessage());
        }

        @DisplayName("测试 declaration field message 为空的情况")
        @Test
        void throwExceptionWhenDeclarationFieldMessageIsNull() {
            BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                    () -> service.create(new OperationRecordDeclaration(), context));

            Assertions.assertEquals("Operation record declaration field message is null.", exception.getMessage());
        }

        @DisplayName("测试 declaration objectId 为空的情况")
        @Test
        void throwExceptionWhenDeclarationFieldObjectIdIsNull() {
            OperationRecordDeclaration d = new OperationRecordDeclaration();
            d.setMessage(UndefinableValue.defined("message"));
            BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                    () -> service.create(d, context));

            Assertions.assertEquals("Operation record declaration field objectId is null.", exception.getMessage());
        }

        @DisplayName("测试 declaration objectType 为空的情况")
        @Test
        void throwExceptionWhenDeclarationFieldObjectTypeIsNull() {
            OperationRecordDeclaration d = new OperationRecordDeclaration();
            d.setMessage(UndefinableValue.defined("message"));
            d.setObjectId(UndefinableValue.defined("objectId"));
            BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                    () -> service.create(d, context));

            Assertions.assertEquals("Operation record declaration field objectType is null.", exception.getMessage());
        }

        @DisplayName("测试 declaration field operate 为空的情况")
        @Test
        void throwExceptionWhenDeclarationFieldOperateIsNull() {
            OperationRecordDeclaration d = new OperationRecordDeclaration();
            d.setMessage(UndefinableValue.defined("message"));
            d.setObjectId(UndefinableValue.defined("objectId"));
            d.setObjectType(UndefinableValue.defined("objectType"));

            BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                    () -> service.create(d, context));

            Assertions.assertEquals("Operation record declaration field operate is null.", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("测试 list 方法")
    class TestList {
        @Test
        @DisplayName("测试 filter 为空的情况")
        void testFilterIsNull() {
            BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                    () -> service.list(null, 0L, 0, context));
            Assertions.assertEquals("Filter is empty!", exception.getMessage());
        }

        @Test
        @DisplayName("测试 filter 中 types 为空的情况")
        void testFilterTypesIsEmpty() {
            OperationRecordFilter filter = new OperationRecordFilter();
            BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                    () -> service.list(filter, 0L, 0, context));
            Assertions.assertEquals("Operation record list filter field objectTypes is empty.", exception.getMessage());
        }

        @Test
        @DisplayName("测试 filter 中 ids 为空的情况")
        void testFilterIdsIsEmpty() {
            OperationRecordFilter filter = new OperationRecordFilter();
            List<String> types = new ArrayList<>();
            types.add("type");
            filter.setObjectTypes(UndefinableValue.defined(types));
            BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                    () -> service.list(filter, 0L, 0, context));
            Assertions.assertEquals("Operation record list filter field objectIds is empty.", exception.getMessage());
        }

        @Test
        @DisplayName("测试成功")
        void testSuccess() {
            // given
            when(executor.executeScalar(startsWith("SELECT COUNT(*)"), anyList())).thenReturn(10L);

            OperationRecordFilter filter = new OperationRecordFilter();
            List<String> types = new ArrayList<>();
            types.add("type");
            filter.setObjectTypes(UndefinableValue.defined(types));
            filter.setObjectIds(UndefinableValue.defined(types));

            Map<String, Object> row = new HashMap<>();
            row.put("operate", "UPDATED");
            row.put("id", "37ea22e4a46c453baa3e07b1a3043b2f");
            row.put("object_id", "37ea22e4a46c453baa3e07b1a3043b2f");
            row.put("object_type", "INSTANCE");
            row.put("operator", "UT");
            row.put("operated_time", new Timestamp(1703922141));
            row.put("message",
                    "{\"declaration\": {\"info\": {\"owner\": \"李孟迪 30030239\",\"status\": \"I\",\"title\": \"lmd20221014-0003\"}},\"detail\": \"8b174cea74204560aa0d209a60170043\",\"title\": \"更新任务实例\"}");
            List<Map<String, Object>> rows = new ArrayList<>();
            rows.add(row);
            when(executor.executeQuery(anyString(), anyList())).thenReturn(rows);
            when(executor.executeScalar(startsWith("select task_id from task_instance_wide"), anyList())).thenReturn(
                    "37ea22e4a46c453baa3e07b1a3043b2f");
            when(taskService.retrieve(anyString(), any())).thenReturn(mockTaskEntity());

            // when
            RangedResultSet<OperationRecordEntity> list = service.list(filter, 0L, 10, context);

            // verify
            List<OperationRecordEntity> results = list.getResults();
            Assertions.assertEquals(1, results.size());
            Assertions.assertEquals("UPDATED", results.get(0).getOperate());
        }

        @Test
        @DisplayName("测试任务关联成功")
        void testSuccessByRel() {
            // given
            when(executor.executeScalar(startsWith("SELECT COUNT(*)"), anyList())).thenReturn(10L);

            OperationRecordFilter filter = new OperationRecordFilter();
            List<String> types = new ArrayList<>();
            types.add("type");
            filter.setObjectTypes(UndefinableValue.defined(types));
            filter.setObjectIds(UndefinableValue.defined(types));

            Map<String, Object> row = new HashMap<>();
            row.put("operate", "RELADD");
            row.put("id", "37ea22e4a46c453baa3e07b1a3043b2f");
            row.put("object_id", "37ea22e4a46c453baa3e07b1a3043b2f");
            row.put("object_type", "INSTANCE");
            row.put("operator", "UT");
            row.put("operated_time", new Timestamp(1703922141));
            row.put("message",
                    "{\"declaration\": {\"info\": {\"owner\": \"李孟迪 30030239\",\"status\": \"I\",\"title\": \"lmd20221014-0003\"}},\"detail\": \"8b174cea74204560aa0d209a60170043\",\"title\": \"更新任务实例\"}");
            List<Map<String, Object>> rows = new ArrayList<>();
            rows.add(row);
            when(executor.executeQuery(anyString(), anyList())).thenReturn(rows);

            // when
            RangedResultSet<OperationRecordEntity> list = service.list(filter, 0L, 10, context);

            // verify
            List<OperationRecordEntity> results = list.getResults();
            Assertions.assertEquals(1, results.size());
            Assertions.assertEquals("RELADD", results.get(0).getOperate());
        }

        TaskEntity mockTaskEntity() {
            TaskEntity taskEntity = new TaskEntity();
            taskEntity.setId("37ea22e4a46c453baa3e07b1a3043b2f");
            taskEntity.setName("UT_TASK");
            taskEntity.setProperties(new ArrayList<>());

            String[] keys = new String[] {"owner", "status", "title"};
            String[] names = new String[] {"拥有者", "状态", "标题"};
            for (int i = 0; i < 3; i++) {
                Map<String, Object> appearance = new HashMap<>();
                appearance.put("name", names[i]);
                if ("status".equals(keys[i])) {
                    appearance.put("displayType", "select");
                    appearance.put("options", mockOptions());
                }
                taskEntity.getProperties().add(TaskProperty.custom().name(keys[i]).appearance(appearance).build());
            }
            return taskEntity;
        }

        List<Map<String, String>> mockOptions() {
            List<Map<String, String>> options = new ArrayList<>();
            options.add(new HashMap<>());
            options.get(0).put("value", "I");
            options.get(0).put("text", "正在工作");
            return options;
        }
    }
}
