/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.variable.aggregation;

import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Fitable;
import modelengine.fitframework.util.CollectionUtils;

import java.util.List;

/**
 * 变量聚合算子服务。
 *
 * @author 张越
 * @since 2024-12-18
 */
@Component
public class AippVariableAggregationService implements VariableAggregationService {
    @Override
    @Fitable("aipp")
    public Object aggregate(List<Object> variables) {
        if (CollectionUtils.isEmpty(variables)) {
            return null;
        }
        int length = variables.size();
        for (int i = length - 1; i >= 0; i--) {
            if (variables.get(i) != null) {
                return variables.get(i);
            }
        }
        return null;
    }
}
