/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.domain.postgresql;

import com.huawei.fit.jane.task.domain.PropertyDataType;
import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jober.common.ServerInternalException;
import com.huawei.fit.jober.common.exceptions.BadRequestException;
import com.huawei.fit.jober.common.exceptions.ConflictException;
import com.huawei.fit.jober.common.exceptions.NotFoundException;
import com.huawei.fit.jober.taskcenter.domain.TaskTemplateProperty;
import com.huawei.fit.jober.taskcenter.util.DynamicSqlExecutor;
import com.huawei.fit.jober.taskcenter.validation.PropertyValidator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 为 {@link PostgresqlTaskPropertyRepo} 提供测试
 *
 * @author 姚江
 * @since 2023-12-06
 */
@ExtendWith(MockitoExtension.class)
public class PostgresqlTaskTemplatePropertyRepoTest {
    @Mock
    private DynamicSqlExecutor executor;

    @Mock
    private PropertyValidator validator;

    private final OperationContext context = OperationContext.custom().operator("UT").build();

    TaskTemplateProperty.Repo repo;

    String[] taskTemplateIds = new String[] {
            "d64e866a0cba4fc8a68e0c15ee4942df", "d8a1ed6da1de44bbbe243adb30942f27", "748501de3a6c48f2a67c7b1e6fcbeabe"
    };

    String[] ids = new String[] {
            "00a8ce17c4f34c0ea266cde5de72b6c0", "c25d293fa9544c6daafd73905d896bad", "f0676858e3cb469c8202ca55fdc5661a"
    };

    @BeforeEach
    void before() {
        repo = new PostgresqlTaskTemplatePropertyRepo(executor, validator);
    }

    @Nested
    @DisplayName("创建方法测试")
    class CreateTest {
        @Test
        @DisplayName("测试成功创建")
        void test01() {
            Mockito.when(executor.executeUpdate(Mockito.anyString(), Mockito.anyList())).thenReturn(1);
            List<TaskTemplateProperty.Declaration> declarations = new LinkedList<>();
            declarations.add(TaskTemplateProperty.Declaration.custom().name("UT_name_1").build());

            List<TaskTemplateProperty> properties = repo.create(taskTemplateIds[0], declarations, context);

            Assertions.assertEquals(taskTemplateIds[0], properties.get(0).taskTemplateId());

            Assertions.assertEquals("UT_name_1", properties.get(0).name());
            Assertions.assertEquals(PropertyDataType.TEXT, properties.get(0).dataType());
            Assertions.assertEquals(1, properties.get(0).sequence());
        }

        @Test
        @DisplayName("测试创建失败：名称重复")
        void test02() {
            String taskTemplateId = taskTemplateIds[0];
            List<TaskTemplateProperty.Declaration> declarations = new LinkedList<>();
            declarations.add(TaskTemplateProperty.Declaration.custom().name("UT_name_1").build());
            declarations.add(TaskTemplateProperty.Declaration.custom().name("UT_name_1").build());

            ConflictException conflictException = Assertions.assertThrows(ConflictException.class,
                    () -> repo.create(taskTemplateId, declarations, context));

            Assertions.assertEquals("The task template property with the same already exist.",
                    conflictException.getMessage());
        }

        @Test
        @DisplayName("测试创建失败：数据库插入错误")
        void test03() {
            Mockito.when(executor.executeUpdate(Mockito.anyString(), Mockito.anyList())).thenReturn(0);
            String taskTemplateId = taskTemplateIds[0];
            List<TaskTemplateProperty.Declaration> declarations = new LinkedList<>();
            declarations.add(TaskTemplateProperty.Declaration.custom().name("UT_name_1").build());

            ServerInternalException exception = Assertions.assertThrows(ServerInternalException.class,
                    () -> repo.create(taskTemplateId, declarations, context));

            Assertions.assertEquals("An error occurred while inserting values into the table task_template_property.",
                    exception.getMessage());
        }
    }

    @Nested
    @DisplayName("更新方法测试")
    class PatchTest {
        @Test
        @DisplayName("修改成功")
        void test01() {
            this.mockRows();
            Mockito.when(executor.executeScalar(Mockito.anyString(), Mockito.anyList())).thenReturn(null);
            Mockito.when(executor.executeUpdate(Mockito.anyString(), Mockito.anyList())).thenReturn(1);
            String ttId = taskTemplateIds[0];
            TaskTemplateProperty.Declaration declaration = TaskTemplateProperty.Declaration.custom()
                    .name("UT_name_1")
                    .dataType("INTEGER")
                    .build();
            repo.patch(ttId, ids[0], declaration, context);
        }

