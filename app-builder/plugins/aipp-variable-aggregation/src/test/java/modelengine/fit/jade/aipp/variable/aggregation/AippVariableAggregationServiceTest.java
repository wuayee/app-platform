/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.variable.aggregation;

import modelengine.fitframework.util.ObjectUtils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link AippVariableAggregationService}测试集。
 *
 * @author 张越
 * @since 2024-12-18
 */
@DisplayName("变量聚合算子对外暴露端口")
public class AippVariableAggregationServiceTest {
    private final VariableAggregationService variableAggregationService = new AippVariableAggregationService();

    @Test
    @DisplayName("变量列表最后一项有值，则返回最后一项")
    void shouldReturnLastWhenLastIsNotNull() {
        List<Object> variables = new ArrayList<>();
        variables.add("1");
        variables.add("2");
        variables.add("3");
        variables.add("4");
        variables.add("5");
        Object result = this.variableAggregationService.aggregate(variables);
        Assertions.assertEquals("5", ObjectUtils.cast(result));
    }

    @Test
    @DisplayName("返回变量列表中最后一个不为null的值")
    void shouldReturnNotNullItemWhenHasNullItem() {
        List<Object> variables = new ArrayList<>();
        variables.add("1");
        variables.add("2");
        variables.add("3");
        variables.add(null);
        variables.add(null);
        Object result = this.variableAggregationService.aggregate(variables);
        Assertions.assertEquals("3", ObjectUtils.cast(result));
    }
}