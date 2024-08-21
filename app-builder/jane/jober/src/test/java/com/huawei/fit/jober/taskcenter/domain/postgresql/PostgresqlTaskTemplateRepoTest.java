/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.domain.postgresql;

import com.huawei.fit.jane.task.domain.PropertyDataType;
import com.huawei.fit.jane.task.util.OperationContext;
import com.huawei.fit.jane.task.util.UndefinableValue;
import com.huawei.fit.jober.taskcenter.domain.TaskTemplate;
import com.huawei.fit.jober.taskcenter.domain.TaskTemplateProperty;
import com.huawei.fit.jober.taskcenter.filter.TaskTemplateFilter;
import com.huawei.fit.jober.taskcenter.util.DynamicSqlExecutor;
import modelengine.fitframework.model.RangedResultSet;

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
import java.util.List;
import java.util.Map;

/**
 * 为 {@link PostgresqlTaskTemplateRepo} 提供测试
 *
 * @author 姚江
 * @since 2023-12-13
 */
@ExtendWith(MockitoExtension.class)
public class PostgresqlTaskTemplateRepoTest {
    PostgresqlTaskTemplateRepo repo;

    @Mock
    DynamicSqlExecutor executor;

    @Mock
    PostgresqlTaskTemplatePropertyRepo propertyRepo;

    OperationContext context = OperationContext.custom().operator("UT").tenantId("public").build();

    @BeforeEach
    void beforeEach() {
        repo = new PostgresqlTaskTemplateRepo(propertyRepo, executor);
    }

    @Nested
    @DisplayName("创建方法测试")
    class CreateTest {
        @Test
        @DisplayName("成功创建任务模板")
        void test01() {

            Mockito.doReturn(0).when(executor).executeScalar(Mockito.startsWith("SELECT 1 FROM"), Mockito.anyList());
            Mockito.doReturn(1).when(executor).executeUpdate(Mockito.anyString(), Mockito.anyList());
            Mockito.doReturn(buildProperties())
                    .when(propertyRepo)
                    .create(Mockito.anyString(), Mockito.anyList(), Mockito.eq(context));
            Mockito.doReturn("4c91c6495fc44b61a127bf8c47b53c06")
                    .when(executor)
                    .executeScalar(Mockito.eq("Select id from tenant where name = 'public'"));

            TaskTemplate.Declaration declaration = TaskTemplate.Declaration.custom()
                    .name("name")
                    .description("description")
                    .properties(buildPropertyDeclarations())
                    .build();

            TaskTemplate taskTemplate = repo.create(declaration, context);

            Assertions.assertEquals("name", taskTemplate.name());
            Assertions.assertEquals("description", taskTemplate.description());
            Assertions.assertEquals(1, taskTemplate.properties().size());
        }

        List<TaskTemplateProperty.Declaration> buildPropertyDeclarations() {
            TaskTemplateProperty.Declaration declaration = TaskTemplateProperty.Declaration.custom()
                    .dataType("TEXT")
                    .name("title")
                    .build();

            return Collections.singletonList(declaration);
        }

        List<TaskTemplateProperty> buildProperties() {
            TaskTemplateProperty templateProperty = TaskTemplateProperty.custom()
                    .taskTemplateId("mm")
                    .sequence(1)
                    .id("mmm")
                    .dataType(PropertyDataType.TEXT)
                    .name("title")
                    .build();
            return Collections.singletonList(templateProperty);
        }
    }

    @Nested
    @DisplayName("修改方法测试")
    class PatchTest {
        @Test
        @DisplayName("修改成功")
        void test01() {
            Map<String, Object> row = new HashMap<>();
            row.put("id", "f0676858e3cb469c8202ca55fdc5661a");
            row.put("name", "name");
            row.put("description", "description");

            Mockito.doReturn(buildModifyProperties())
                    .when(propertyRepo)
                    .list(Mockito.eq("f0676858e3cb469c8202ca55fdc5661a"), Mockito.any());
            Mockito.doReturn(Collections.singletonList(row))
                    .when(executor)
                    .executeQuery(Mockito.anyString(), Mockito.anyList());
            Mockito.doReturn(0).when(executor).executeScalar(Mockito.startsWith("SELECT 1 FROM"), Mockito.anyList());
            Mockito.doReturn(1).when(executor).executeUpdate(Mockito.anyString(), Mockito.anyList());

            TaskTemplate.Declaration declaration = TaskTemplate.Declaration.custom()
                    .description("description_modify")
                    .name("name_m")
                    .properties(buildModifyPropertyDeclarations())
                    .build();

            Assertions.assertDoesNotThrow(() -> repo.patch("f0676858e3cb469c8202ca55fdc5661a", declaration, context));
        }

