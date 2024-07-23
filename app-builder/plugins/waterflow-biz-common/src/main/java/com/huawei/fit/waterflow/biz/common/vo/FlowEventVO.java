/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.waterflow.biz.common.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 流程节点事件VO类
 *
 * @author 陈镕希 c00572808
 * @since 2024-02-28
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlowEventVO {
    /**
     * 节点事件metaId，与前端保持一致
     */
    private String metaId;

    /**
     * 节点事件名称
     */
    private String name;

    /**
     * 来源节点metaId，与前端保持一致
     */
    private String from;

    /**
     * 下一节点metaId，与前端保持一致
     */
    private String to;

    /**
     * 节点事件中条件属性
     */
    private String conditionRule;
}
