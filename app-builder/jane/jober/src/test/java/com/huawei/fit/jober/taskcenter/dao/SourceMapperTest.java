/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.dao;

import modelengine.fit.jober.DatabaseBaseTest;
import modelengine.fit.jober.taskcenter.dao.po.SourceObject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * {@link SourceMapper}对应测试类
 *
 * @author 陈镕希
 * @since 2023-08-09
 */
class SourceMapperTest extends DatabaseBaseTest {
    private SourceMapper sourceMapper;

    @BeforeEach
    void setUp() {
        sourceMapper = sqlSessionManager.openSession(true).getMapper(SourceMapper.class);
    }

    @Override
    protected void cleanTable() {
        executeSqlInFile("handler/source/cleanData.sql");
    }

    @Nested
    @DisplayName("测试创建Source功能")
    class TestCreate {
        @Test
        @DisplayName("参数正确，创建成功")
        void create() {
            // when
            Integer affectRows = sourceMapper.insert(SourceObject.builder()
                    .id("a3a7df7e45f44451970a6a8e138382c2")
                    .taskId("a3a7df7e45f44451970a6a8e138382a2")
                    .name("name")
                    .app("libing")
                    .type("a3a7df7e45f44451970a6a8e138382b2")
                    .build());
            // then
            Assertions.assertEquals(1, affectRows);
            SourceObject actual = sourceMapper.select("a3a7df7e45f44451970a6a8e138382c2");
            Assertions.assertNotNull(actual);
            Assertions.assertEquals("a3a7df7e45f44451970a6a8e138382c2", actual.getId());
            Assertions.assertEquals("a3a7df7e45f44451970a6a8e138382a2", actual.getTaskId());
            Assertions.assertEquals("name", actual.getName());
            Assertions.assertEquals("libing", actual.getApp());
            Assertions.assertEquals("a3a7df7e45f44451970a6a8e138382b2", actual.getType());
        }
    }

    @Nested
    @DisplayName("测试删除Source功能")
    class TestDelete {
        @Test
        @DisplayName("删除已有Source，删除成功")
        void givenExistentSourceIdThenDeleteSuccessfully() {
            // given
            executeSqlInFile("handler/source/saveData.sql");
            SourceObject actual = sourceMapper.select("a3a7df7e45f44451970a6a8e138382c2");
            Assertions.assertNotNull(actual);
            // when
            Integer affectRows = sourceMapper.delete("a3a7df7e45f44451970a6a8e138382c2");
            // then
            Assertions.assertEquals(1, affectRows);
            actual = sourceMapper.select("a3a7df7e45f44451970a6a8e138382c2");
            Assertions.assertNull(actual);
        }

        @Test
        @DisplayName("删除没有Source，返回受影响0条")
        void givenNonExistentSourceIdThenAffectRowsIsZero() {
            // when
            Integer actual = sourceMapper.delete("uuid1");
            // then
            Assertions.assertEquals(0, actual);
        }
    }

    @Nested
    @DisplayName("测试检索Source功能")
    class TestRetrieve {
        @Test
        @DisplayName("查询已有Source，查询成功")
        void givenExistentSourceIdThenRetrieveSuccessfully() {
            // given
            executeSqlInFile("handler/source/saveData.sql");
            // when
            SourceObject actual = sourceMapper.select("a3a7df7e45f44451970a6a8e138382c2");
            // then
            Assertions.assertNotNull(actual);
            Assertions.assertEquals("a3a7df7e45f44451970a6a8e138382c2", actual.getId());
            Assertions.assertEquals("a3a7df7e45f44451970a6a8e138382a2", actual.getTaskId());
            Assertions.assertEquals("libing", actual.getApp());
            Assertions.assertEquals("a3a7df7e45f44451970a6a8e138382b2", actual.getType());
        }

        @Test
        @DisplayName("查询没有Source，返回Null")
        void givenNonExistentSourceIdThenRetrieveNullResult() {
            // when
            SourceObject actual = sourceMapper.select("uuid1");
            // then
            Assertions.assertNull(actual);
        }
    }

    @Nested
    @DisplayName("测试列出指定任务定义的Source功能")
    class TestList {
        @Test
        @DisplayName("任务定义带Source，查询成功")
        void givenTaskIdWithSourceThenListSuccessfully() {
            // given
            executeSqlInFile("handler/source/saveData.sql");
            // when
            List<SourceObject> actual = sourceMapper.selectByTaskId("a3a7df7e45f44451970a6a8e138382a2");
            // then
            Assertions.assertNotNull(actual);
            Assertions.assertEquals(2, actual.size());
            Assertions.assertEquals("a3a7df7e45f44451970a6a8e138382c2", actual.get(0).getId());
            Assertions.assertEquals("a3a7df7e45f44451970a6a8e138382a2", actual.get(0).getTaskId());
            Assertions.assertEquals("libing", actual.get(0).getApp());
            Assertions.assertEquals("a3a7df7e45f44451970a6a8e138382b2", actual.get(0).getType());
            Assertions.assertEquals("a3a7df7e45f44451970a6a8e138382c4", actual.get(1).getId());
            Assertions.assertEquals("a3a7df7e45f44451970a6a8e138382a2", actual.get(1).getTaskId());
            Assertions.assertEquals("quality", actual.get(1).getApp());
            Assertions.assertEquals("a3a7df7e45f44451970a6a8e138382b4", actual.get(1).getType());
        }

        @Test
        @DisplayName("任务定义不带Source，查询成功")
        void givenTaskIdWithoutSourceThenListSuccessfully() {
            // when
            List<SourceObject> actual = sourceMapper.selectByTaskId("a3a7df7e45f44451970a6a8e138382a2");
            // then
            Assertions.assertNotNull(actual);
            Assertions.assertEquals(0, actual.size());
        }
    }
}