/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.huawei.fitframework.annotation.Fit;
import com.huawei.fitframework.test.annotation.MybatisTest;
import com.huawei.fitframework.test.annotation.Sql;
import com.huawei.fitframework.test.domain.db.DatabaseModel;
import com.huawei.fitframework.util.StringUtils;
import com.huawei.jade.app.engine.eval.dto.EvalDatasetQueryParam;
import com.huawei.jade.app.engine.eval.entity.EvalDatasetEntity;
import com.huawei.jade.app.engine.eval.po.EvalDatasetPo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
    @DisplayName("插入数据后，回填主键成功")
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
    @DisplayName("根据数据集 ID 查询数据集元数据成功")
    void shouldOkWhenGetEvalDatasetById() {
        EvalDatasetEntity entity = this.evalDatasetMapper.getEvalDatasetById(3L);
        assertThat(entity).extracting(EvalDatasetEntity::getSchema,
                EvalDatasetEntity::getName,
                EvalDatasetEntity::getDescription,
                EvalDatasetEntity::getCreatedBy,
                EvalDatasetEntity::getUpdatedBy).containsExactly("Fake schema 3", "name3", "desc3", "Sky3", "Fang3");
    }
}
