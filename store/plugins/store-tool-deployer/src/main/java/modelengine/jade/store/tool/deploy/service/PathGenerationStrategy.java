/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.store.tool.deploy.service;

import java.nio.file.Path;
import java.util.Map;

/**
 * 表示插件路径生成策略。
 *
 * @author 杭潇
 * @since 2025-01-13
 */
public interface PathGenerationStrategy {
    /** 表示插件的持久化目录。 **/
    String PERSISTENT_PATH = "/opt/fit/tools";

    /**
     * 根据扩展信息生成路径值。
     *
     * @param extension 表示给定的扩展信息的 {@link Map}{@code <}{@link String}{@code , }{@link Object}{@code >}。
     * @return 表示生成路径的 {@link Path}。
     */
    Path generatePath(Map<String, Object> extension);
}
