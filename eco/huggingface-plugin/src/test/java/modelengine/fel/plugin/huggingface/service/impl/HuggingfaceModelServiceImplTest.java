/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fel.plugin.huggingface.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import modelengine.fel.plugin.huggingface.dto.HuggingfaceModelQueryParam;
import modelengine.fel.plugin.huggingface.entity.HuggingfaceModelEntity;
import modelengine.fel.plugin.huggingface.mapper.HuggingfaceModelMapper;
import modelengine.fel.plugin.huggingface.mapper.HuggingfaceTaskMapper;
import modelengine.fel.plugin.huggingface.service.HuggingfaceModelService;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.test.annotation.FitTestWithJunit;
import modelengine.fitframework.test.annotation.Mock;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * 表示 {@link HuggingfaceModelServiceImpl} 的测试用例。
 *
 * @author 何嘉斌
 * @author 邱晓霞
 * @since 2024-09-10
 */
@FitTestWithJunit(includeClasses = {HuggingfaceModelServiceImpl.class})
public class HuggingfaceModelServiceImplTest {
    @Fit
    private HuggingfaceModelService modelService;

    @Mock
    private HuggingfaceModelMapper modelMapper;

    @Mock
    private HuggingfaceTaskMapper taskMapper;

    @Test
    @DisplayName("插入 Huggingface 模型成功")
    void shouldOkWhenInsertModel() {
        doNothing().when(this.modelMapper).insert(any());
        doNothing().when(this.taskMapper).increaseModelCount(anyLong());

        HuggingfaceModelEntity entity = new HuggingfaceModelEntity();
        entity.setModelName("name");
        entity.setModelSchema("desc");
        entity.setTaskId(1L);

        this.modelService.insert(entity);
        verify(this.modelMapper, times(1)).insert(any());
        verify(this.taskMapper, times(1)).increaseModelCount(anyLong());
    }

    @Test
    @DisplayName("查询指定任务模型数据成功")
    void shouldOkWhenQueryModels() {
        List<HuggingfaceModelEntity> modelEntityList = new ArrayList<>();
        HuggingfaceModelEntity modelEntity = new HuggingfaceModelEntity();
        modelEntity.setModelName("name");
        modelEntity.setModelSchema("schema");
        modelEntity.setTaskId(1L);
        modelEntityList.add(modelEntity);
        HuggingfaceModelQueryParam modelQueryParam = new HuggingfaceModelQueryParam();
        modelQueryParam.setTaskId(1L);
        modelQueryParam.setPageIndex(1);
        modelQueryParam.setPageSize(2);
        when(this.modelMapper.listModelPartialInfo(any())).thenReturn(modelEntityList);
        when(this.modelMapper.countModel(any())).thenReturn(2);
        List<HuggingfaceModelEntity> response = this.modelMapper.listModelPartialInfo(modelQueryParam);
        assertThat(response.get(0).getTaskId()).isEqualTo(1L);
        assertThat(response.get(0).getModelName()).isEqualTo("name");
        assertThat(response.get(0).getModelSchema()).isEqualTo("schema");
    }
}