        @Test
        @DisplayName("修改失败：模板属性被使用")
        void test02() {
            this.mockRows();
            Mockito.when(executor.executeScalar(Mockito.anyString(), Mockito.anyList())).thenReturn(1);
            String ttId = taskTemplateIds[0];
            TaskTemplateProperty.Declaration declaration = TaskTemplateProperty.Declaration.custom()
                    .name("UT_name_1")
                    .dataType("INTEGER")
                    .build();
            ConflictException conflictException = Assertions.assertThrows(ConflictException.class,
                    () -> repo.patch(ttId, ids[0], declaration, context));

            Assertions.assertEquals("The property cannot be modified or deleted when the property is used.",
                    conflictException.getMessage());
        }

        @Test
        @DisplayName("修改失败：名称和旧名称相同")
        void test03() {
            this.mockRows();
            String ttId = taskTemplateIds[0];
            TaskTemplateProperty.Declaration declaration = TaskTemplateProperty.Declaration.custom()
                    .name("UT_name_modify")
                    .dataType("INTEGER")
                    .build();
            BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                    () -> repo.patch(ttId, ids[0], declaration, context));

            Assertions.assertEquals("The old property name is equals new name.", exception.getMessage());
        }

        @Test
        @DisplayName("修改失败：名称重复")
        void test04() {
            this.mockRows();
            String ttId = taskTemplateIds[0];
            TaskTemplateProperty.Declaration declaration = TaskTemplateProperty.Declaration.custom()
                    .name("UT_name_modify_2")
                    .dataType("INTEGER")
                    .build();
            ConflictException exception = Assertions.assertThrows(ConflictException.class,
                    () -> repo.patch(ttId, ids[0], declaration, context));

            Assertions.assertEquals("The task template property with the same already exist.", exception.getMessage());
        }

        @Test
        @DisplayName("修改失败：数据类型和原有类型相同")
        void test05() {
            this.mockRows();
            String ttId = taskTemplateIds[0];
            TaskTemplateProperty.Declaration declaration = TaskTemplateProperty.Declaration.custom()
                    .name("UT_name_1")
                    .dataType("TEXT")
                    .build();
            BadRequestException exception = Assertions.assertThrows(BadRequestException.class,
                    () -> repo.patch(ttId, ids[0], declaration, context));

            Assertions.assertEquals("The old property dataType is equals new dataType.", exception.getMessage());
        }

