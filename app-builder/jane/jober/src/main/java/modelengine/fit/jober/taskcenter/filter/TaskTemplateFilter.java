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
 * 为任务模板提供过滤器
 *
 * @author 姚江
 * @since 2023-12-04
 */
@Data
public class TaskTemplateFilter {
    private UndefinableValue<List<String>> ids = UndefinableValue.undefined();

    private UndefinableValue<List<String>> names = UndefinableValue.undefined();
}
