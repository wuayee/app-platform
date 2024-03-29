/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.log.console;

import com.huawei.fitframework.conf.Config;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.log.LoggerFactory;
import com.huawei.fitframework.util.ObjectUtils;
import com.huawei.fitframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 表示 {@link LoggerFactory} 的控制台实现。
 *
 * @author 季聿阶 j00559309
 * @since 2023-06-13
 */
public class ConsoleLoggerFactory implements LoggerFactory {
    private Logger.Level level = Logger.Level.INFO;
    private final Map<String, Logger> loggers = new ConcurrentHashMap<>();

    @Override
    public void initialize(Config config, ClassLoader frameworkClassLoader) {
        String initialLevel = config.get("logging.level", String.class);
        if (StringUtils.isNotBlank(initialLevel)) {
            this.level = Logger.Level.from(initialLevel);
        }
    }

    @Override
    public Logger getLogger(Class<?> clazz) {
        return this.getLogger(clazz == null ? "ROOT" : clazz.getName());
    }

    @Override
    public Logger getLogger(String name) {
        String actualName = StringUtils.isBlank(name) ? "ROOT" : name;
        return this.loggers.computeIfAbsent(actualName, key -> new ConsoleLogger(key, this.level));
    }

    @Override
    public void setGlobalLevel(Logger.Level level) {
        this.level = ObjectUtils.nullIf(level, Logger.Level.NONE);
        for (Logger logger : this.loggers.values()) {
            logger.setLevel(this.level);
        }
    }

    @Override
    public void setLevels(String basePackage, Logger.Level level) {
        Logger.Level actualLevel = ObjectUtils.nullIf(level, Logger.Level.NONE);
        for (Logger logger : this.loggers.values()) {
            if (StringUtils.equalsIgnoreCase(logger.name(), basePackage)
                    || StringUtils.startsWithIgnoreCase(logger.name(), basePackage + ".")) {
                logger.setLevel(actualLevel);
            }
        }
    }
}
