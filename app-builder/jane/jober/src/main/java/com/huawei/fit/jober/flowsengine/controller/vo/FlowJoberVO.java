/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.flowsengine.controller.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;
import java.util.Set;

/**
 * 流程节点自动任务VO类
 *
 * @author 陈镕希 c00572808
 * @since 2024-02-28
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlowJoberVO {
    /**
     * 所在节点的metaId
     */
    private String nodeMetaId;

    /**
     * jober所在的节点位置
     */
    private String nodeId;

    /**
     * 节点任务名称
     */
    private String name;

    /**
     * 节点任务类型
     */
    private String type;

    /**
     * 节点任务属性，所有任务中定义的变量作为该属性的key
     */
    private Map<String, String> properties;

    /**
     * 节点任务的fitables集合，不同的jober内置的fitables数量不一致
     */
    private Set<String> fitables;

    /**
     * 节点任务异常处理fitables集合
     */
    private Set<String> exceptionFitables;
}
