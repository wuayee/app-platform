/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.huawei.fit.jane.common.entity.OperationContext;
import com.huawei.fit.jober.aipp.dto.ToolDto;
import com.huawei.fit.jober.aipp.mapper.AppBuilderAppMapper;
import com.huawei.fit.jober.aipp.service.impl.StoreServiceImpl;
import com.huawei.jade.carver.ListResult;
import com.huawei.jade.carver.tool.model.query.ToolQuery;
import com.huawei.jade.carver.tool.model.transfer.ToolData;
import com.huawei.jade.carver.tool.service.ToolService;
import com.huawei.jade.store.service.EcoTaskService;
import com.huawei.jade.store.service.HuggingFaceModelService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;

/**
 * 市场相关接口测试。
 *
 * @author 鲁为
 * @since 2024-08-02
 */
@DisplayName("测试 StoreServiceImpl")
public class StoreServiceImplTest {
    private StoreServiceImpl storeService;

    @BeforeEach
    void setUp() {
        ToolService toolService = mock(ToolService.class);
        EcoTaskService ecoTaskService = mock(EcoTaskService.class);
        HuggingFaceModelService huggingFaceModelService = mock(HuggingFaceModelService.class);
        AppBuilderAppMapper appBuilderAppMapper = mock(AppBuilderAppMapper.class);
        this.storeService = new StoreServiceImpl(toolService, ecoTaskService, huggingFaceModelService,
                appBuilderAppMapper);
        when(toolService.searchTools(any())).thenReturn(this.buildToolData());
    }

    @Test
    @DisplayName("当根据参数查询工具时，返回正确结果。")
    void shouldReturnCorrectToolWhenSearchTools() {
        ToolDto res = this.storeService.getPlugins(new ToolQuery(),
                new OperationContext());
        assertThat(res.getToolData().get(0).getUniqueName().equals("testUniqueName"));
    }

    private ListResult<ToolData> buildToolData() {
        ToolData toolData = new ToolData();
        toolData.setUniqueName("testUniqueName");
        return ListResult.create(Collections.singletonList(toolData), 1);
    }
}
