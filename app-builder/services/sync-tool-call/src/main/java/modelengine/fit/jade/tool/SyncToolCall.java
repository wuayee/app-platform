/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.tool;

import modelengine.fitframework.annotation.Genericable;

/**
 * 同步工具调用接口
 *
 * @author 夏斐
 * @since 2025/3/12
 */
public interface SyncToolCall {
    @Genericable("modelengine.jober.aipp.tool.sync.call")
    String call(String uniqueName, String toolArgs);
}
