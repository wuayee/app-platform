/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.rewrite.domain.factory;

import modelengine.fit.jade.aipp.rewrite.domain.entity.RewriteStrategy;
import modelengine.fit.jade.aipp.rewrite.domain.entity.Rewriter;

/**
 * 表示 {@link Rewriter} 的工厂定义。
 *
 * @author 易文渊
 * @since 2024-09-23
 */
public interface RewriterFactory {
    /**
     * 根据策略创建重写算子。
     *
     * @param strategy 表示重写策略的 {@link RewriteStrategy}。
     * @return 表示重写算子的 {@link Rewriter}。
     */
    Rewriter create(RewriteStrategy strategy);

    /**
     * 注册一个重写算子。
     *
     * @param rewriter 表示重写算子的 {@link Rewriter}。
     */
    void register(Rewriter rewriter);
}