/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.tool;

import com.huawei.fitframework.annotation.Genericable;

/**
 * app工具
 *
 * @author 邬涨财 w00575064
 * @since 2024-05-20
 */
public interface AppBuilderAppTool {
    /**
     * 创建app
     *
     * @param appInfo app信息
     * @param userId 创建用户的id
     * @return app
     */
    @Genericable(id = "com.huawei.fit.jober.aipp.tool.create.app")
    String createApp(String appInfo, String userId);
}
