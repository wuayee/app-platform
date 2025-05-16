/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.declaration;

import lombok.Data;
import modelengine.fit.jane.task.util.UndefinableValue;

import java.util.List;
import java.util.Map;

/**
 * 表示任务实例的声明。
 *
 * @author 梁济时
 * @since 2023-08-14
 */
@Data
public class InstanceDeclaration {
    private UndefinableValue<String> typeId;

    private UndefinableValue<String> sourceId;

    private UndefinableValue<Map<String, Object>> info;

    private UndefinableValue<List<String>> tags;
}
