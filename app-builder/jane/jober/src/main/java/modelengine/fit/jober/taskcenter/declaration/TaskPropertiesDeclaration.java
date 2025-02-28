/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.declaration;

import lombok.Data;
import modelengine.fit.jane.task.domain.TaskProperty;
import modelengine.fit.jober.taskcenter.domain.TaskTemplate;

import java.util.List;

/**
 * 为批量属性提供声明。
 *
 * @author 梁济时
 * @since 2023-08-08
 */
@Data
public class TaskPropertiesDeclaration {
    private String taskId;

    private List<TaskProperty.Declaration> properties;

    private TaskTemplate template;
}
