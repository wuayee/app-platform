/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fel.plugin.huggingface.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 表示 Huggingface 任务的实体对象。
 *
 * @author 何嘉斌
 * @author 邱晓霞
 * @since 2024-09-09
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HuggingfaceTaskEntity {
    /**
     * 表示任务的唯一标识。
     */
    private Long taskId;

    /**
     * 表示任务名称的唯一编码。
     */
    private String taskNameCode;

    /**
     * 表示任务描述的唯一编码。
     */
    private String taskDescriptionCode;

    /**
     * 表示可用模型数量。
     */
    private Integer totalModelNum;
}