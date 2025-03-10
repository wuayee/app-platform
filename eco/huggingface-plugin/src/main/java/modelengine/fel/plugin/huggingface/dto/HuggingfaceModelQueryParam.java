/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fel.plugin.huggingface.dto;

import modelengine.jade.common.query.PageQueryParam;

import lombok.Data;
import lombok.NoArgsConstructor;
import modelengine.fitframework.annotation.Property;
import modelengine.fitframework.validation.constraints.Positive;

/**
 * 表示 Huggingface 模型元数据查询参数。
 *
 * @author 邱晓霞
 * @since 2024-09-11
 */
@Data
@NoArgsConstructor
public class HuggingfaceModelQueryParam extends PageQueryParam {
    @Property(description = "任务主键", required = true)
    @Positive(message = "Task ID range should between [0, Long.MAX_VALUE].")
    private Long taskId;

    /**
     * 表示 {@linkHuggingfaceModelQueryParam} 的构造器。
     *
     * @param taskId 表示任务主键的 {@link Long}
     * @param pageIndex 表示页码的 {@link Integer}。
     * @param pageSize 表示页面大小的 {@link Integer}。
     */
    public HuggingfaceModelQueryParam(Long taskId, Integer pageIndex, Integer pageSize) {
        super(pageIndex, pageSize);
        this.taskId = taskId;
    }
}
