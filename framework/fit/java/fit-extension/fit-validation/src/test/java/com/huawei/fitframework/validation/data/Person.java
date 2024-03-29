/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.validation.data;

import com.huawei.fitframework.validation.constraints.NotBlank;
import com.huawei.fitframework.validation.constraints.NotEmpty;
import com.huawei.fitframework.validation.constraints.Range;
import com.huawei.fitframework.validation.group.NotPersonGroup;
import com.huawei.fitframework.validation.group.PersonGroup;

/**
 * 表示人类的数据类。
 *
 * @author 邬涨财 w00575064
 * @since 2023-05-19
 */
public class Person {
    @Range(min = 0, max = 1, message = "嘴巴数量范围只能在0和1！")
    private final int mouth;

    @Range(min = 0, max = 2, message = "眼睛数量范围只能在0和2！")
    private final int eyes;

    @NotBlank(message = "姓名不能为空！")
    private final String name;

    @NotEmpty(message = "性别不能为空！")
    private final String sex;

    @Range(min = 0, max = 150, message = "人类年龄要在0~150之内", groups = {PersonGroup.class})
    private final int personAge;

    @Range(min = 151, max = 200, message = "非人类年龄在151~200之内", groups = {NotPersonGroup.class})
    private final int notPersonAge;

    public Person(int mouth, int eyes, String name, String sex, int personAge, int notPersonAge) {
        this.mouth = mouth;
        this.eyes = eyes;
        this.name = name;
        this.sex = sex;
        this.personAge = personAge;
        this.notPersonAge = notPersonAge;
    }
}
