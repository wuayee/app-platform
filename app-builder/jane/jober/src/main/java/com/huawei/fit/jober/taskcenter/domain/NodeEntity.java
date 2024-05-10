/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.taskcenter.domain;

import com.huawei.fit.jane.task.util.Entities;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 表示任务树中的节点。
 *
 * @author 梁济时 l00815032
 * @since 2023-08-09
 */
@Data
public class NodeEntity implements Entities.CreationTraceable, Entities.ModificationTraceable {
    private String id;

    private String parentId;

    private String name;

    private long childCount;

    private List<String> sourceIds;

    private String creator;

    private LocalDateTime creationTime;

    private String lastModifier;

    private LocalDateTime lastModificationTime;
}
