/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fel.plugin.huggingface.po;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Huggingface 模型 ORM 对象。
 *
 * @author 何嘉斌
 * @since 2024-09-09
 */
@Data
public class HuggingfaceModelPo {
    /**
     * 主键。
     */
    private Long id;

    /**
     * 模型名字。
     */
    private String modelName;

    /**
     * 模型格式规范。
     */
    private String modelSchema;

    /**
     * Huggingface 任务唯一标识。
     */
    private Long taskId;

    /**
     * 模型创建者。
     */
    private String createdBy;

    /**
     * 模型创建时间。
     */
    private LocalDateTime createdDate;
}