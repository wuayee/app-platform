/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.carver.tool.waterflow;

/**
 * WaterFlowTool Constants
 *
 * @author 黄夏露
 * @since 2024-05-27
 */
public class WaterFlowToolConst {
    /**
     * 工具流 schema 中动态参数的位置
     */
    public static final String INPUT_PARAMS_KEY = "inputParams";

    /**
     * 工具流上下文中需要包含的追踪 id
     */
    public static final String TRACE_ID = "traceId";

    /**
     * 工具流上下文中需要包含的回调函数 id
     */
    public static final String CALLBACK_ID = "callbackId";

    /**
     * 工具流上下文中需要包含的用户 id
     */
    public static final String USER_ID = "userId";

    /**
     * schema 中 runnable 中信息的 name
     */
    public static final String APP_RUNNABLE_NAME = "APP";
}
