/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.extract.command;

/**
 * 信息提取命令处理器。
 *
 * @author 易文渊
 * @since 2024-10-24
 */
public interface ExtractCommandHandler {
    /**
     * 处理信息提取命令。
     *
     * @param command 表示信息提取命令的 {@link ContentExtractCommand}。
     * @return 表示提取液结果的 {@link Object}。
     */
    Object handle(ContentExtractCommand command);
}