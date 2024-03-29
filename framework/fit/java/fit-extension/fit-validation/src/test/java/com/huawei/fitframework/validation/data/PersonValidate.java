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
    public void validate1(@Validated Person person) {
    }

    public void validate2(@Range(min = 0, max = 1, message = "耳朵数量范围只能在0和1！") int ears, int mouth) {
    }

    public void validate3(@Validated(PersonGroup.class) Person person) {
    }
}
