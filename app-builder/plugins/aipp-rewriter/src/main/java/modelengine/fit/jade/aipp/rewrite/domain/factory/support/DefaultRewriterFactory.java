/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.rewrite.domain.factory.support;

import static modelengine.fitframework.inspection.Validation.notNull;

import modelengine.fit.jade.aipp.rewrite.domain.entity.RewriteStrategy;
import modelengine.fit.jade.aipp.rewrite.domain.entity.Rewriter;
import modelengine.fit.jade.aipp.rewrite.domain.factory.RewriterFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 表示 {@link RewriterFactory} 的默认实现。
 *
 * @author 易文渊
 * @since 2024-09-23
 */
public class DefaultRewriterFactory implements RewriterFactory {
    private final Map<RewriteStrategy, Rewriter> cache = new ConcurrentHashMap<>();

    @Override
    public Rewriter create(RewriteStrategy strategy) {
        Rewriter rewriter = this.cache.get(strategy);
        notNull(rewriter, "Failed to create rewrite strategy: {0}.", strategy);
        return rewriter;
    }

    @Override
    public void register(Rewriter rewriter) {
        this.cache.put(rewriter.strategy(), rewriter);
    }
}