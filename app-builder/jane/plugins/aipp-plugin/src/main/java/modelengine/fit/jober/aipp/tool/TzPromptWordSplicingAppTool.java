/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jober.aipp.tool;

import modelengine.fitframework.annotation.Genericable;

/**
 * 天舟相关接口
 *
 * @author 晏钰坤
 * @since 2024/5/31
 */
public interface TzPromptWordSplicingAppTool {
    /**
     * 提示词拼接
     *
     * @param appId 应用ID
     * @param instanceId 流程实例ID
     * @param input 用户输入
     * @return 处理后的提示词
     */
    @Genericable(id = "modelengine.fit.jober.aipp.tool.tianzhou.prompt.word.splice")
    String promptWordSplice(String appId, String instanceId, String input);
}