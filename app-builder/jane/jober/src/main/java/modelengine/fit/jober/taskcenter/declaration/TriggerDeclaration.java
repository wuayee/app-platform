/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.declaration;

import lombok.Data;
import modelengine.fit.jane.task.util.UndefinableValue;

/**
 * 表示任务属性触发器的定义。
 *
 * @author 陈镕希
 * @since 2023-08-07
 */
@Data
public class TriggerDeclaration {
    private UndefinableValue<String> propertyName;

    private UndefinableValue<String> fitableId;
}
