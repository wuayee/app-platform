/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.tool;

import com.huawei.fitframework.annotation.Genericable;

/**
 * 天舟相关接口
 *
 * @author y00693950
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
    @Genericable(id = "com.huawei.fit.jober.aipp.tool.tianzhou.prompt.word.splice")
    String promptWordSplice(String appId, String instanceId, String input);
}