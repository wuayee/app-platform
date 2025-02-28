/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.filter;

import lombok.Data;
import modelengine.fit.jane.task.util.UndefinableValue;

import java.util.List;

/**
 * 为任务树的节点提供过滤器。
 *
 * @author 梁济时
 * @since 2023-08-09
 */
@Data
public class NodeFilter {
    private UndefinableValue<List<String>> ids;

    private UndefinableValue<List<String>> names;

    private UndefinableValue<List<String>> parentIds;

    private UndefinableValue<List<String>> sourceIds;
}
