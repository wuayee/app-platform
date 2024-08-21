/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.log.support;

import com.huawei.fit.log.LoggerLevelChanger;
import modelengine.fitframework.log.Logger;
import modelengine.fitframework.log.Loggers;
import modelengine.fitframework.util.StringUtils;

/**
 * 表示 {@link LoggerLevelChanger} 的 FIT 框架的默认实现。
 *
 * @author 季聿阶
 * @since 2023-12-24
 */
public class FitChanger implements LoggerLevelChanger {
    /** 表示 {@link FitChanger} 单例。 */
    public static final FitChanger INSTANCE = new FitChanger();

    @Override
    public void changeLevel(String pluginName, String packageName, String name, String level) {
        Logger.Level loggerLevel = Logger.Level.from(level);
        if (StringUtils.isNotBlank(name)) {
            Loggers.getFactory().getLogger(name).setLevel(loggerLevel);
        } else {
            Loggers.getFactory().setLevels(packageName, loggerLevel);
        }
    }
}
