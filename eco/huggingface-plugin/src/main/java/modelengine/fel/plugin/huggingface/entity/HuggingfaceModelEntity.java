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
 * 表示 Huggingface 模型的实体对象。
 *
 * @author 何嘉斌
 * @since 2024-09-09
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HuggingfaceModelEntity {
    /**
     * 表示模型名字。
     */
    private String modelName;

    /**
     * 表示模型格式规范。
     */
    private String modelSchema;

    /**
     * 表示 Huggingface 任务唯一标识。
     */
    private Long taskId;
}