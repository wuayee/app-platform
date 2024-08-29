/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.task.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.huawei.jade.app.engine.task.po.EvalRecordPo;

import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.MybatisTest;
import modelengine.fitframework.test.annotation.Sql;
import modelengine.fitframework.test.domain.db.DatabaseModel;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

/**
 * 表示 {@link EvalRecordMapper} 的测试集。
 *
 * @author 何嘉斌
 * @since 2024-08-13
 */
@MybatisTest(classes = {EvalRecordMapper.class}, model = DatabaseModel.POSTGRESQL)
@Sql(scripts = "sql/test_create_table.sql")
@DisplayName("测试 EvalTaskCaseResultMapper")
public class EvalRecordMapperTest {
    @Fit
    private EvalRecordMapper mapper;

    @Test
    @DisplayName("插入单个评估记录后，回填主键成功")
    void shouldOkWhenInsertSingleEvalRecord() {
        EvalRecordPo resultPo = new EvalRecordPo();
        resultPo.setInput("{\"input\":\"1+1\", \"output\":\"2\", \"expected\":\"2\"}");
        resultPo.setNodeId("node1");
        resultPo.setScore(1.0);
        resultPo.setTaskCaseId(1L);

        this.mapper.create(Collections.singletonList(resultPo));
        assertThat(resultPo.getId()).isNotEqualTo(null);
    }

    @Test
    @DisplayName("插入评估记录后，回填主键成功")
    void shouldOkWhenInsertEvalRecord() {
        EvalRecordPo resultPo1 = new EvalRecordPo();
        resultPo1.setInput("{\"input\":\"1+1\", \"output\":\"2\", \"expected\":\"2\"}");
        resultPo1.setNodeId("node1");
        resultPo1.setScore(1.0);
        resultPo1.setTaskCaseId(1L);

        EvalRecordPo resultPo2 = new EvalRecordPo();
        resultPo2.setInput("{\"input\":\"1+1\", \"output\":\"2\", \"expected\":\"2\"}");
        resultPo2.setNodeId("node2");
        resultPo2.setScore(1.0);
        resultPo2.setTaskCaseId(1L);

        this.mapper.create(Arrays.asList(resultPo1, resultPo2));
        assertThat(resultPo1.getId()).isNotEqualTo(null);
        assertThat(resultPo2.getId()).isNotEqualTo(null);
    }
}