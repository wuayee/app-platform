/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.taskcenter.filter;

import lombok.Data;
import modelengine.fit.jane.task.util.UndefinableValue;

import java.util.List;
import java.util.Map;

/**
 * 为任务实例提供过滤器。
 *
 * @author 梁济时
 * @since 2023-08-14
 */
@Data
public class InstanceFilter {
    private UndefinableValue<List<String>> ids;

    private UndefinableValue<List<String>> typeIds;

    private UndefinableValue<List<String>> sourceIds;

    private UndefinableValue<List<String>> tags;

    private UndefinableValue<List<String>> categories;

    private UndefinableValue<Map<String, List<String>>> infos;

    private UndefinableValue<List<String>> orderBy;
}
