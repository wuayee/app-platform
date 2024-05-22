/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.tool;

import com.huawei.fitframework.annotation.Genericable;

/**
 * @author 邬涨财 w00575064
 * @since 2024-05-20
 */
public interface AppBuilderAppTool {
    @Genericable(id = "com.huawei.fit.jober.aipp.tool.create.app")
    String createApp(String appInfo);
}
