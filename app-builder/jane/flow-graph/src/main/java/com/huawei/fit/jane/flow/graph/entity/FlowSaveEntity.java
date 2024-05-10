/*
 *  Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jane.flow.graph.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 流程入参信息
 *
 * @author y00679285
 * @since 2023/12/13
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlowSaveEntity {
    /**
     * 流程定义图json字符串
     */
    private String graphData;

    /**
     * 流程定义引擎json字符串
     */
    private String definitionData;

    /**
     * 流程所属标签
     */
    private List<String> tags;

    /**
     * 流程id
     */
    private String id;

    /**
     * 流程版本
     */
    private String version;

    /**
     * 流程来源模板id
     */
    private String previous;

    /**
     * 流程状态
     */
    private String status;
}
