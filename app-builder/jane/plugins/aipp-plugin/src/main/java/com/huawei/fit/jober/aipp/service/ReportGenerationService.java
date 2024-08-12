/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.service;

import com.huawei.fitframework.annotation.Genericable;

/**
 * 生成报告接口类。
 *
 * @author 李鑫
 * @since 2024-06-15
 */
public interface ReportGenerationService {
    /**
     * 生成分析报告
     *
     * @param chatHistory 表示聊天记录的json字符串。
     * @return 生成的分析报告字符串。
     */
    @Genericable(id = "com.huawei.fit.finance.generate.report")
    String generateOperationReport(String chatHistory);
}
