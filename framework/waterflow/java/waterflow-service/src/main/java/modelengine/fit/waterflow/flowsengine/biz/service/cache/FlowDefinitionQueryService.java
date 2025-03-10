/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.biz.service.cache;

import lombok.AllArgsConstructor;
import modelengine.fit.waterflow.flowsengine.domain.flows.definitions.FlowDefinition;
import modelengine.fitframework.annotation.Component;

/**
 * 流程定义查询缓存服务
 *
 * @author yangxiangyu
 * @since 2025/1/24
 */
@Component
@AllArgsConstructor
public class FlowDefinitionQueryService {
    private final FlowCacheService cacheService;

    /**
     * 根据streamId查找流程定义
     *
     * @param streamId stream id
     * @return 流程定义
     */
    public FlowDefinition findByStreamId(String streamId) {
        return cacheService.getDefinitionByStreamId(streamId);
    }

    /**
     * 根据流程定义id获取流程定义
     *
     * @param definitionId 流程定义id
     * @return 流程定义
     */
    public FlowDefinition findByDefinitionId(String definitionId) {
        return cacheService.getDefinitionById(definitionId);
    }
}
