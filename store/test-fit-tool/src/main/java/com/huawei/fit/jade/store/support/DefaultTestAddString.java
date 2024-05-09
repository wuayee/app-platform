/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jade.store.support;

import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Fitable;

/**
 * 表示提供的测试用的 FIT 默认实现。
 *
 * @author 王攀博
 * @since 2024-04-29
 */
@Component
public class DefaultTestAddString implements TestAddString {
    @Override
    @Fitable(id = "default-test-fit-impl")
    public String add(String p1, String p2) {
        return p1 + "*_*" + p2;
    }
}