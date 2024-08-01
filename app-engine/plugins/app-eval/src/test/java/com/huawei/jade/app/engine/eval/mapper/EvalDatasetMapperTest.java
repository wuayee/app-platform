/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.huawei.fitframework.annotation.Fit;
import com.huawei.fitframework.test.annotation.MybatisTest;
import com.huawei.fitframework.test.annotation.Sql;
import com.huawei.fitframework.test.domain.db.DatabaseModel;
import com.huawei.jade.app.engine.eval.po.EvalDatasetPo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
}
