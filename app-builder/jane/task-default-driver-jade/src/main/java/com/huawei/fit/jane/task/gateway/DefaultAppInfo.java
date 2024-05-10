/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jane.task.gateway;

import com.huawei.fitframework.annotation.Alias;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Value;

/**
 * 为 {@link AppInfo} 提供默认实现。
 *
 * @author 梁济时 l00815032
 * @since 2023-11-17
 */
@Alias("Jade-Impl")
@Component
public class DefaultAppInfo implements AppInfo {
    private final String id;
    private final String key;
    private final String token;

    public DefaultAppInfo() {
        this.id = "jade";
        this.key = "jade";
        this.token = "jade";
    }

    @Override
    public String id() {
        return this.id;
    }

    @Override
    public String key() {
        return this.key;
    }

    @Override
    public String token() {
        return this.token;
    }

    @Override
    public String toString() {
        return this.id;
    }
}
