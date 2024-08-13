/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.flowsengine.domain.flows.streams;

/**
 * ID生成器接口
 *
 * @author 高诗意
 * @since 2023/08/14
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
