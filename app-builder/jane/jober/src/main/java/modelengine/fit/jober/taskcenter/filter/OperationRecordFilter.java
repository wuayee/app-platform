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
 * 为操作记录提供过滤器。
 *
 * @author 姚江
 * @since 2023-11-17 13:47
 */
@Data
public class OperationRecordFilter {
    private UndefinableValue<List<String>> objectTypes;

    private UndefinableValue<List<String>> objectIds;
}
