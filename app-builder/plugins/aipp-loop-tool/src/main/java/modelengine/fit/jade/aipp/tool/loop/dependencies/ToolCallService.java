/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.tool.loop.dependencies;

import java.util.Map;

/**
 * 工具调用服务
 *
 * @author 夏斐
 * @since 2025/3/11
 */
public interface ToolCallService {
    /**
     * 调用执行的工具
     *
     * @param uniqueName 工具名
     * @param toolArgs 工具参数
     * @return 工具调用结果
     */
    Object call(String uniqueName, Map<?, ?> toolArgs);
}
