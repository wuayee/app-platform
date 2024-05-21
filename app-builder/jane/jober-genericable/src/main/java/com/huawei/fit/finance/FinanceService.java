/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.finance;

import com.huawei.fitframework.annotation.Genericable;

/**
 * FinanceService
 *
 * @author 易文渊
 * @since 2024-04-27
 */
public interface FinanceService {
    @Genericable(id = "com.huawei.fit.finance.router")
    NLRouter nlRouter(String query);

    @Genericable(id = "com.huawei.fit.finance.autoGraph")
    String autoGraph(String sql, String query);

    @Genericable(id = "com.huawei.fit.finance.generate.report")
    String generateOperationReport(String chatHistory);
}