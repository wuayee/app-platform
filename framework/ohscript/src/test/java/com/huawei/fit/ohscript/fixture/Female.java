/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.ohscript.fixture;

import java.util.List;

/**
 * Female
 * 测试数据类
 *
 * @since 1.0
 */
public class Female implements Human {
    private Integer age = 200;

    private String name = "Elsa";

    private Human friend = null;

    @Override
    public Integer getAge() {
        return this.age;
    }

    @Override
    public String getName(String firstName) {
        return name + " " + firstName;
    }

    @Override
    public List<Human> makeFriends(List<Human> friends) {
        return null;
    }

    @Override
    public Human getFriend() {
        return this.friend;
    }
}
