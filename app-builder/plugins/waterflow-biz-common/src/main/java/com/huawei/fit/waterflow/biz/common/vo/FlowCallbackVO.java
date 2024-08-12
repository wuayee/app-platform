/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.waterflow.biz.common.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;
import java.util.Set;

/**
 * 流程定义节点回调函数类
 *
 * @author 陈镕希
 * @since 2024-02-28
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlowCallbackVO {
    /**
     * 所在节点的metaId
     */
    private String nodeMetaId;

    /**
     * 回调函数名称
     */
    private String name;

    /**
     * 回调函数类型
     */
    private String type;

    /**
     * 回调函数filteredKeys集合
     * filteredKeys表示用户在回调过程中关心的业务数据key的集合
     */
    private Set<String> filteredKeys;

    /**
     * 回调函数fitables集合
     */
    private Set<String> fitables;

    /**
     * 回调函数属性，所有回调函数中定义的变量作为该属性的key
     */
    private Map<String, String> properties;
}
