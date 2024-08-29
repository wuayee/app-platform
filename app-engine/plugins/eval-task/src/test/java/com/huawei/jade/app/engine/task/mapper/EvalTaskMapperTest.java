/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.task.mapper;

import static com.huawei.jade.app.engine.task.entity.EvalTaskStatusEnum.PUBLISHED;
import static org.assertj.core.api.Assertions.assertThat;

import com.huawei.jade.app.engine.task.dto.EvalTaskQueryParam;
import com.huawei.jade.app.engine.task.entity.EvalTaskEntity;
import com.huawei.jade.app.engine.task.po.EvalTaskPo;

import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.MybatisTest;
import modelengine.fitframework.test.annotation.Sql;
import modelengine.fitframework.test.domain.db.DatabaseModel;
import modelengine.fitframework.util.StringUtils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

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
    @DisplayName("插入评估任务后，回填主键成功")
    void shouldOkWhenCreateEvalTask() {
        EvalTaskPo evalTaskPo = new EvalTaskPo();
        evalTaskPo.setName("task1");
        evalTaskPo.setDescription("eval task");
        evalTaskPo.setStatus(PUBLISHED);
        evalTaskPo.setAppId("123456");
        evalTaskPo.setWorkflowId("flow1");

        this.evalTaskMapper.create(evalTaskPo);
        assertThat(evalTaskPo.getId()).isNotEqualTo(null);
    }

    @Test
    @Sql(scripts = "sql/test_create_task.sql")
    @DisplayName("分页查询数据集元数据成功")
    void shouldOkWhenListEvalTask() {
        EvalTaskQueryParam queryParam = new EvalTaskQueryParam();
        queryParam.setAppId("123456");
        queryParam.setPageIndex(1);
        queryParam.setPageSize(2);
        List<EvalTaskEntity> taskEntities = this.evalTaskMapper.listEvalTask(queryParam);
        assertThat(taskEntities.size()).isEqualTo(2);

        for (int i = 0; i < taskEntities.size(); i++) {
            EvalTaskEntity entity = taskEntities.get(i);
            assertThat(entity).extracting(EvalTaskEntity::getId,
                            EvalTaskEntity::getName,
                            EvalTaskEntity::getDescription,
                            EvalTaskEntity::getStatus,
                            EvalTaskEntity::getCreatedBy,
                            EvalTaskEntity::getUpdatedBy,
                            EvalTaskEntity::getAppId,
                            EvalTaskEntity::getWorkflowId)
                    .containsExactly(Long.valueOf(i + 1),
                            StringUtils.format("task{0}", i + 1),
                            StringUtils.format("desc{0}", i + 1),
                            PUBLISHED,
                            StringUtils.format("user{0}", i + 1),
                            StringUtils.format("user{0}", i + 1),
                            "123456",
                            StringUtils.format("wf{0}", i + 1));
        }
    }
}