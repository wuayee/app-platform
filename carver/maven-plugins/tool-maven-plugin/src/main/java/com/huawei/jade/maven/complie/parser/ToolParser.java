/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.maven.complie.parser;

import com.huawei.jade.maven.complie.entity.ToolEntity;

import java.nio.file.Path;

/**
 * 工具解析接口。
 *
 * @author 杭潇
 * @since 2024-06-28
 */
public interface ToolParser {
    /**
     * 基于 class 文件路径将工具注解的方法解析成 {@link ToolEntity}。
     *
     * @param classFilePath 表示给定的 class 文件路径的 {@link Path}。
     * @return 表示解析完之后的 {@link ToolEntity}。
     */
    ToolEntity parseTool(Path classFilePath);
}
