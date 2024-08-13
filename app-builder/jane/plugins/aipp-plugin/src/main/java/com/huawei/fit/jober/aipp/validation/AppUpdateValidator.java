/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.validation;

/**
 * App 更新校验器
 *
 * @author 邬涨财
 * @since 2024-06-20
 */
public interface AppUpdateValidator {
    /**
     * 校验一个app是否可以更新
     *
     * @param id 待验证的appId
     */
    void validate(String id);
}
