/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.fit.jade.aipp.rewrite.util;

import modelengine.fit.jade.aipp.memory.AippMemoryConfig;
import modelengine.fit.jade.aipp.rewrite.command.RewriteQueryCommand;
import modelengine.fit.jade.aipp.rewrite.domain.entity.RewriteStrategy;
import modelengine.fitframework.util.MapBuilder;

/**
 * 测试使用工具类。
 *
 * @author 易文渊
 * @since 2024-09-27
 */
public class TestUtils {
    /**
     * 获取重写问题命令。
     *
     * @return 表示重写问题命令的 {@link RewriteQueryCommand}。
     */
    public static RewriteQueryCommand getQueryCommand() {
        RewriteQueryCommand command = new RewriteQueryCommand();
        command.setStrategy(RewriteStrategy.BUILTIN);
        command.setArgs(MapBuilder.<String, String>get().put(Constant.QUERY_KEY, "sky").build());
        command.setTemplate("hello");
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