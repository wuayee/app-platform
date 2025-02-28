/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.classify.question;

import modelengine.fit.jade.aipp.memory.AippChatRound;
import modelengine.fit.jade.aipp.memory.AippMemoryConfig;
import modelengine.fitframework.annotation.Genericable;

import java.util.List;

/**
 * 问题分类算子服务。
 *
 * @author 张越
 * @since 2024-11-18
 */
public interface ClassifyQuestionService {
    /**
     * 问题分类。
     *
     * @param classifyQuestionParam 表示问题分类参数的 {@link ClassifyQuestionParam}。
     * @param memoryConfig 表示历史理解配置的 {@link AippMemoryConfig}。
     * @param histories 表示历史记录的 {@link List}{@code <}{@link AippChatRound}{@code >}。
     * @return 表示问题分类后的分类id {@link String}。
     */
    @Genericable("modelengine.jober.aipp.classify.question")
    String classifyQuestion(ClassifyQuestionParam classifyQuestionParam, AippMemoryConfig memoryConfig,
            List<AippChatRound> histories);
}
