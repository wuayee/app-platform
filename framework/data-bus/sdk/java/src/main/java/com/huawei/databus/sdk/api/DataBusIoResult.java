/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.databus.sdk.api;

import com.huawei.databus.sdk.memory.SharedMemory;

/**
 * 为 DataBus IO 相关请求提供结果。
 *
 * @author 王成 w00863339
 * @since 2024-03-17
 */
public interface DataBusIoResult extends DataBusResult {
    /**
     * 返回与本次 IO 相关的内存
     * <p>仅当 {@link #isSuccess()} 为 {@code true} 时有效。</p>
     *
     * @return 表示与本次 IO 请求相关的内存 {@link SharedMemory}
     */
    SharedMemory sharedMemory();
}
