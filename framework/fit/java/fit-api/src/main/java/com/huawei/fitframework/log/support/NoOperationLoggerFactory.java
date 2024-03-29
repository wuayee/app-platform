/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.log.support;

import com.huawei.fitframework.conf.Config;
import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.log.LoggerFactory;

/**
 * 表示 {@link LoggerFactory} 的忽略日志的实现。
 *
 * @author 季聿阶 j00559309
 * @since 2023-06-15
 */
public class NoOperationLoggerFactory implements LoggerFactory {
    /** 表示 {@link NoOperationLoggerFactory} 的单例。 */
    public static final NoOperationLoggerFactory INSTANCE = new NoOperationLoggerFactory();

    private NoOperationLoggerFactory() {}

    @Override
    public void initialize(Config config, ClassLoader frameworkClassLoader) {}

    @Override
    public Logger getLogger(Class<?> clazz) {
        return NoOperationLogger.INSTANCE;
    }

    @Override
    public Logger getLogger(String name) {
        return NoOperationLogger.INSTANCE;
    }

    @Override
    public void setGlobalLevel(Logger.Level level) {}

    @Override
    public void setLevels(String basePackage, Logger.Level level) {}
}
