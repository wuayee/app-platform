/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.flowsengine.domain.flows.streams.callbacks;

import modelengine.fit.waterflow.flowsengine.domain.flows.streams.FitStream;

import java.util.List;

/**
 * ToCallback表示对 {@link FitStream.Callback} 的实现类
 *
 * @author 李哲峰
 * @since 2023-12-12
 */
public class ToCallback<O> implements FitStream.Callback<O> {
    private final List<O> products;

    public ToCallback(List<O> products) {
        this.products = products;
    }

    public List<O> getAll() {
        return this.products;
    }

    /**
     * get
     *
     * @return O
     */
    public O get() {
        return this.products.get(0);
    }
}
