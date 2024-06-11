/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.repository.pgsql.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 存入数据库的模型的实体类。
 *
 * @author 鲁为 l00839724
 * @since 2024-06-07
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModelDo extends CommonDo {
    /**
     * 表示模型的创建时间。
     */
    private String createdTime;

    /**
     * 表示模型的更新时间。
     */
    private String updatedTime;

    /**
     * 表示任务的唯一标识。
     */
    private String taskId;

    /**
     * 表示模型的名字。
     */
    private String name;

    /**
     * 表示模型的跳转链接。
     */
    private String url;

    /**
     * 表示任务的上下文。
     */
    private String context;
}
