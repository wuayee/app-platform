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
 * 为任务属性触发器提供过滤器。
 *
 * @author 梁济时
 * @since 2023-08-09
 */
@Data
public class TriggerFilter {
    private UndefinableValue<List<String>> ids;

    private UndefinableValue<List<String>> sourceIds;

    private UndefinableValue<List<String>> propertyIds;

    private UndefinableValue<List<String>> fitableIds;
}
