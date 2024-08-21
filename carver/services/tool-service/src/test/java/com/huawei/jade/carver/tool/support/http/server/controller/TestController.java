/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.carver.tool.support.http.server.controller;

import com.huawei.fit.http.annotation.PathVariable;
import com.huawei.fit.http.annotation.PostMapping;
import com.huawei.fit.http.annotation.RequestBean;
import com.huawei.fit.http.annotation.RequestBody;
import com.huawei.fit.http.annotation.RequestForm;
import com.huawei.fit.http.annotation.RequestMapping;
import com.huawei.fit.http.annotation.RequestQuery;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.annotation.Property;
import modelengine.fitframework.util.MapBuilder;

import java.util.List;
import java.util.Map;

/**
 * 测试 Http 提供的接口。
 *
 * @author 王攀博
 * @since 2024-06-17
 */
@Component
@RequestMapping(group = "Test")
public class TestController {
    /**
     * 用于测试验证 Http 服务端各个注解是否正常
     *
     * @param tourism 表示 {@link PathVariable} 类型的参数的 {@link String}。
     * @param person 表示 {@link RequestBean} 类型，内部成员变量为报文头的对象的 {@link Person}。
     * @param transportations 表示 {@link RequestQuery} 类型的参数的 {@link List}{@code <}{@link String}{@code >}。
     * @param weather 表示 {@link RequestBody} 类型的参数的 {@link Weather}。
     * @return 表示用于校验输入参数的集合的 {@link Map}{@code <}{@link String}{@code >}。
     */
    @PostMapping(path = "/test/travel/{type}")
    public Map<String, Object> travel(@PathVariable("type") @Property(description = "表示出行类型") String tourism,
            @RequestBean @Property(description = "表示出行人员信息") Person person,
            @RequestQuery("transportations") @Property(description = "表示需要的交通方式") List<String> transportations,
            @RequestBody @Property(description = "表示出行的天气") Weather weather) {
        return MapBuilder.<String, Object>get()
                .put("type", tourism)
                .put("name", person.getName())
                .put("age", person.getAge())
                .put("hobby", person.getHobby())
                .put("phoneNumber", person.getPhoneNumber())
                .put("transportations", transportations)
                .put("weather", weather)
                .build();
    }

    /**
     * 用于测试 Http Form 格式消息体.
     *
     * @param strings 表示列表类型的消息体内容 {@link List}{@code <}{@link String}{@code >}。
     * @param integer 表示整形类型的消息体内容 {@link Integer}。
     * @return 表示用于校验输入参数的集合的 {@link Map}{@code <}{@link String}{@code >}。
     */
    @PostMapping(path = "/test/form")
    public Map<String, Object> weather(
            @RequestForm(("strings")) @Property(description = "表示列表类型") List<String> strings,
            @RequestForm(("integer")) @Property(description = "表示") Integer integer) {
        return MapBuilder.<String, Object>get()
                .put("strings", strings)
                .put("integer", integer)
                .build();
    }
}
