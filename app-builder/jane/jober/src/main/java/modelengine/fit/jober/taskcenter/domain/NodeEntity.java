/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.domain;

import lombok.Data;
import modelengine.fit.jane.task.util.Entities;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 表示任务树中的节点。
 *
 * @author 梁济时
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
