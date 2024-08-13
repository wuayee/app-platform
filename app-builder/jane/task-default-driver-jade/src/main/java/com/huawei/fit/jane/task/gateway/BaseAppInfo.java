/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jane.task.gateway;

import com.huawei.fitframework.annotation.Alias;
import com.huawei.fitframework.annotation.Component;

/**
 * 为 {@link AppInfo} 提供a3000的默认实现。
 *
 * @author 孙怡菲
 * @since 2023/11/28
 */
@Alias("Default-Impl")
@Component
public class BaseAppInfo implements AppInfo {
    private final String id;

    private final String key;

    private final String token;

    public BaseAppInfo() {
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
