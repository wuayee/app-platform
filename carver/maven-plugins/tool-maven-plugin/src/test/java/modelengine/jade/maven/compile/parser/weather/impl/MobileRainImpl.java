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
import modelengine.fitframework.annotation.Property;

import java.util.Date;

/**
 * 添加测试用的工具的实现。
 *
 * @author 曹嘉美
 * @since 2024-10-26
 */
@Group(name = "implGroup_weather_Rain_Mobile", extensions = {
        @Attribute(key = "owner", value = "测试"), @Attribute(key = "language", value = "english")
})
public class MobileRainImpl implements Rain {
    private static final String FITABLE_ID = "weather_rain_mobile";

    @Fitable(FITABLE_ID)
    @ToolMethod(name = "mobile_rain_today", description = "使用移动提供的今日下雨信息", extensions = {
            @Attribute(key = "tags", value = "FIT"), @Attribute(key = "tags", value = "TEST")
    })
    @Property(description = "获取今日下雨信息的结果")
    @Override
    public String today(String location, Date date) {
        return null;
    }

    @Fitable(FITABLE_ID)
    @ToolMethod(name = "mobile_rain_tomorrow", description = "使用移动提供的明日下雨信息", extensions = {
            @Attribute(key = "tags", value = "FIT"), @Attribute(key = "tags", value = "TEST")
    })
    @Property(description = "获取明日下雨信息的结果")
    @Override
    public String tomorrow(String location, Date date) {
        return null;
    }
}
