/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fel.plugin.huggingface.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fel.plugin.huggingface.entity.HuggingfaceTaskEntity;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.MybatisTest;
import modelengine.fitframework.test.annotation.Sql;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * 表示 {@link HuggingfaceTaskMapper} 的测试用例。
 *
 * @author 何嘉斌
 * @author 邱晓霞
 * @since 2024-09-09
 */
@MybatisTest(classes = {HuggingfaceTaskMapper.class})
@Sql(scripts = "sql/test_create_table.sql")
@DisplayName("测试 HuggingfaceTaskMapper")
public class HuggingfaceTaskMapperTest {
    @Fit
    private HuggingfaceTaskMapper mapper;

    @Test
    @Sql(scripts = "sql/test_insert_task.sql")
    @DisplayName("模型数量增加成功")
    void shouldOkWhenIncreaseTotalModelNum() {
        HuggingfaceTaskEntity entity = this.mapper.listAvailableTasks().get(0);
        assertThat(entity.getTotalModelNum()).isEqualTo(1);
        this.mapper.increaseModelCount(1L);
        entity = this.mapper.listAvailableTasks().get(0);
        assertThat(entity.getTotalModelNum()).isEqualTo(2);
    }

    @Test
    @DisplayName("查询可用任务数据成功")
    @Sql(scripts = "sql/test_insert_task.sql")
    void TestListAvailableTasks() {
        List<HuggingfaceTaskEntity> response = this.mapper.listAvailableTasks();
        assertThat(response.size()).isEqualTo(1);
        for (HuggingfaceTaskEntity entity : response) {
            assertThat(entity.getTaskId()).isNotEqualTo(null);
            assertThat(entity.getTaskNameCode()).isEqualTo("name");
            assertThat(entity.getTaskDescriptionCode()).isEqualTo("desc");
            assertThat(entity.getTotalModelNum()).isEqualTo(1);
        }
    }
}