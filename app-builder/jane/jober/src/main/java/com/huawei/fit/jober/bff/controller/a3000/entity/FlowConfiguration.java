/*
 *  Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.bff.controller.a3000.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 流程模板配置信息
 *
 * @author 杨祥宇
 * @since 2024/7/15
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FlowConfiguration {
    /**
     * tag列表
     */
    private List<String> tags;

    /**
     * 操作人
     */
    private String operator;

    /**
     * 流程模板json信息
     */
    private String definitionData;

    /**
     * 父流程Id
     */
    private String previous;
}
