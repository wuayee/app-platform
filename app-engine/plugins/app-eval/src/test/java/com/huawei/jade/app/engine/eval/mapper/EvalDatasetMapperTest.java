/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.huawei.fitframework.annotation.Fit;
import com.huawei.fitframework.test.annotation.MybatisTest;
import com.huawei.fitframework.test.annotation.Sql;
import com.huawei.fitframework.test.domain.db.DatabaseModel;
import com.huawei.fitframework.transaction.DataAccessException;
import com.huawei.fitframework.util.StringUtils;
import com.huawei.jade.app.engine.eval.dto.EvalDatasetQueryParam;
import com.huawei.jade.app.engine.eval.entity.EvalDatasetEntity;
import com.huawei.jade.app.engine.eval.po.EvalDatasetPo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

/**
 * 表示 {@link EvalDatasetMapper} 的测试集。
 *
 * @author 何嘉斌
 * @since 2024-07-31
 */
@MybatisTest(classes = {EvalDatasetMapper.class}, model = DatabaseModel.POSTGRESQL)
@Sql(scripts = "sql/test_create_table.sql")
@DisplayName("测试 EvalDatasetMapper")
public class EvalDatasetMapperTest {
    @Fit
    private EvalDatasetMapper evalDatasetMapper;

    @Test
    @DisplayName("插入数据集后，回填主键成功")
    void shouldOkWhenInsert() {
        EvalDatasetPo evalDatasetPo = new EvalDatasetPo();
        evalDatasetPo.setName("ds1");
        evalDatasetPo.setDescription("Test dataset");
        evalDatasetPo.setSchema("{}");
        evalDatasetPo.setAppId("");

        this.evalDatasetMapper.create(evalDatasetPo);
        assertThat(evalDatasetPo.getId()).isNotEqualTo(null);
    }

    @Test
    @Sql(scripts = "sql/test_insert_data.sql")
    @DisplayName("删除单个数据集时，不存在未删除数据，返回正确的删除行数")
    void shouldOkWhenDeleteSingleDataset() {
        assertThat(this.evalDatasetMapper.deleteById(2L)).isEqualTo(1);
    }

    @Test
    @Sql(scripts = "sql/test_insert_data.sql")
    @DisplayName("删除单个数据集时，数据集不存在，删除行数为 0")
    void shouldOKWhenDeleteSingleNonexistentDataset() {
        assertThat(this.evalDatasetMapper.deleteById(4L)).isEqualTo(0);
    }

    @Test
    @Sql(scripts = "sql/test_insert_data.sql")
    @DisplayName("删除单个数据集时，存在未删除数据，删除失败")
    void shouldFailWhenDeleteSingleDatasetWithRemainingData() {
        assertThatThrownBy(() -> this.evalDatasetMapper.deleteById(1L)).isInstanceOf(DataAccessException.class);
    }

    @Test
    @Sql(scripts = "sql/test_insert_data.sql")
    @DisplayName("删除数据集时，不存在未删除数据，返回正确的删除行数")
    void shouldOkWhenDeleteDataset() {
        assertThat(this.evalDatasetMapper.delete(Arrays.asList(2L, 3L))).isEqualTo(2);
    }

    @Test
    @Sql(scripts = "sql/test_insert_data.sql")
    @DisplayName("删除数据集时，存在已删除数据集，返回正确的删除行数")
    void shouldOkWhenDeleteDatasetWithNonexistentDataset() {
        assertThat(this.evalDatasetMapper.delete(Arrays.asList(2L, 4L))).isEqualTo(1);
    }

    @Test
    @Sql(scripts = "sql/test_insert_data.sql")
    @DisplayName("删除数据集时，存在未删除数据，删除失败")
    void shouldFailWhenDeleteDatasetsWithRemainingData() {
        assertThatThrownBy(() -> this.evalDatasetMapper.delete(Arrays.asList(1L,
                2L))).isInstanceOf(DataAccessException.class);
    }

