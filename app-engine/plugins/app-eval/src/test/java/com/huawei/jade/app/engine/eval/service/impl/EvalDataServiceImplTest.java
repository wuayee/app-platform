/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.app.engine.eval.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.huawei.fitframework.annotation.Fit;
import com.huawei.fitframework.test.annotation.FitTestWithJunit;
import com.huawei.fitframework.test.annotation.Mocked;
import com.huawei.jade.app.engine.eval.code.AppEvalRetCodeEnum;
import com.huawei.jade.app.engine.eval.dto.EvalDataQueryParam;
import com.huawei.jade.app.engine.eval.entity.EvalDataEntity;
import com.huawei.jade.app.engine.eval.exception.AppEvalException;
import com.huawei.jade.app.engine.eval.manager.EvalDataValidator;
import com.huawei.jade.app.engine.eval.manager.EvalDatasetVersionManager;
import com.huawei.jade.app.engine.eval.mapper.EvalDataMapper;
import com.huawei.jade.app.engine.eval.service.EvalDataService;
import com.huawei.jade.common.vo.PageVo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 表示 {@link EvalDataServiceImpl} 的测试用例。
 *
 * @author 易文渊
 * @since 2024-07-22
 */
@FitTestWithJunit(classes = EvalDataServiceImpl.class)
public class EvalDataServiceImplTest {
    private static final List<String> TEST_CONTENTS = Arrays.asList("test1", "test2");

    @Fit
    private EvalDataService evalDataService;

    @Mocked
    private EvalDataMapper evalDataMapper;

    @Mocked
    private EvalDataValidator evalDataValidator;

    @Mocked
    private EvalDatasetVersionManager evalDatasetVersionManager;

    @BeforeEach
    void setUp() {
        when(this.evalDatasetVersionManager.applyVersion()).thenReturn(1L);
        doNothing().when(this.evalDataMapper).insertAll(anyList());
        when(this.evalDataMapper.updateExpiredVersion(anyList(), anyLong())).thenReturn(1);
    }

    @Test
    @DisplayName("批量插入评估数据成功")
    void shouldOkWhenInsertAll() {
        doNothing().when(this.evalDataValidator).verify(anyLong(), anyList());
        this.evalDataService.insertAll(1L, TEST_CONTENTS);
        verify(this.evalDataMapper, times(1)).insertAll(anyList());
    }

    @Test
    @DisplayName("批量软删除评估数据成功")
    void shouldOkWhenDelete() {
        this.evalDataService.delete(Collections.singletonList(1L));
        verify(this.evalDataMapper, times(1)).updateExpiredVersion(anyList(), anyLong());
    }

    @Test
    @DisplayName("批量插入评估数据，校验 schema 失败")
    void shouldFailWhenVerifyError() {
        doThrow(new AppEvalException(AppEvalRetCodeEnum.EVAL_DATA_INVALID_ERROR, "a", "b")).when(this.evalDataValidator)
                .verify(anyLong(), anyList());
        assertThatThrownBy(() -> this.evalDataService.insertAll(1L,
                TEST_CONTENTS)).isInstanceOf(AppEvalException.class);
    }

    @Test
    @DisplayName("查询数据成功")
    void shouldOkWhenListEvalData() {
        EvalDataEntity entity = new EvalDataEntity();
        entity.setId(1L);
        entity.setContent("abcd");
        List<EvalDataEntity> entities = Collections.singletonList(entity);

        when(this.evalDataMapper.listEvalData(any())).thenReturn(entities);
        when(this.evalDataMapper.countEvalData(any())).thenReturn(1);

        EvalDataQueryParam queryParam = new EvalDataQueryParam();
        queryParam.setDatasetId(1L);
        queryParam.setVersion(3L);
        queryParam.setPageIndex(1);
        queryParam.setPageSize(2);

        PageVo<EvalDataEntity> response = this.evalDataService.listEvalData(queryParam);
        assertThat(response.getTotal()).isEqualTo(1);
        assertThat(response.getItems().size()).isEqualTo(1);
        assertThat(response.getItems().get(0).getContent()).isEqualTo("abcd");
    }
}