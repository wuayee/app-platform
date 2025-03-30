/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.rewrite.domain.entity;

import modelengine.fel.core.pattern.Pattern;
import modelengine.fit.jade.aipp.rewrite.domain.vo.RewriteParam;

import java.util.List;

/**
 * 表示重写算子的接口定义。
 *
 * @author 易文渊
 * @since 2024-09-23
 */
public interface Rewriter extends Pattern<RewriteParam, List<String>> {
    /**
     * 获取重写策略。
     *
     * @return 表示重写策略的 {@link RewriteStrategy}。
     */
    RewriteStrategy strategy();
}