/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.waterflow.domain.stream.reactive;

import modelengine.fit.waterflow.domain.context.FlowContext;
import modelengine.fit.waterflow.domain.stream.operators.Operators;

import java.util.List;

/**
 * publisher与subscriber之间的连接器
 * 其中包含了两者传递的一些要求信息：处理多少数据，如何过滤数据
 * subscriber来不及处理的数据或者block的数据在subscription中缓存
 *
 * @param <I> 接收的数据类型
 * @since 1.0
 */
public interface Subscription<I> extends StreamIdentity {
    /**
     * cache
     *
     * @param contexts contexts
     */
    void cache(List<FlowContext<I>> contexts);

    /**
     * getWhether
     *
     * @return Whether<I>
     */
    Operators.Whether<I> getWhether();

    /**
     * getTo
     *
     * @return Subscriber<O, R>
     */
    <R> Subscriber<I, R> getTo();
}
