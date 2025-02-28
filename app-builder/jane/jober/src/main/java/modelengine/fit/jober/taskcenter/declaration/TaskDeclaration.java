/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.declaration;

import lombok.Data;
import modelengine.fit.jane.task.domain.TaskProperty;
import modelengine.fit.jane.task.util.UndefinableValue;
import modelengine.fit.jober.taskcenter.domain.Index;

import java.util.List;
import java.util.Map;

/**
 * 表示任务的声明。
 *
 * @author 陈镕希
 * @since 2023-08-07
 */
@Data
public class TaskDeclaration {
    private UndefinableValue<String> name = UndefinableValue.undefined();

    private UndefinableValue<String> category = UndefinableValue.undefined();

    private UndefinableValue<String> templateId = UndefinableValue.undefined();

    private UndefinableValue<Map<String, Object>> attributes = UndefinableValue.undefined();

    private UndefinableValue<List<TaskProperty.Declaration>> properties = UndefinableValue.undefined();

    private UndefinableValue<List<TaskCategoryTriggerDeclaration>> categoryTriggers = UndefinableValue.undefined();

    private UndefinableValue<List<Index.Declaration>> indexes = UndefinableValue.undefined();
}
