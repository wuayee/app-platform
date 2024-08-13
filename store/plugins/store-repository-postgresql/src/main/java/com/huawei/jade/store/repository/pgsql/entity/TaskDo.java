/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.repository.pgsql.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 存入数据库的任务的实体类。
 *
 * @author 鲁为
 * @since 2024-06-06
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskDo extends CommonDo {
    /**
     * 表示任务的唯一标识。
     */
    private String taskName;

    /**
     * 表示任务的上下文。
     */
    private String context;

    /**
     * 表示工具的唯一标识。
     */
    private String toolUniqueName;
}