    @Test
    @Sql(scripts = "sql/test_create_dataset.sql")
    @DisplayName("分页查询数据集元数据成功")
    void shouldOkWhenListEvalDataset() {
        EvalDatasetQueryParam queryParam = new EvalDatasetQueryParam();
        queryParam.setAppId("123456");
        queryParam.setPageIndex(1);
        queryParam.setPageSize(2);
        List<EvalDatasetEntity> datasetEntities = this.evalDatasetMapper.listEvalDataset(queryParam);
        assertThat(datasetEntities.size()).isEqualTo(2);

        for (int i = 0; i < datasetEntities.size(); i++) {
            EvalDatasetEntity entity = datasetEntities.get(i);
            assertThat(entity).extracting(EvalDatasetEntity::getSchema,
                            EvalDatasetEntity::getId,
                            EvalDatasetEntity::getName,
                            EvalDatasetEntity::getDescription,
                            EvalDatasetEntity::getCreatedBy,
                            EvalDatasetEntity::getUpdatedBy)
                    .containsExactly(null,
                            Long.valueOf(i + 1),
                            StringUtils.format("name{0}", i + 1),
                            StringUtils.format("desc{0}", i + 1),
                            StringUtils.format("Sky{0}", i + 1),
                            StringUtils.format("Fang{0}", i + 1));
        }
    }

    @Test
    @Sql(scripts = "sql/test_create_dataset.sql")
    @DisplayName("根据数据集唯一标识查询数据集元数据成功")
    void shouldOkWhenGetEvalDatasetById() {
        EvalDatasetEntity entity = this.evalDatasetMapper.getEvalDatasetById(3L);
        assertThat(entity).extracting(EvalDatasetEntity::getSchema,
                EvalDatasetEntity::getName,
                EvalDatasetEntity::getDescription,
                EvalDatasetEntity::getCreatedBy,
                EvalDatasetEntity::getUpdatedBy).containsExactly("Fake schema 3", "name3", "desc3", "Sky3", "Fang3");
    }

    @Test
    @Sql(scripts = "sql/test_create_dataset.sql")
    @DisplayName("修改数据集名称成功")
    void shouldOKWhenUpdateEvalDatasetName() {
        String name = "datasetName1";
        Long id = 1L;
        EvalDatasetPo evalDatasetPo = new EvalDatasetPo();
        evalDatasetPo.setName(name);
        evalDatasetPo.setId(id);

        this.evalDatasetMapper.updateEvaldataset(evalDatasetPo);
        EvalDatasetEntity entity = this.evalDatasetMapper.getEvalDatasetById(id);
        assertThat(entity).hasFieldOrPropertyWithValue("name", name);
        assertThat(entity).hasFieldOrPropertyWithValue("description", "desc1");
    }

    @Test
    @Sql(scripts = "sql/test_create_dataset.sql")
    @DisplayName("修改数据集描述成功")
    void shouldOKWhenUpdateEvalDatasetDesc() {
        String desc = "datasetDesc1";
        Long id = 1L;
        EvalDatasetPo evalDatasetPo = new EvalDatasetPo();
        evalDatasetPo.setDescription(desc);
        evalDatasetPo.setId(id);

        this.evalDatasetMapper.updateEvaldataset(evalDatasetPo);
        EvalDatasetEntity entity = this.evalDatasetMapper.getEvalDatasetById(id);
        assertThat(entity).hasFieldOrPropertyWithValue("name", "name1");
        assertThat(entity).hasFieldOrPropertyWithValue("description", desc);
    }


    @Test
    @Sql(scripts = "sql/test_create_dataset.sql")
    @DisplayName("修改数据集描述和名字成功")
    void shouldOKWhenUpdateEvalDatasetDescAndName() {
        String name = "datasetName1";
        String desc = "datasetDesc1";
        Long id = 1L;
        EvalDatasetPo evalDatasetPo = new EvalDatasetPo();
        evalDatasetPo.setDescription(desc);
        evalDatasetPo.setName(name);
        evalDatasetPo.setId(id);

        this.evalDatasetMapper.updateEvaldataset(evalDatasetPo);
        EvalDatasetEntity entity = this.evalDatasetMapper.getEvalDatasetById(id);
        assertThat(entity).hasFieldOrPropertyWithValue("name", name);
        assertThat(entity).hasFieldOrPropertyWithValue("description", desc);
    }
}
