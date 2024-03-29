/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.validation.data;

import com.huawei.fitframework.validation.Validated;
import com.huawei.fitframework.validation.constraints.Range;
import com.huawei.fitframework.validation.group.StudentGroup;
import com.huawei.fitframework.validation.group.TeacherGroup;

/**
 * 学生的校验类。
 *
 * @author 邬涨财 w00575064
 * @since 2023-05-19
 */
@Validated(StudentGroup.class)
public class StudentValidate {
    public void validateStudent(
            @Range(min = 7, max = 20, message = "范围要在7~20之内", groups = {StudentGroup.class}) int age) {
    }

    public void validateTeacher(
            @Range(min = 30, max = 70, message = "范围要在30~70之内", groups = {TeacherGroup.class}) int age) {
    }
}
