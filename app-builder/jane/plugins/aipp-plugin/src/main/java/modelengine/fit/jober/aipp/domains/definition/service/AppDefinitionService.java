/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2025-2025. All rights reserved.
 */

package modelengine.fit.jober.aipp.domains.definition.service;

import modelengine.fit.jober.aipp.dto.AippDto;
import modelengine.fit.jade.waterflow.entity.FlowDefinitionResult;

/**
 * 定义服务接口.
 *
 * @author 张越
 * @since 2025-02-08
 */
public interface AppDefinitionService {
    /**
     * 获取相同的定义.
     *
     * @param aippDto 参数.
     * @return {@link FlowDefinitionResult} 对象.
     */
    FlowDefinitionResult getSameFlowDefinition(AippDto aippDto);
}
