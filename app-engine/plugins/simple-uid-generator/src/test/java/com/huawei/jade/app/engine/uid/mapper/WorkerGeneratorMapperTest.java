/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.uid.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.huawei.fitframework.annotation.Fit;
import com.huawei.fitframework.test.annotation.MybatisTest;
import com.huawei.fitframework.test.annotation.Sql;
import com.huawei.fitframework.test.domain.db.DatabaseModel;
import com.huawei.jade.app.engine.uid.po.WorkerPo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 表示 {@link WorkerGeneratorMapper} 的测试用例。
 *
 * @author 何嘉斌
 * @since 2024-07-29
 */
@MybatisTest(classes = {WorkerGeneratorMapper.class}, model = DatabaseModel.POSTGRESQL)
@Sql(scripts = "sql/test_create_table.sql")
@DisplayName("测试 WorkerGeneratorMapper")
public class WorkerGeneratorMapperTest {
    @Fit
    private WorkerGeneratorMapper workerGeneratorMapper;

    @Test
    @DisplayName("获取机器ID成功")
    void shouldOkWhenGetWorkerId() {
        WorkerPo workerPo = new WorkerPo();
        for (int i = 1; i <= 3; i++) {
            this.workerGeneratorMapper.getWorkerId(workerPo);
            assertThat(workerPo.getWorkerId()).isEqualTo(i);
        }
    }
}
