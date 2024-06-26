/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.waterflow.flowsengine.domain.flows.definitions.nodes.converter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 映射配置节点
 *
 * @author s00558940
 * @since 2024/4/18
 */
@AllArgsConstructor
@Builder
@Getter
public class MappingNode {
    private String name;

    private MappingNodeType type;

    private MappingFromType from;

    /**
     * from为reference时，表示对应节点下的参数key
     * from为value时，表示对应value的解析结构
     */
    private Object value;

    /**
     * 引用节点的id, from为reference时才有意义
     */
    private String referenceNode;
}
