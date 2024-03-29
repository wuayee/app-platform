/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2024. All rights reserved.
 */

package com.huawei.fit.serialization.test.person;

import lombok.Data;

import java.util.List;

/**
 * 为单元测试提供人的信息定义。
 *
 * @author 梁济时 l00815032
 * @since 2020-11-23
 */
@Data
public class Person {
    private PersonName name;
    private List<String> inventions;
}
