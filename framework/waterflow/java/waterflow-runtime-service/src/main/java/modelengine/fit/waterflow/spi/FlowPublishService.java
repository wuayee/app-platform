/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.spi;

import modelengine.fit.waterflow.entity.FlowNodePublishInfo;
import modelengine.fitframework.annotation.Genericable;

/**
 * 流程推送服务Genericable。
 *
 * @author 陈镕希
 * @since 2024-05-21
 */
public interface FlowPublishService {
    /**
     * PUBLISH_NODE_INFO_GENERICABLE
     */
    String PUBLISH_NODE_INFO_GENERICABLE = "493b419f8e3641d88d7ff54b460d52ba";

    /**
     * 推送节点信息。
     *
     * @param flowNodePublishInfo 流程节点信息推送对象的 {@link FlowNodePublishInfo}。
     */
    @Genericable(id = PUBLISH_NODE_INFO_GENERICABLE)
    void publishNodeInfo(FlowNodePublishInfo flowNodePublishInfo);
}
