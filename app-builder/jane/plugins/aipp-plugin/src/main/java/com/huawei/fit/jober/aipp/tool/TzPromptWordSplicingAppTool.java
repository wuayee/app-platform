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
    @Genericable(id = "com.huawei.fit.jober.aipp.tool.tianzhou.prompt.word.splice")
    String promptWordSplice(String appId, String instanceId, String input);
}