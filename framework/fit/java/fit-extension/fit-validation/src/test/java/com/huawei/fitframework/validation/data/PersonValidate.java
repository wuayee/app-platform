/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.validation.data;

import com.huawei.fitframework.validation.Validated;
import com.huawei.fitframework.validation.constraints.Range;
import com.huawei.fitframework.validation.group.PersonGroup;

/**
 * 表示人类的校验器。
 *
 * @author 邬涨财 w00575064
 * @since 2023-05-16
 */
@Validated
public class PersonValidate {
    /**
     * Person 类的校验方法一。
     *
     * @param person 表示校验的 Person 对象 {@link Person}。
     */
    public void validate1(@Validated Person person) {}

    /**
     * Person 类的校验方法二。
     *
     * @param ears 表示输入的耳朵数量 {@link int}。
     * @param mouth 表示输入的嘴巴数量 {@link int}。
     */
    public void validate2(@Range(min = 0, max = 1, message = "耳朵数量范围只能在0和1！") int ears, int mouth) {}

    /**
     * Person 类的校验方法三。
     *
     * @param person 表示校验的 Person 对象 {@link Person}。
     */
    public void validate3(@Validated(PersonGroup.class) Person person) {}
}
