/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fel.core.model;

/**
 * 表示额外参数的接口定义。
 *
 * @param <T> 表示数据的泛型。
 * @author 易文渊
 * @since 2024-12-23
 */
public interface ModelExtractParam<T> {
    /**
     * 获取携带的数据。
     *
     * @return 表示携带数据的 {@link T}。
     */
    T data();
}