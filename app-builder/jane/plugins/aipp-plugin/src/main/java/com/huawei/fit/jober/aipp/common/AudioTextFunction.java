/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.common;

import java.io.IOException;

/**
 * 音频提取文本接口
 *
 * @author l00611472
 * @since 2024/1/19
 */
@FunctionalInterface
public interface AudioTextFunction<T1, R> {
    R apply(T1 t1) throws IOException;
}
