/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.fitframework.validation.data;

import modelengine.fit.http.annotation.PostMapping;
import modelengine.fit.http.annotation.RequestBody;
import modelengine.fit.http.annotation.RequestMapping;
import modelengine.fitframework.annotation.Component;
import modelengine.fitframework.validation.Validated;
import modelengine.fitframework.validation.group.PersonGroup;

/**
 * 表示评估注解验证数据接口集。
 *
 * @author 吕博文
 * @since 2024-08-15
 */
@Component
@RequestMapping(path = "/validation", group = "评估注解验证数据接口")
public class ValidationDataController {
    /**
     * Person 类默认分组注解验证。
     *
     * @param person 表示注解验证类 {@link Person}。
     */
    @PostMapping(path = "/person/default", description = "验证Person类默认分组注解")
    public void validatePersonDefaultGroup(@RequestBody @Validated Person person) {}

    /**
     * Person 类特定分组注解验证。
     *
     * @param person 表示注解验证类 {@link Person}。
     */
    @PostMapping(path = "/person/personGroup", description = "验证Person类特定分组注解")
    public void validatePersonPersonGroup(@RequestBody @Validated(PersonGroup.class) Person person) {}

    /**
     * Product 类默认分组注解验证。
     *
     * @param product 表示注解验证类 {@link Product}。
     */
    @PostMapping(path = "/product/default", description = "验证Product类默认分组注解")
    public void validateProductDefaultGroup(@RequestBody @Validated Product product) {}
}