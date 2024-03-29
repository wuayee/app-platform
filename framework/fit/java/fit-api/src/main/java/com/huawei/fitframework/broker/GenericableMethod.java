/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2024. All rights reserved.
 */

package com.huawei.fitframework.broker;

import com.huawei.fitframework.inspection.Nullable;

import java.lang.reflect.Method;

/**
 * 表示服务方法。
 *
 * @author 季聿阶 j00559309
 * @since 2023-03-29
 */
public interface GenericableMethod {
    /**
     * 获取服务的方法。
     *
     * @return 表示服务方法的 {@link Method}。
     */
    @Nullable
    Method method();
}
