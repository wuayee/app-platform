/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.task.service.impl;

import static com.huawei.jade.app.engine.task.entity.EvalInstanceStatusEnum.RUNNING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.huawei.jade.app.engine.task.dto.EvalInstanceQueryParam;
import com.huawei.jade.app.engine.task.entity.EvalInstanceEntity;
import com.huawei.jade.app.engine.task.mapper.EvalInstanceMapper;
import com.huawei.jade.app.engine.task.service.EvalInstanceService;
import com.huawei.jade.common.vo.PageVo;

import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.FitTestWithJunit;
import modelengine.fitframework.test.annotation.Mock;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

/**
 * 表示 {@link EvalInstanceServiceImpl} 的测试集。
 *
 * @author 何嘉斌
 * @since 2024-08-12
 */
@FitTestWithJunit(includeClasses = EvalInstanceServiceImpl.class)
public class EvalInstanceServiceImplTest {
    @Fit
    private EvalInstanceService service;

    @Mock
    private EvalInstanceMapper mapper;

    @AfterEach
    void teardown() {
        clearInvocations(this.mapper);
    }

    @Test
    @DisplayName("插入评估任务实例后，回填主键成功")
    void shouldOkWhenCreateEvalInstance() {
        doNothing().when(this.mapper).create(any());
        this.service.createEvalInstance(1L);
        verify(this.mapper, times(1)).create((any()));
    }

    @Test
    @DisplayName("查询评估任务实例成功")
    void shouldOkWhenListEvalInstance() {
        EvalInstanceEntity entity = new EvalInstanceEntity();
        entity.setPassRate(76.0);
        entity.setCreatedBy("Sky");
        entity.setStatus(RUNNING);
        List<EvalInstanceEntity> entities = Collections.singletonList(entity);
        when(this.mapper.listEvalInstance(any())).thenReturn(entities);

        EvalInstanceQueryParam queryParam = new EvalInstanceQueryParam();
        queryParam.setTaskId(1L);
        queryParam.setPageSize(5);
        queryParam.setPageIndex(1);

        PageVo<EvalInstanceEntity> response = this.service.listEvalInstance(queryParam);
        EvalInstanceEntity firstItem = response.getItems().get(0);
        assertThat(firstItem).extracting(EvalInstanceEntity::getPassRate,
                EvalInstanceEntity::getCreatedBy,
                EvalInstanceEntity::getStatus).containsExactly(76.0, "Sky", RUNNING);
    }
}