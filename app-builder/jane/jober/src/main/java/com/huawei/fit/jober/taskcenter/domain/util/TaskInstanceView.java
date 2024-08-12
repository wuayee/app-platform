/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.domain.util;

import com.huawei.fit.jober.taskcenter.util.ExecutableSql;

/**
 * 为任务实例提供视图。
 *
 * @author 梁济时
 * @since 2024-01-27
 */
public interface TaskInstanceView {
    /**
     * 构建视图的 SQL。
     *
     * @return 表示视图的 SQL 的 {@link ExecutableSql}。
     */
    ExecutableSql sql();
}
