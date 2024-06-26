/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.carver.tool.support;

import com.huawei.fit.http.annotation.PathVariable;
import com.huawei.fit.http.annotation.PostMapping;
import com.huawei.fit.http.annotation.RequestBean;
import com.huawei.fit.http.annotation.RequestBody;
import com.huawei.fit.http.annotation.RequestForm;
import com.huawei.fit.http.annotation.RequestHeader;
import com.huawei.fit.http.annotation.RequestMapping;
import com.huawei.fit.http.annotation.RequestQuery;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Property;
import com.huawei.fitframework.util.MapBuilder;
import com.huawei.jade.carver.tool.support.entity.Address;
import com.huawei.jade.carver.tool.support.entity.Education;

import java.util.List;
import java.util.Map;

/**
 * 表示测试用的模拟 HTTP 服务端。
 *
 * @author 何天放 h00679269
 * @since 2024-06-15
 */
@Component
@RequestMapping(group = "Test")
public class HttpClientTestController {
    /**
     * 表示返回值为 {@link Map} 的 HTTP 服务端接口。
     *
     * @param name 表示姓名的 {@link String}。
     * @param age 表示年龄的 {@link Integer}。
     * @param address 表示地址的 {@link Address}
     * @param education 表示教育信息的 {@link Education}。
     * @param phoneNumbers 表示电话号码列表的 {@link List}{@code <}{@link String}{@code >}。
     * @return 表示结果的 {@link Map}{@code <}{@link String}{@code ,}{@link Object}{@code >}。
     */
    @PostMapping(path = "/test/return/map/{name}")
    public Map<String, Object> returnMap(@PathVariable("name") @Property(description = "表示姓名") String name,
            @RequestHeader("age") @Property(description = "表示年龄") Integer age,
            @RequestBody @Property(description = "表示地址") Address address,
            @RequestBean @Property(description = "表示教育经历") Education education,
            @RequestQuery("phoneNumbers") @Property(description = "表示电话列表") List<String> phoneNumbers) {
        return MapBuilder.<String, Object>get()
                .put("name", name)
                .put("age", age)
                .put("address", address)
                .put("education", education)
                .put("phoneNumbers", phoneNumbers)
                .build();
    }

    /**
     * 表示返回值为 {@link String} 的 HTTP 服务端接口。
     *
     * @param values 表示字符串列表的 {@link List}{@code <}{@link String}{@code >}。
     * @return 表示结果的 {@link String}。
     */
    @PostMapping(path = "/test/return/string")
    public String returnString(@RequestForm("values") @Property(description = "表示字符串列表") List<String> values) {
        return String.join(",", values);
    }

    /**
     * 表示返回值为 {@link Integer} 的 HTTP 服务端接口。
     *
     * @param values 表示整形数字列表的 {@link List}{@code <}{@link Integer}{@code >}。
     * @return 表示结果的 {@link Integer}。
     */
    @PostMapping(path = "/test/return/integer")
    public Integer returnInteger(@RequestQuery("values") @Property(description = "表示数字列表") List<Integer> values) {
        return values.stream().mapToInt(Integer::intValue).sum();
    }

    /**
     * 表示返回值为 {@code null} 的 HTTP 服务端接口。
     */
    @PostMapping(path = "/test/return/void")
    public void returnVoid() {}
}
