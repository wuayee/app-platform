/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

/**
 * AppUtils测试类
 *
 * @author 陈潇文
 * @since 2024-08-26
 */
public class AppUtilsTest {
    @Test
    void testReplaceAsterisks() {
        List<String> excludeNames = Arrays.asList("*name*", "age");
        List<String> results = AppUtils.replaceAsterisks(excludeNames);
        Assertions.assertEquals(results.get(0), "{name}");
        Assertions.assertEquals(results.get(1), "age");
    }
}
