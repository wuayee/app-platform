/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.http.call.command;

/**
 * {@link HttpCallCommand} 命令执行器。
 *
 * @author 张越
 * @since 2024-11-22
 */
public interface HttpCallCommandHandler {
    /**
     * 执行 {@link HttpCallCommand} 命令.
     *
     * @param command {@link HttpCallCommand} 对象.
     * @return {@link HttpCallResult} 对象.
     */
    HttpCallResult handle(HttpCallCommand command);
}
