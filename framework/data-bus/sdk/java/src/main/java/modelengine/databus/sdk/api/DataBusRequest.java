/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.databus.sdk.api;

/**
 * DataBus 请求共有接口。
 *
 * @author 王成
 * @since 2024-05-27
 */
public interface DataBusRequest {
    /**
     * 返回与本次 IO 相关的内存用户键。
     *
     * @return 表示与本次 IO 请求相关的内存 {@code String}。
     */
    String userKey();
}
