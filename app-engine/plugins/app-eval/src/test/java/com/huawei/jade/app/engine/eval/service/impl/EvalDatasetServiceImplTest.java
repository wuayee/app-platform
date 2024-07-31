/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.huawei.fitframework.annotation.Fit;
import com.huawei.fitframework.test.annotation.FitTestWithJunit;
import com.huawei.fitframework.test.annotation.Mock;
import com.huawei.jade.app.engine.eval.entity.EvalDatasetEntity;
import com.huawei.jade.app.engine.eval.mapper.EvalDatasetMapper;
import com.huawei.jade.app.engine.eval.service.EvalDataService;
import com.huawei.jade.app.engine.eval.service.EvalDatasetService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
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
}
