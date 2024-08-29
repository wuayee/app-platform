/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.task.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.huawei.jade.app.engine.task.po.EvalCasePo;

import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.MybatisTest;
import modelengine.fitframework.test.annotation.Sql;
import modelengine.fitframework.test.domain.db.DatabaseModel;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 表示 {@link EvalCaseMapper} 的测试集。
 *
 * @author 何嘉斌
 * @since 2024-08-13
 */
@MybatisTest(classes = {EvalCaseMapper.class}, model = DatabaseModel.POSTGRESQL)
@Sql(scripts = "sql/test_create_table.sql")
@DisplayName("测试 EvalTaskCaseMapper")
public class EvalCaseMapperTest {
    @Fit
    private EvalCaseMapper mapper;

    @Test
    @DisplayName("插入评估任务用例后，回填主键成功")
    void shouldOkWhenCreateEvalTaskCase() {
        EvalCasePo evalCasePo = new EvalCasePo();
        evalCasePo.setLatency(100);
        evalCasePo.setOutcome(true);
        evalCasePo.setInstanceId(1L);

        this.mapper.create(evalCasePo);
        assertThat(evalCasePo.getId()).isNotEqualTo(null);
    }
}