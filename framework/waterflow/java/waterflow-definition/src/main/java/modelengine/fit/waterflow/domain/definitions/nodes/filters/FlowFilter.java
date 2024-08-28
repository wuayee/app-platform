/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.waterflow.domain.definitions.nodes.filters;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import modelengine.fit.waterflow.domain.context.FlowData;
import modelengine.fit.waterflow.domain.enums.FlowFilterType;
import modelengine.fit.waterflow.domain.stream.operators.Operators;

import java.util.Map;

/**
 * 流程节点数据过滤器
 *
 * @author 高诗意
 * @since 1.0
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class FlowFilter {
    private FlowFilterType filterType;

    private Map<String, String> properties;

    /**
     * 得到contexts的过滤器
     *
     * @return 过滤器
     */
    public abstract Operators.Filter<FlowData> filter();
}
