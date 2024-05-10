/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.health;

import com.huawei.fitframework.annotation.Component;

/**
 * 表示插件2的实现。
 *
 * @author 季聿阶 j00559309
 * @since 2023-06-06
 */
@Component
public class HelloServiceImpl implements HelloService {
    @Override
    public String hi(String name) {
        return "Hi, " + name + ". This is jober.";
    }
}
