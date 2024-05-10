/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */


package com.huawei.fit.jober.taskcenter.dao;

import com.huawei.fit.jober.DatabaseBaseTest;
import com.huawei.fit.jober.taskcenter.dao.po.TaskObject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

/**
 * {@link TaskMapper}对应测试类
 *
 * @author 梁致强 l50033199
 * @since 2023-08-09
 */
public class TaskMapperTest extends DatabaseBaseTest {
    private TaskMapper taskMapper;

    @BeforeEach
    void setUp() {
        taskMapper = sqlSessionManager.openSession(true).getMapper(TaskMapper.class);
    }

    @Override
    protected void cleanTable() {
        executeSqlInFile("handler/task/cleanData.sql");
    }

    @Nested
    @DisplayName("测试创建Task功能")
    class TestCreate {
        @Test
        @DisplayName("参数正确，创建成功")
        void create() {
            // when
            LocalDateTime now = LocalDateTime.now();
            TaskObject taskObject = new TaskObject();
            taskObject.setId("a3a7df7e45f44451970a6a8e138382c2");
            taskObject.setName("name");
            taskObject.setCategory("TASK");
            taskObject.setTenantId("a3a7df7e45f44451970a6a8e138382c2");
            taskObject.setAttributes("{}");
            taskObject.setCreatedBy("a3a7df7e45f44451970a6a8e138382c2");
            taskObject.setCreatedAt(now);
            taskObject.setUpdatedBy("a3a7df7e45f44451970a6a8e138382c2");
            taskObject.setUpdatedAt(now);
            taskObject.setTemplateId("a3a7df7e45f44451970a6a8e138382c2");
            Integer affectRows = taskMapper.insert(taskObject);
            // then
            Assertions.assertEquals(1, affectRows);
            TaskObject actual = taskMapper.selectById("a3a7df7e45f44451970a6a8e138382c2");
            Assertions.assertNotNull(actual);
            Assertions.assertEquals("a3a7df7e45f44451970a6a8e138382c2", actual.getId());
            Assertions.assertEquals(4, actual.getName().length());
            Assertions.assertEquals("name", actual.getName().trim());
            Assertions.assertEquals("a3a7df7e45f44451970a6a8e138382c2", actual.getCreatedBy());
            Assertions.assertEquals("a3a7df7e45f44451970a6a8e138382c2", actual.getTenantId());
            Assertions.assertEquals("a3a7df7e45f44451970a6a8e138382c2", actual.getUpdatedBy());
            Assertions.assertEquals("a3a7df7e45f44451970a6a8e138382c2", actual.getTemplateId());
            Assertions.assertEquals(now, actual.getUpdatedAt());
            Assertions.assertEquals(now, actual.getUpdatedAt());
        }
    }

    @Nested
    @DisplayName("测试更新Task功能")
    class TestUpdate {
        @Test
        @DisplayName("删除没有Task，返回受影响0条")
        void givenNonExistentTaskIdThenAffectRowsIsZero() {
            // when
            Integer actual = taskMapper.delete("uuid1");
            // then
            Assertions.assertEquals(0, actual);
        }
    }

    @Nested
    @DisplayName("测试删除Task功能")
    class TestDelete {
        @Test
        @DisplayName("删除已有Task，删除成功")
        void givenExistentTaskIdThenDeleteSuccessfully() {
            // given
            executeSqlInFile("handler/task/saveData.sql");
            TaskObject actual = taskMapper.selectById("a3a7df7e45f44451970a6a8e138382c2");
            Assertions.assertNotNull(actual);
            // when
            Integer affectRows = taskMapper.delete("a3a7df7e45f44451970a6a8e138382c2");
            // then
            Assertions.assertEquals(1, affectRows);
            actual = taskMapper.selectById("a3a7df7e45f44451970a6a8e138382c2");
            Assertions.assertNull(actual);
        }

        @Test
        @DisplayName("删除没有Task，返回受影响0条")
        void givenNonExistentTaskIdThenAffectRowsIsZero() {
            // when
            Integer actual = taskMapper.delete("uuid1");
            // then
            Assertions.assertEquals(0, actual);
        }
    }

    @Nested
    @DisplayName("测试检索Task功能")
    class TestSelectById {
        @Test
        @DisplayName("查询已有Task，查询成功")
        void givenExistentTaskIdThenRetrieveSuccessfully() {
            // given
            executeSqlInFile("handler/task/saveData.sql");
            LocalDateTime localDateTime = LocalDateTime.parse("2023-08-11T00:00:00");
            // when
            TaskObject actual = taskMapper.selectById("a3a7df7e45f44451970a6a8e138382c2");
            // then
            Assertions.assertNotNull(actual);
            Assertions.assertEquals("a3a7df7e45f44451970a6a8e138382c2", actual.getId());
            Assertions.assertEquals("a3a7df7e45f44451970a6a8e138382c2", actual.getName());
            Assertions.assertEquals("a3a7df7e45f44451970a6a8e138382c2", actual.getCreatedBy());
            Assertions.assertEquals("a3a7df7e45f44451970a6a8e138382c2", actual.getTenantId());
            Assertions.assertEquals("a3a7df7e45f44451970a6a8e138382c2", actual.getUpdatedBy());
            Assertions.assertEquals(localDateTime, actual.getUpdatedAt());
            Assertions.assertEquals(localDateTime, actual.getUpdatedAt());
        }

        @Test
        @DisplayName("查询没有Task，返回Null")
        void givenNonExistentTaskIdThenRetrieveNullResult() {
            // when
            TaskObject actual = taskMapper.selectById("uuid1");
            // then
            Assertions.assertNull(actual);
        }
    }
}
