/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.extract;

import modelengine.fit.jade.aipp.model.dto.ModelAccessInfo;

import lombok.Data;

/**
 * 文本提取参数定义。
 *
 * @author 易文渊
 * @since 2024-10-24
 */
@Data
public class ContentExtractParam {
    /**
     * 需要提取的文本。
     */
    private String text;

    /**
     * 提取要求描述。
     */
    private String desc;

    /**
     * 输出结构描述，必须是一个合法的 json Schema。
     */
    private String outputSchema;

    /**
     * 模型访问信息。
     */
    private ModelAccessInfo accessInfo;

    /**
     * 模型温度。
     */
    private Double temperature;
}