        private void mockRows() {
            Map<String, Object> row1 = new HashMap<>();
            row1.put("id", ids[0]);
            row1.put("data_type", "TEXT");
            row1.put("sequence", 1);
            row1.put("task_template_id", taskTemplateIds[0]);
            row1.put("name", "UT_name_modify");
            Map<String, Object> row2 = new HashMap<>();
            row2.put("id", ids[1]);
            row2.put("data_type", "TEXT");
            row2.put("sequence", 2);
            row2.put("task_template_id", taskTemplateIds[0]);
            row2.put("name", "UT_name_modify_2");
            Mockito.when(executor.executeQuery(
                            Mockito.endsWith("WHERE task_template_id in (SELECT find_template_parents(?) AS id)"),
                            Mockito.anyList()))
                    .thenReturn(Arrays.asList(row1, row2));
        }
    }

    @Nested
    @DisplayName("检索方法测试")
    class RetrieveTest {
        @Test
        @DisplayName("检索成功")
        public void test01() {
            Map<String, Object> row = new HashMap<>();
            row.put("id", ids[0]);
            row.put("data_type", "TEXT");
            row.put("sequence", 1);
            row.put("task_template_id", taskTemplateIds[0]);
            row.put("name", "UT_name_modify");
            Mockito.when(executor.executeQuery(Mockito.anyString(), Mockito.anyList()))
                    .thenReturn(Collections.singletonList(row));

            TaskTemplateProperty retrieve = repo.retrieve(taskTemplateIds[0], ids[0], context);

            Assertions.assertEquals(1, retrieve.sequence());
            Assertions.assertEquals(PropertyDataType.TEXT, retrieve.dataType());
            Assertions.assertEquals("UT_name_modify", retrieve.name());
        }

        @Test
        @DisplayName("检索失败：没有找到对应的返回值")
        public void test02() {

            Mockito.when(executor.executeQuery(Mockito.anyString(), Mockito.anyList()))
                    .thenReturn(Collections.emptyList());

            NotFoundException notFoundException = Assertions.assertThrows(NotFoundException.class,
                    () -> repo.retrieve(taskTemplateIds[0], ids[0], context));

            Assertions.assertEquals("The task template property does not exist.", notFoundException.getMessage());
        }

        @Test
        @DisplayName("检索失败：找到多个返回值")
        public void test03() {
            Map<String, Object> row1 = new HashMap<>();
            row1.put("id", ids[0]);
            row1.put("data_type", "TEXT");
            row1.put("sequence", 1);
            row1.put("task_template_id", taskTemplateIds[0]);
            row1.put("name", "UT_name_modify");
            Map<String, Object> row2 = new HashMap<>();
            row2.put("id", ids[1]);
            row2.put("data_type", "TEXT");
            row2.put("sequence", 2);
            row2.put("task_template_id", taskTemplateIds[0]);
            row2.put("name", "UT_name_modify");
            Mockito.when(executor.executeQuery(Mockito.anyString(), Mockito.anyList()))
                    .thenReturn(Arrays.asList(row1, row2));

            ServerInternalException e = Assertions.assertThrows(ServerInternalException.class,
                    () -> repo.retrieve(taskTemplateIds[0], ids[0], context));

            Assertions.assertEquals("Find two or more row in database by id " + ids[0] + ".", e.getMessage());
        }
    }

    @Nested
    @DisplayName("查询方法测试")
    class ListTest {
        @Test
        @DisplayName("测试list方法：templateId")
        void test01() {
            Map<String, Object> row1 = new HashMap<>();
            row1.put("id", ids[0]);
            row1.put("data_type", "TEXT");
            row1.put("sequence", 1);
            row1.put("task_template_id", taskTemplateIds[0]);
            row1.put("name", "UT_name_modify");
            Mockito.when(executor.executeQuery(Mockito.anyString(), Mockito.anyList()))
                    .thenReturn(Collections.singletonList(row1));
            List<TaskTemplateProperty> list = repo.list(taskTemplateIds[0], context);
            TaskTemplateProperty property = list.get(0);
            Assertions.assertEquals(ids[0], property.id());
        }

        @Test
        @DisplayName("测试list方法：templateIds")
        void test02() {
            Map<String, Object> row1 = new HashMap<>();
            row1.put("id", ids[0]);
            row1.put("data_type", "TEXT");
            row1.put("sequence", 1);
            row1.put("task_template_id", taskTemplateIds[0]);
            row1.put("name", "UT_name_modify");
            Mockito.when(executor.executeQuery(Mockito.anyString(), Mockito.anyList()))
                    .thenReturn(Collections.singletonList(row1));
            Map<String, List<TaskTemplateProperty>> list = repo.list(Arrays.asList(taskTemplateIds), context);

            Assertions.assertTrue(list.containsKey(taskTemplateIds[0]));
        }
    }

    @Nested
    @DisplayName("删除方法测试")
    class DeleteTest {
        @Test
        @DisplayName("删除一个Property成功")
        void test01() {
            Mockito.when(executor.executeScalar(Mockito.anyString(), Mockito.anyList())).thenReturn(0);
            Mockito.when(executor.executeUpdate(Mockito.anyString(), Mockito.anyList())).thenReturn(1);
            Assertions.assertDoesNotThrow(() -> repo.delete(taskTemplateIds[0], ids[0], context));
        }

        @Test
        @DisplayName("删除一个Property失败：任务模板属性已被使用")
        void test02() {
            Mockito.when(executor.executeScalar(Mockito.anyString(), Mockito.anyList())).thenReturn(1);
            ConflictException exception = Assertions.assertThrows(ConflictException.class,
                    () -> repo.delete(taskTemplateIds[0], ids[0], context));

            Assertions.assertEquals("The property cannot be modified or deleted when the property is used.",
                    exception.getMessage());
        }

        @Test
        @DisplayName("删除一个Property失败：数据库执行失败")
        void test03() {
            Mockito.when(executor.executeScalar(Mockito.anyString(), Mockito.anyList())).thenReturn(0);
            Mockito.when(executor.executeUpdate(Mockito.anyString(), Mockito.anyList())).thenReturn(0);

            ServerInternalException exception = Assertions.assertThrows(ServerInternalException.class,
                    () -> repo.delete(taskTemplateIds[0], ids[0], context));

            Assertions.assertEquals("Failed delete the data in task_template_property, id=" + ids[0],
                    exception.getMessage());
        }

        @Test
        @DisplayName("通过taskTemplateId删除properties成功")
        void test04() {
            Mockito.when(executor.executeUpdate(Mockito.anyString(), Mockito.anyList())).thenReturn(10);
            Assertions.assertDoesNotThrow(() -> repo.deleteByTaskTemplateId(taskTemplateIds[0], context));
        }
    }
}
