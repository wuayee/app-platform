/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober;

import com.huawei.fit.jober.entity.FlowNodePublishInfo;
import com.huawei.fitframework.annotation.Genericable;

/**
 * 流程推送服务Genericable。
 *
 * @author 陈镕希 c00572808
 * @since 2024-05-21
 */
public interface FlowPublishService {
    /**
     * PUBLISH_NODE_INFO_GENERICABLE
     */
    String PUBLISH_NODE_INFO_GENERICABLE = "1b5ffv4ib16iui8ddizapuejgqtsjj59";

    /**
     * 推送节点信息。
     *
     * @param flowNodePublishInfo 流程节点信息推送对象的 {@link FlowNodePublishInfo}。
     */
    @Genericable(id = PUBLISH_NODE_INFO_GENERICABLE)
    void publishNodeInfo(FlowNodePublishInfo flowNodePublishInfo);
}
