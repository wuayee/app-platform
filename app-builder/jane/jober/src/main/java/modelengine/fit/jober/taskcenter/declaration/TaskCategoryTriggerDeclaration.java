/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.declaration;

import lombok.Data;
import modelengine.fit.jane.task.util.UndefinableValue;

import java.util.List;

/**
 * 为任务定义的声明提供任务类目变更触发器的声明。
 *
 * @author 梁济时
 * @since 2023-08-23
 */
@Data
public class TaskCategoryTriggerDeclaration {
    private UndefinableValue<String> category;

    private UndefinableValue<List<String>> fitableIds;
}
