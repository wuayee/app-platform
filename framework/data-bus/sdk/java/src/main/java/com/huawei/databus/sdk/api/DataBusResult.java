/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.databus.sdk.api;

import com.huawei.databus.sdk.memory.SharedMemory;

/**
 * 为 DataBus 请求提供结果。
 *
 * @author 王成 w00863339
 * @since 2024-03-17
 */
public interface DataBusResult {
    /**
     * 返回与本次 IO 相关的内存
     * <p>仅当 {@link #isSuccess()} 为 {@code true} 时有效。</p>
     *
     * @return 表示与本次 IO 请求相关的内存 {@link SharedMemory}
     */
    SharedMemory sharedMemory();

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
}
