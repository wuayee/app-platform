/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.huawei.fit.jane.task.gateway.Authenticator;
import com.huawei.fit.jober.aipp.service.AippLogService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

/**
 * 为 {@link AippLogController} 提供测试
 *
 * @author 吴宇伦
 * @since 2024-08-22
 */
@ExtendWith(MockitoExtension.class)
public class AippLogControllerTest {
    private AippLogController aippLogController;

    @Mock
    private Authenticator authenticator;

    @Mock
    private AippLogService aippLogService;

    @BeforeEach
    void before() {
        this.aippLogController = new AippLogController(authenticator, aippLogService);
    }

    @Test
    @DisplayName("测试删除指定对话接口")
    void testDeleteLogs() {
        List<Long> logIds = new ArrayList<>();
        logIds.add(123L);
        this.aippLogController.deleteLogs(logIds);
        verify(aippLogService, times(1)).deleteLogs(logIds);
    }
}
