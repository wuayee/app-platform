/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package modelengine.fit.log;

/**
 * 表示日志级别的修改器。
 *
 * @author 季聿阶
 * @since 2023-12-24
 */
@FunctionalInterface
public interface LoggerLevelChanger {
    /**
     * 修改日志记录器的级别。
     *
     * @param pluginName 表示待修改的日志记录器所在插件的 {@link String}。
     * @param packageName 表示待修改日志记录器的包名称的 {@link String}。
     * @param name 表示待修改日志记录器的名字的 {@link String}。
     * @param level 表示修改后的日志级别的 {@link String}。
     */
    void changeLevel(String pluginName, String packageName, String name, String level);
}
