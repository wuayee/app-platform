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
 * 为任务提供过滤器。
 *
 * @author 梁济时
 * @since 2023-08-08
 */
@Data
public class TaskFilter {
    private UndefinableValue<List<String>> ids = UndefinableValue.undefined();

    private UndefinableValue<List<String>> names = UndefinableValue.undefined();

    private UndefinableValue<List<String>> templateIds = UndefinableValue.undefined();

    private UndefinableValue<List<String>> categories = UndefinableValue.undefined();

    private UndefinableValue<List<String>> creators = UndefinableValue.undefined();

    private UndefinableValue<List<String>> orderBys = UndefinableValue.undefined();
}
