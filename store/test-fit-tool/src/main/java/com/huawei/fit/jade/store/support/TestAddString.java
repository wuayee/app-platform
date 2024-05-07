/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jade.store.support;

import com.huawei.fitframework.annotation.Genericable;

/**
 * 表示提供的测试用的 FIT 接口。
 *
 * @author 王攀博
 * @since 2024-04-29
 */
public interface TestAddString {
    /**
     * 表示一个字符串相加的接口。
     *
     * @param p1 表示第一个相加的入参的 {@link String}。
     * @param p2 表示第二个相加的入参的 {@link String}。
     * @return 表示两个字符串相加的结果的 {@link String}。
     */
    @Genericable(id = "com.huawei.jade.store.tool.test.add.string")
    String add(String p1, String p2);
}
