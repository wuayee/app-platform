/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.waterflow.flowsengine.biz.service.entity;

import modelengine.fit.waterflow.flowsengine.domain.flows.context.ContextErrorInfo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * 流程实例运行错误节点信息
 *
 * @author 杨祥宇
 * @since 2023/12/19
 */
@Getter
@Setter
@Builder
public class FlowsErrorInfo {
    /**
     * 错误context的businessData数据
     */
    private Map<String, Object> businessData;

    /**
     * 错误context的报错信息
     */
    private ContextErrorInfo contextErrorInfo;

    /**
     * 错误context所在节点id
     */
    private String nodeId;

    /**
     * 错误context所在节点名称
     */
    private String nodeName;
}
