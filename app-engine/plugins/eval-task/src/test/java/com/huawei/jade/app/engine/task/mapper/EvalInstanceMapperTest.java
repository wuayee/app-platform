/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.task.mapper;

import static com.huawei.jade.app.engine.task.entity.EvalInstanceStatusEnum.RUNNING;
import static com.huawei.jade.app.engine.task.entity.EvalInstanceStatusEnum.SUCCESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

import com.huawei.jade.app.engine.task.dto.EvalInstanceQueryParam;
import com.huawei.jade.app.engine.task.entity.EvalInstanceEntity;
import com.huawei.jade.app.engine.task.po.EvalInstancePo;

import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.MybatisTest;
import modelengine.fitframework.test.annotation.Sql;
import modelengine.fitframework.test.domain.db.DatabaseModel;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

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

    @Test
    @Sql(scripts = "sql/test_insert_eval_instance.sql")
    @DisplayName("查询评估任务实例成功")
    void shouldOkWhenQueryEvalInstance() {
        EvalInstanceQueryParam queryParam = new EvalInstanceQueryParam();
        queryParam.setTaskId(1L);
        queryParam.setPageSize(5);
        queryParam.setPageIndex(1);
        List<EvalInstanceEntity> entities = this.mapper.listEvalInstance(queryParam);
        assertThat(entities.size()).isEqualTo(2);
        assertThat(entities).extracting(EvalInstanceEntity::getStatus,
                        EvalInstanceEntity::getPassRate,
                        EvalInstanceEntity::getCreatedBy)
                .containsExactly(tuple(RUNNING, 76.0, "sky"), tuple(SUCCESS, 95.0, "fang"));
    }

    @Test
    @Sql(scripts = "sql/test_insert_eval_instance.sql")
    @DisplayName("统计评估任务实例成功")
    void shouldOkWhenCountEvalInstance() {
        EvalInstanceQueryParam queryParam = new EvalInstanceQueryParam();
        queryParam.setTaskId(1L);
        int count = this.mapper.countEvalInstance(queryParam);
        assertThat(count).isEqualTo(2);
    }
}
