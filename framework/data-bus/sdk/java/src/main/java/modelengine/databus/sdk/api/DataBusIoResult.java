/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.databus.sdk.api;

import modelengine.databus.sdk.memory.SharedMemory;

/**
 * 为 DataBus IO 相关请求提供结果。
 *
 * @author 王成
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
