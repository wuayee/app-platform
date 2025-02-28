/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.rewrite;

import modelengine.fit.jade.aipp.model.dto.ModelAccessInfo;

import lombok.Data;

import java.util.Map;

/**
 * 问题重写参数。
 *
 * @author 易文渊
 * @since 2024-09-28
 */
@Data
public class RewriteQueryParam {
    /**
     * 重写策略：
     * <ul>
     *     <li>builtin：内置策略；</li>
     *     <li>custom：自定义策略。</li>
     * </ul>
     */
    private String strategy;

    /**
     * 输入参数。
     */
    private Map<String, String> args;

    /**
     * 输入模板：可能为问题背景或用户提示词。
     */
    private String template;

    /**
     * 模型访问信息。
     */
    private ModelAccessInfo accessInfo;

    /**
     * 模型温度
     */
    private Double temperature;
}