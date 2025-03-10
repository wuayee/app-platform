/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.domain;

import lombok.Data;

import java.util.List;

/**
 * 表示任务的类目变更触发器。
 *
 * @author 梁济时
 * @since 2023-08-23
 */
@Data
public class TaskCategoryTriggerEntity {
    private String category;

    private List<String> fitableIds;
}
