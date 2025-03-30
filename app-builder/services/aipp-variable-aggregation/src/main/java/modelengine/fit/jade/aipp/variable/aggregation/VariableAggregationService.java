/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.variable.aggregation;

import modelengine.fitframework.annotation.Genericable;

import java.util.List;

/**
 * 变量聚合算子服务。
 *
 * @author 张越
 * @since 2024-12-18
 */
public interface VariableAggregationService {
    /**
     * 变量聚合。
     *
     * @param variables 表示需要聚合的变量。
     * @return 表示聚合后返回的变量 {@link Object}。
     */
    @Genericable("modelengine.jober.aipp.variable.aggregation")
    Object aggregate(List<Object> variables);
}
