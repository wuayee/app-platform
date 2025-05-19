/*---------------------------------------------------------------------------------------------
 *  Copyright (c) 2025 Huawei Technologies Co., Ltd. All rights reserved.
 *  This file is a part of the ModelEngine Project.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/

package modelengine.jade.maven.compile.parser.weather.impl;

import modelengine.jade.carver.tool.annotation.Attribute;
import modelengine.jade.carver.tool.annotation.Group;
import modelengine.jade.carver.tool.annotation.ToolMethod;
import modelengine.jade.maven.compile.parser.weather.Rain;

import modelengine.fitframework.annotation.Fitable;

import java.util.Date;

/**
 * 添加测试用的工具的实现。
 *
 * @author 曹嘉美
 * @since 2024-10-26
 */
@Group(name = "implGroup_weather_Rain_Unicom")
public class UnicomRainImpl implements Rain {
    private static final String FITABLE_ID = "weather_rain_unicom";

    @Fitable(FITABLE_ID)
    @ToolMethod(name = "unicom_rain_today", description = "使用联通提供的今日下雨信息", extensions = {
            @Attribute(key = "tags", value = "FIT"), @Attribute(key = "tags", value = "TEST")
    })
    @Override
    public String today(String location, Date date) {
        return null;
    }

    @Fitable(FITABLE_ID)
    @ToolMethod(name = "unicom_rain_tomorrow", description = "使用联通提供的明日下雨信息", extensions = {
            @Attribute(key = "tags", value = "FIT"), @Attribute(key = "tags", value = "TEST")
    })
    @Override
    public String tomorrow(String location, Date date) {
        return null;
    }
}
