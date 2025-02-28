/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import modelengine.fitframework.annotation.Property;

/**
 * w3待办任务
 *
 * @author 刘信宏
 * @since 2024-01-19
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class W3Task {
    @Property(description = "owner")
    private String owner;

    @Property(description = "任务title")
    private String title;

    @Property(description = "任务详情", name = "task_detail")
    private String taskDetail;
}
