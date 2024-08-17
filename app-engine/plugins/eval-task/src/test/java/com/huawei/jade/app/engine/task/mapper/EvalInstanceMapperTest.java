/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.task.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.huawei.fitframework.annotation.Fit;
import com.huawei.fitframework.test.annotation.MybatisTest;
import com.huawei.fitframework.test.annotation.Sql;
import com.huawei.fitframework.test.domain.db.DatabaseModel;
import com.huawei.jade.app.engine.task.po.EvalInstancePo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 表示 {@link EvalInstanceMapper} 的测试集。
 *
 * @author 何嘉斌
 * @since 2024-08-12
 */
@MybatisTest(classes = {EvalInstanceMapper.class}, model = DatabaseModel.POSTGRESQL)
@Sql(scripts = "sql/test_create_table.sql")
@DisplayName("测试 EvalTaskInstanceMapper")
public class EvalInstanceMapperTest {
    @Fit
    private EvalInstanceMapper mapper;

    @Test
    @DisplayName("插入评估任务实例后，回填主键成功")
    void shouldOkWhenCreateEvalInstance() {
        EvalInstancePo po = new EvalInstancePo();
        po.setTaskId(1L);

        this.mapper.create(po);
        assertThat(po.getTaskId()).isNotEqualTo(null);
    }
}
