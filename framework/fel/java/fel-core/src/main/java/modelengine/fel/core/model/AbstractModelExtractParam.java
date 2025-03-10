/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fel.core.model;

/**
 * 表示 {@link ModelExtractParam} 的抽象实现。
 *
 * @param <T> 表示数据的泛型。
 * @author 易文渊
 * @since 2024-12-23
 */
public abstract class AbstractModelExtractParam<T> implements ModelExtractParam<T> {
    private final T data;

    protected AbstractModelExtractParam(T data) {
        this.data = data;
    }

    @Override
    public T data() {
        return this.data;
    }
}