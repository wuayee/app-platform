/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.databus.sdk.api;

import java.util.Optional;

/**
 * 为 DataBus 请求提供结果。
 *
 * @author 王成 w00863339
 * @since 2024-03-17
 */
public interface DataBusResult {
    /**
     * 获取一个布尔值，该值指示相关请求是否成功。
     *
     * @return 若请求得到满足，则为 {@code true}，否则为 {@code false}。
     */
    boolean isSuccess();

    /**
     * 获取此请求的错误代码字符串。
     * <p>当 {@link #isSuccess()} 为 {@code true} 时，错误码恒为 ErrorType.None。</p>
     *
     * @return 表示错误代码的 {@code byte}。
     */
    byte errorType();

    /**
     * 获取此请求的原始 Java 异常。
     * <p>当 {@link #isSuccess()} 为 {@code true} 时，为 Optional.empty() </p>
     *
     * @return 表示错误代码的 {@code byte}。
     */
    Optional<Throwable> cause();
}
