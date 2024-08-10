/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.carver.tool.repository.pgsql.support;

import static org.mockito.Mockito.verify;

import com.huawei.jade.carver.tool.Tool;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

/**
 * 表示 {@link DefaultToolRepository} 的单元测试。
 *
 * @author 鲁为
 * @since 2024-08-08
 */
@DisplayName("测试 DefaultToolRepository")
public class DefaultToolRepositoryTest {
    @Mock
    private DefaultToolRepository toolRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("验证插入工具列表成功")
    void shouldSuccessWhenAddTool() {
        Tool.Info info = Tool.Info.custom().uniqueName("testUniqueName").build();
        this.toolRepository.addTool(info);
        verify(toolRepository).addTool(info);
    }

    @Test
    @DisplayName("验证删除工具列表成功")
    void shouldSuccessWhenDeleteTools() {
        List<String> uniqueNames = new ArrayList<>();
        this.toolRepository.deleteTools(uniqueNames);
        verify(toolRepository).deleteTools(uniqueNames);
    }
}
