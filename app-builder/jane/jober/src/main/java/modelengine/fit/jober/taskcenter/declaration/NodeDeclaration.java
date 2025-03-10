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
 * 为任务树的节点提供声明。
 *
 * @author 梁济时
 * @since 2023-08-09
 */
@Data
public class NodeDeclaration {
    private UndefinableValue<String> name;

    private UndefinableValue<String> parentId;

    private UndefinableValue<List<String>> sourceIds;
}
