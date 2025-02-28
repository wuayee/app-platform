/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.classify.question.command;

/**
 * 问题分类理器。
 *
 * @author 张越
 * @since 2024-11-18
 */
public interface ClassifyQuestionCommandHandler {
    /**
     * 处理问题分类命令。
     *
     * @param command 表示问题重写命令的 {@link ClassifyQuestionCommand}。
     * @return 问题分类结果。
     */
    String handle(ClassifyQuestionCommand command);
}