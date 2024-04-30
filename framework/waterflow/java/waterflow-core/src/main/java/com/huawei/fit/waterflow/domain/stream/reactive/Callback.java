/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.waterflow.domain.stream.reactive;

import java.util.List;

/**
 * 用于流结束后的返回对象处理
 *
 * @param <O> 处理的对象类型
 * @since 1.0
 */
public interface Callback<O> {
    /**
     * getAll
     *
     * @return List<O>
     */
    List<O> getAll();

    /**
     * get
     *
     * @return O
     */
    O get();
}
