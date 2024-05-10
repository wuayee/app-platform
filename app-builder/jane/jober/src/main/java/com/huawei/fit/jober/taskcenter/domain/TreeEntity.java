/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.domain;

import com.huawei.fit.jane.task.util.Entities;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 表示任务树。
 *
 * @author 梁济时 l00815032
 * @since 2023-08-09
 */
@Data
public class TreeEntity implements Entities.CreationTraceable, Entities.ModificationTraceable {
    private String id;

    private String name;

    private String taskId;

    private long childCount;

    private String creator;

    private LocalDateTime creationTime;

    private String lastModifier;

    private LocalDateTime lastModificationTime;
}
