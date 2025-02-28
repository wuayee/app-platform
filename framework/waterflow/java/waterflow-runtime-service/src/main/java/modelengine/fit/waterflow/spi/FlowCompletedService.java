/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.spi;

import modelengine.fit.waterflow.entity.FlowTransCompletionInfo;
import modelengine.fitframework.annotation.Genericable;

/**
 * 流程运行结束回调Genericable
 *
 * @author yangxiangyu
 * @since 2024/9/10
 */
public interface FlowCompletedService {
    /**
     * 流程运行结束回调Genericable id
     */
    String FLOW_CALLBACK_GENERICABLE = "17b7e95402c5472180575fd35e9e8b8f";

    /**
     * 流程运行结束回调接口
     *
     * @param info 流程完成回调信息
     */
    @Genericable(id = FLOW_CALLBACK_GENERICABLE)
    void callback(FlowTransCompletionInfo info);
}