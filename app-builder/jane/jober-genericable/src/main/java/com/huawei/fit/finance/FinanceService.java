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
    /**
     * 路由器
     *
     * @param query 查询条件
     * @return 路由器结构体
     */
    @Genericable(id = "com.huawei.fit.finance.router")
    NlRouter nlRouter(String query);

    /**
     * 签名
     *
     * @param sql 执行的sql
     * @param query 查询条件
     * @return 查询得到的签名
     */
    @Genericable(id = "com.huawei.fit.finance.autoGraph")
    String autoGraph(String sql, String query);
}