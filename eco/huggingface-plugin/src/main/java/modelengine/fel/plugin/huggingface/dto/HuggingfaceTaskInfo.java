/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fel.plugin.huggingface.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 表示 Huggingface 任务的传输对象。
 *
 * @author 邱晓霞
 * @since 2024-09-09
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HuggingfaceTaskInfo {
    /**
     * 表示任务的唯一标识。
     */
    private Long taskId;

    /**
     * 表示任务的名称。
     */
    private String taskName;

    /**
     * 表示任务的描述。
     */
    private String taskDescription;
}