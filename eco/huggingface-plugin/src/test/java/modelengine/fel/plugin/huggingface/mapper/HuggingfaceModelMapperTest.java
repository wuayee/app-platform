/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fel.plugin.huggingface.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import modelengine.fel.plugin.huggingface.dto.HuggingfaceModelQueryParam;
import modelengine.fel.plugin.huggingface.entity.HuggingfaceModelEntity;
import modelengine.fel.plugin.huggingface.po.HuggingfaceModelPo;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.MybatisTest;
import modelengine.fitframework.test.annotation.Sql;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * 表示 {@link HuggingfaceModelMapper} 的测试用例。
 *
 * @author 何嘉斌
 * @author 邱晓霞
 * @since 2024-09-10
 */
@MybatisTest(classes = {HuggingfaceModelMapper.class})
@Sql(scripts = "sql/test_create_table.sql")
@DisplayName("测试 HuggingfaceModelMapper")
public class HuggingfaceModelMapperTest {
    @Fit
    private HuggingfaceModelMapper mapper;

    @Test
    @Sql(scripts = "sql/test_insert_task.sql")
    @DisplayName("插入 Huggingface 模型成功")
    void shouldOkWhenInsertModel() {
        HuggingfaceModelPo po = new HuggingfaceModelPo();
        po.setModelName("name");
        po.setModelSchema("desc");
        po.setTaskId(1L);
        this.mapper.insert(po);
        assertThat(po.getId()).isNotNull();
    }

    @Test
    @DisplayName("查询指定任务的默认模型实体成功")
    @Sql(scripts = "sql/test_insert_task_and_model.sql")
    void testDefaultModelEntity() {
        HuggingfaceModelQueryParam modelQueryParam = new HuggingfaceModelQueryParam();
        modelQueryParam.setTaskId(1L);
        modelQueryParam.setPageIndex(1);
        modelQueryParam.setPageSize(1);
        List<HuggingfaceModelEntity> response = this.mapper.listModelPartialInfo(modelQueryParam);
        assertThat(response.size()).isEqualTo(1);
        HuggingfaceModelEntity defaultModelEntity = response.get(0);
        assertThat(defaultModelEntity.getTaskId()).isEqualTo(1);
        assertThat(defaultModelEntity.getModelName()).isEqualTo("name1");
        assertThat(defaultModelEntity.getModelSchema()).isEqualTo("schema1");
    }

    @Test
    @DisplayName("查询指定任务的模型实体成功")
    @Sql(scripts = "sql/test_insert_task_and_model.sql")
    void testListModelsEntity() {
        HuggingfaceModelQueryParam modelQueryParam = new HuggingfaceModelQueryParam();
        modelQueryParam.setTaskId(1L);
        modelQueryParam.setPageIndex(1);
        modelQueryParam.setPageSize(2);
        List<HuggingfaceModelEntity> response = this.mapper.listModelPartialInfo(modelQueryParam);
        assertThat(response.size()).isEqualTo(2);
        HuggingfaceModelEntity entity = response.get(1);
        assertThat(entity.getTaskId()).isEqualTo(1);
        assertThat(entity.getModelName()).isEqualTo("name2");
        assertThat(entity.getModelSchema()).isEqualTo("schema2");
    }

    @Test
    @DisplayName("查询指定任务的模型数量成功")
    @Sql(scripts = "sql/test_insert_task_and_model.sql")
    void testListModelsCount() {
        HuggingfaceModelQueryParam modelQueryParam = new HuggingfaceModelQueryParam();
        modelQueryParam.setTaskId(1L);
        int response = this.mapper.countModel(modelQueryParam);
        assertThat(response).isEqualTo(2);
    }
}