/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.rewrite.command;

import java.util.List;

/**
 * 重写命令处理器。
 *
 * @author 易文渊
 * @since 2024-09-24
 */
public interface RewriteCommandHandler {
    /**
     * 处理问题重写命令。
     *
     * @param command 表示问题重写命令的 {@link RewriteQueryCommand}。
     * @return 表示重写结果的 {@link List}{@code <}{@link String}{@code >}。
     */
    List<String> handle(RewriteQueryCommand command);
}