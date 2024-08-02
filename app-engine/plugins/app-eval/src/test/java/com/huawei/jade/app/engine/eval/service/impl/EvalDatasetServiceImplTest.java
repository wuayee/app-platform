/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.huawei.fitframework.annotation.Fit;
import com.huawei.fitframework.test.annotation.FitTestWithJunit;
import com.huawei.fitframework.test.annotation.Mock;
import com.huawei.jade.app.engine.eval.dto.EvalDatasetQueryParam;
import com.huawei.jade.app.engine.eval.entity.EvalDatasetEntity;
import com.huawei.jade.app.engine.eval.mapper.EvalDatasetMapper;
import com.huawei.jade.app.engine.eval.service.EvalDataService;
import com.huawei.jade.app.engine.eval.service.EvalDatasetService;
import com.huawei.jade.common.vo.PageVo;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 表示 {@link EvalDatasetServiceImpl} 的测试集。
 *
 * @author 何嘉斌
 * @since 2024-07-31
 */
@FitTestWithJunit(includeClasses = EvalDatasetServiceImpl.class)
public class EvalDatasetServiceImplTest {
    private static final List<String> TEST_CONTENTS = Arrays.asList("test1", "test2");

    @Fit
    private EvalDatasetService evalDatasetService;

    @Mock
    private EvalDatasetMapper evalDatasetMapper;

    @Mock
    private EvalDataService evalDataService;

    @AfterEach
    void teardown() {
        clearInvocations(this.evalDatasetMapper, this.evalDataService);
    }

    @Test
    @DisplayName("创建评估数据集成功")
    void shouldOkWhenInsertAll() {
        doNothing().when(this.evalDataService).insertAll(anyLong(), anyList());
        EvalDatasetEntity entity = new EvalDatasetEntity();
        entity.setName("ds1");
        entity.setDescription("test dataset");
        entity.setContents(TEST_CONTENTS);
        entity.setSchema("{}");
        entity.setAppId("1");
        this.evalDatasetService.create(entity);
        verify(this.evalDatasetMapper, times(1)).create(any());
    }

    @Test
    @DisplayName("查询全量评估数据集元数据成功")
    void shouldOkWhenListEvalDataset() {
        Long id = 1L;
        String name = "name";
        String desc = "desc";
        String schema = "schema";

        EvalDatasetEntity entity = new EvalDatasetEntity();
        entity.setId(id);
        entity.setName(name);
        entity.setDescription(desc);
        entity.setSchema(schema);
        List<EvalDatasetEntity> entities = Collections.singletonList(entity);

        when(this.evalDatasetMapper.listEvalDataset(any())).thenReturn(entities);
        when(this.evalDatasetMapper.countEvalDataset(any())).thenReturn(1);

        EvalDatasetQueryParam queryParam = new EvalDatasetQueryParam();
        PageVo<EvalDatasetEntity> response = this.evalDatasetService.listEvalDataset(queryParam);

        EvalDatasetEntity firstEntity = response.getItems().get(0);
        assertThat(response).extracting(PageVo::getTotal, r -> r.getItems().size()).containsExactly(1, 1);
        assertThat(firstEntity).extracting(EvalDatasetEntity::getId,
                EvalDatasetEntity::getName,
                EvalDatasetEntity::getDescription,
                EvalDatasetEntity::getSchema).containsExactly(id, name, desc, schema);
    }

    @Test
    @DisplayName("根据数据集 ID 查询评估数据集元数据成功")
    void shouldOkWhenGetEvalDatasetById() {
        Long id = 1L;
        String name = "name";
        String desc = "desc";
        String schema = "schema";

        EvalDatasetEntity entity = new EvalDatasetEntity();
        entity.setId(null);
        entity.setName(name);
        entity.setDescription(desc);
        entity.setSchema(schema);

        when(this.evalDatasetMapper.getEvalDatasetById(id)).thenReturn(entity);

        EvalDatasetEntity response = this.evalDatasetService.getEvalDatasetById(id);

        assertThat(response).extracting(EvalDatasetEntity::getId,
                EvalDatasetEntity::getName,
                EvalDatasetEntity::getDescription,
                EvalDatasetEntity::getSchema).containsExactly(null, name, desc, schema);
    }
}
