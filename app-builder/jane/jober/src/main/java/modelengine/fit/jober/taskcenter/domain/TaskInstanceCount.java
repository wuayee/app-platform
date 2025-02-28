/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.domain;

import lombok.Data;

/**
 * 表示任务实例的数量。
 *
 * @author 梁济时
 * @since 2023-08-25
 */
@Data
public class TaskInstanceCount {
    private String taskId;

    private String taskName;

    private Long count;
}
