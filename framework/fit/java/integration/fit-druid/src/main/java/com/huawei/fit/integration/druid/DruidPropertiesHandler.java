/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fit.integration.druid;

import java.util.Properties;

/**
 * 为Druid的配置属性集提供处理程序。
 *
 * @author 梁济时 l00815032
 * @since 2022-08-02
 */
public interface DruidPropertiesHandler {
    /**
     * 处理Druid的配置属性集。
     *
     * @param properties 表示待处理的配置属性集的 {@link Properties}。
     */
    void handleDruidProperties(Properties properties);
}
