/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.classify.question;

import modelengine.fit.jade.aipp.model.dto.ModelAccessInfo;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 问题分类参数。
 *
 * @author 张越
 * @since 2024-11-18
 */
@Data
public class ClassifyQuestionParam {
    /**
     * 模型访问信息。
     */
    private ModelAccessInfo accessInfo;

    /**
     * 模型温度
     */
    private Double temperature;

    /**
     * 用户提示词。
     */
    private String template;

    /**
     * 输入参数。
     */
    private Map<String, String> args;

    /**
     * 问题类型分类。
     */
    private List<QuestionType> questionTypeList;
}
