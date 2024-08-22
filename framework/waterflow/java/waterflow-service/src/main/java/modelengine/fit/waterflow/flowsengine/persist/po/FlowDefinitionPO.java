/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.waterflow.flowsengine.persist.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 流程定义数据库持久化类
 * 持久化流程定义
 *
 * @author 高诗意
 * @since 2023/08/14
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlowDefinitionPO {
    /**
     * 流程定义ID
     */
    private String definitionId;

    /**
     * 流程定义metaID，与前端保持一致
     */
    private String metaId;

    /**
     * 流程定义名称
     */
    private String name;

    /**
     * 流程定义版本
     */
    private String version;

    /**
     * 租户唯一标识的
     */
    private String tenant;

    /**
     * 创建人
     */
    private String createdBy;

    /**
     * 发布时间
     */
    private String createdAt;

    /**
     * 流程定义关系图
     */
    private String graph;

    /**
     * 流程定义状态
     */
    private String status;
}
