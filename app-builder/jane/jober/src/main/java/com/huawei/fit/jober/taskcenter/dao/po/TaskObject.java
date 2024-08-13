/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.dao.po;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * task表的数据访问层的Object实体
 *
 * @author 梁致强
 * @since 2023-08-08
 */
@Data
public class TaskObject {
    private String id;

    private String name;

    private String category;

    private String attributes;

    private String tenantId;

    private String templateId;

    private String createdBy;

    private String updatedBy;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
