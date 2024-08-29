/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.task.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.huawei.jade.app.engine.task.po.EvalReportPo;

import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.MybatisTest;
import modelengine.fitframework.test.annotation.Sql;
import modelengine.fitframework.test.domain.db.DatabaseModel;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 表示 {@link EvalReportMapper} 的测试集。
 *
 * @author 何嘉斌
 * @since 2024-08-14
 */
@MybatisTest(classes = {EvalReportMapper.class}, model = DatabaseModel.POSTGRESQL)
@Sql(scripts = "sql/test_create_table.sql")
@DisplayName("测试 EvalReportMapper")
public class EvalReportMapperTest {
    @Fit
    private EvalReportMapper mapper;

    @Test
    @DisplayName("插入评估任务报告后，回填主键成功")
    void shouldOkWhenCreateEvalTask() {
        EvalReportPo evalReportPo = new EvalReportPo();
        evalReportPo.setNodeName("Accuracy");
        evalReportPo.setAlgorithmSchema("{}");
        evalReportPo.setPassScore(80.0);
        evalReportPo.setAverageScore(100.0);
        evalReportPo.setHistogram("{}");
        evalReportPo.setInstanceId(1L);

        this.mapper.create(evalReportPo);
        assertThat(evalReportPo.getId()).isNotEqualTo(null);
    }
}