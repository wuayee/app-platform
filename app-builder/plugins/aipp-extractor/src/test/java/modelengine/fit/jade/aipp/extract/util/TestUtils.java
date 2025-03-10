/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.extract.util;

import modelengine.fit.jade.aipp.extract.command.ContentExtractCommand;
import modelengine.fit.jade.aipp.memory.AippMemoryConfig;

/**
 * 测试使用工具类。
 *
 * @author 何嘉斌
 * @since 2024-10-28
 */
public class TestUtils {
    /**
     * 获取提取问题命令。
     *
     * @return 表示提取问题命令的 {@link ContentExtractCommand}。
     */
    public static ContentExtractCommand getExtractCommand() {
        ContentExtractCommand command = new ContentExtractCommand();
        command.setText("text");
        command.setDesc("desc");
        command.setOutputSchema("{}");
        command.setModel("model");
        command.setTemperature(0.1);
        AippMemoryConfig config = new AippMemoryConfig();
        config.setWindowAlg("buffer_window");
        config.setSerializeAlg("full");
        config.setProperty(3);
        command.setMemoryConfig(config);
        return command;
    }
}