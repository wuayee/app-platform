/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.health;

import com.huawei.fit.http.annotation.GetMapping;
import com.huawei.fit.http.annotation.RequestMapping;
import com.huawei.fitframework.annotation.Component;

/**
 * 表示测试入口。
 *
 * @author 季聿阶 j00559309
 * @since 2023-06-06
 */
@Component
@RequestMapping(path = "/health")
public class HealthController {
    @GetMapping(summary = "健康检查接口")
    public String health() {
        return "Hi, This is jober.";
    }
}
