/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fit.waterflow.flowsengine.biz.service.entity;

import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowContext;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowData;
import modelengine.fit.waterflow.flowsengine.domain.flows.context.FlowRetry;
import modelengine.fit.waterflow.flowsengine.domain.flows.streams.To;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 重试数据相关信息
 *
 * @author yangxiangyu
 * @since 2024/5/17
 */
@Setter
@Getter
@Builder
public class FlowRetryInfo {
    private FlowRetry flowRetry;

    private To<FlowData, Object> to;

    private List<FlowContext<String>> flowContexts;
}
