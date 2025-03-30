/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.store.tool.deploy.service.support;

import modelengine.jade.store.tool.deploy.service.PathGenerationStrategy;

import java.nio.file.Path;
import java.util.Map;

/**
 * 表示未定义插件类型路径生成策略。
 *
 * @author 杭潇
 * @since 2025-01-13
 */
class UnsupportedLanguageStrategy implements PathGenerationStrategy {
    private final String type;

    UnsupportedLanguageStrategy(String type) {
        this.type = type;
    }

    @Override
    public Path generatePath(Map<String, Object> extension) {
        throw new IllegalArgumentException("Unsupported language type: " + type);
    }
}
