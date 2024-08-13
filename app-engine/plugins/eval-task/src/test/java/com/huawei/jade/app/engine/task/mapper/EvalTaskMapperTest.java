/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.task.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.huawei.fitframework.annotation.Fit;
import com.huawei.fitframework.test.annotation.MybatisTest;
import com.huawei.fitframework.test.annotation.Sql;
import com.huawei.fitframework.test.domain.db.DatabaseModel;
import com.huawei.jade.app.engine.task.po.EvalTaskPo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 表示 {@link EvalTaskMapper} 的测试集。
 *
 * @author 何嘉斌
 * @since 2024-08-09
 */
@MybatisTest(classes = {EvalTaskMapper.class}, model = DatabaseModel.POSTGRESQL)
@Sql(scripts = "sql/test_create_table.sql")
@DisplayName("测试 EvalTaskMapper")
public class EvalTaskMapperTest {
    @Fit
    private EvalTaskMapper evalTaskMapper;

    @Test
    @DisplayName("插入数据集后，回填主键成功")
    void shouldOkWhenCreateEvalTask() {
        EvalTaskPo evalTaskPo = new EvalTaskPo();
        evalTaskPo.setName("task1");
        evalTaskPo.setDescription("eval task");
        evalTaskPo.setStatus("published");
        evalTaskPo.setAppId("123456");
        evalTaskPo.setWorkflowId("flow1");

        this.evalTaskMapper.create(evalTaskPo);
        assertThat(evalTaskPo.getId()).isNotEqualTo(null);
    }
}
