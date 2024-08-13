/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.log;

import com.huawei.fitframework.conf.Config;

/**
 * 统一日志接口的工厂。
 *
 * @author 季聿阶
 * @since 2023-06-13
 */
public interface LoggerFactory {
    /**
     * 根据应用配置信息初始化日志系统。
     *
     * @param config 表示应用配置信息的 {@link Config}。
     * @param frameworkClassLoader 表示 FIT 框架的类加载器的 {@link ClassLoader}。
     */
    void initialize(Config config, ClassLoader frameworkClassLoader);

    /**
     * 获取指定类型的日志记录器。
     *
     * @param clazz 表示指定类型的 {@link Class}{@code <?>}。
     * @return 表示指定类型的日志记录器的 {@link Logger}。
     */
    Logger getLogger(Class<?> clazz);

    /**
     * 获取指定名字的日志记录器。
     *
     * @param name 表示指定名字的 {@link String}。
     * @return 表示指定名字的日志记录器的 {@link Logger}。
     */
    Logger getLogger(String name);

    /**
     * 设置全局的日志级别。
     * <p>将内存中所有日志记录器的日志级别全部调整为指定值，包括之后新建的日志记录器，也会使用该值作为默认级别。</p>
     *
     * @param level 表示待设置的全局日志级别的 {@link Logger.Level}。
     */
    void setGlobalLevel(Logger.Level level);

    /**
     * 将指定包路径及其子包路径的日志记录器的级别调整为指定值。
     * <p>例如：{@code basePackage} 为 {@code 'com.huawei.fit'} 时，{@code 'com.huawei.fit'} 和
     * {@code 'com.huawei.fit.sample'} 包下的所有日志记录器的级别会受影响，但是 {@code 'com.huawei.fitframework'}
     * 包下的所有日志记录器的级别不受影响。</p>
     *
     * @param basePackage 表示指定基础包路径的 {@link String}。
     * @param level 表示待设置的日志级别的 {@link Logger.Level}。
     */
    void setLevels(String basePackage, Logger.Level level);
}
