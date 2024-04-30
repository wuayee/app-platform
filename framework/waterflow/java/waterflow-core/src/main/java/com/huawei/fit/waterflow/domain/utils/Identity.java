/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.domain.utils;

/**
 * ID生成器接口
 *
 * @author g00564732
 * @since 1.0
 */
@FunctionalInterface
public interface Identity {
    /**
     * getId
     *
     * @return String
     */
    String getId();
}