        List<TaskTemplateProperty.Declaration> buildModifyPropertyDeclarations() {
            TaskTemplateProperty.Declaration declaration1 = TaskTemplateProperty.Declaration.custom()
                    .id("d8a1ed6da1de44bbbe243adb30942f27")
                    .name("title")
                    .dataType("TEXT")
                    .build();
            TaskTemplateProperty.Declaration declaration2 = TaskTemplateProperty.Declaration.custom()
                    .name("owner")
                    .dataType("TEXT")
                    .build();

            return Arrays.asList(declaration2, declaration1);
        }

        List<TaskTemplateProperty> buildModifyProperties() {
            TaskTemplateProperty declaration1 = TaskTemplateProperty.custom()
                    .id("d8a1ed6da1de44bbbe243adb30942f27")
                    .name("title")
                    .dataType(PropertyDataType.TEXT)
                    .build();
            TaskTemplateProperty declaration2 = TaskTemplateProperty.custom()
                    .id("c25d293fa9544c6daafd73905d896bad")
                    .name("owner1")
                    .dataType(PropertyDataType.TEXT)
                    .build();

            return Arrays.asList(declaration2, declaration1);
        }
    }

    @Nested
    @DisplayName("删除测试")
    class DeleteTest {
        @Test
        @DisplayName("成功删除")
        void test01() {
            Mockito.doReturn(0).when(executor).executeScalar(Mockito.startsWith("SELECT 1 FROM"), Mockito.anyList());
            Mockito.doNothing().when(propertyRepo).deleteByTaskTemplateId(Mockito.anyString(), Mockito.any());
            Mockito.doReturn(1).when(executor).executeUpdate(Mockito.anyString(), Mockito.anyList());

            Assertions.assertDoesNotThrow(() -> repo.delete("d8a1ed6da1de44bbbe243adb30942f27", context));
        }
    }

    @Nested
    @DisplayName("查询测试")
    class ListTest {
        @Test
        @DisplayName("成功查询")
        void test01() {

            Map<String, Object> row = new HashMap<>();
            row.put("id", "f0676858e3cb469c8202ca55fdc5661a");
            row.put("name", "name");
            row.put("description", "description");

            Mockito.doReturn(1L).when(executor).executeScalar(Mockito.startsWith("SELECT COUNT(1)"), Mockito.anyList());
            Mockito.doReturn(Collections.singletonList(row))
                    .when(executor)
                    .executeQuery(Mockito.anyString(), Mockito.anyList());
            Mockito.doReturn(buildModifyProperties()).when(propertyRepo).list(Mockito.anyList(), Mockito.any());
            Mockito.doReturn("4c91c6495fc44b61a127bf8c47b53c06")
                    .when(executor)
                    .executeScalar(Mockito.eq("Select id from tenant where name = 'public'"));

            TaskTemplateFilter filter = new TaskTemplateFilter();
            filter.setIds(UndefinableValue.defined(Collections.singletonList("f0676858e3cb469c8202ca55fdc5661a")));
            filter.setNames(UndefinableValue.defined(Collections.singletonList("name")));
            RangedResultSet<TaskTemplate> list = repo.list(filter, 0, 10, context);

            Assertions.assertEquals(1, list.getRange().getTotal());
            Assertions.assertEquals(1, list.getResults().size());
        }

        Map<String, List<TaskTemplateProperty>> buildModifyProperties() {
            TaskTemplateProperty declaration1 = TaskTemplateProperty.custom()
                    .id("d8a1ed6da1de44bbbe243adb30942f27")
                    .name("title_o")
                    .dataType(PropertyDataType.BOOLEAN)
                    .build();
            TaskTemplateProperty declaration2 = TaskTemplateProperty.custom()
                    .id("c25d293fa9544c6daafd73905d896bad")
                    .name("owner1")
                    .dataType(PropertyDataType.TEXT)
                    .build();

            Map<String, List<TaskTemplateProperty>> result = new HashMap<>();
            result.put("f0676858e3cb469c8202ca55fdc5661a", Arrays.asList(declaration2, declaration1));

            return result;
        }
    }
}
