/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.carver.tool.support.http.server.controller;

import modelengine.fit.http.annotation.RequestHeader;
import modelengine.fitframework.annotation.Property;

/**
 * 测试 Http 提供的天气的相关信息。
 *
 * @author 王攀博
 * @since 2024-06-17
 */
public class Weather {
    @Property(description = "表示天气等级", example = "light")
    @RequestHeader("level")
    private Integer level;
    @Property(description = "表示天气情况", example = "raining")
    @RequestHeader("weather")
    private String weather;

    public Integer getLevel() {
        return this.level;
    }

    public void setAge(Integer level) {
        this.level = level;
    }

    public String getWeather() {
        return this.weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }
}
