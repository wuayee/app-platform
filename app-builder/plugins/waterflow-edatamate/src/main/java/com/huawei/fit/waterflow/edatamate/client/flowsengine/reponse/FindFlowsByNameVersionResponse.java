/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.edatamate.client.flowsengine.reponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 根据名称和版本查询流程定义response
 *
 * @author 杨祥宇
 * @since 2023/10/13
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FindFlowsByNameVersionResponse {
    private String flowDefinitionId;

    private String metaId;

    private String versionStatus;

    private String releaseTime;
}
