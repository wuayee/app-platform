/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.jober.health;

/**
 * 测试服务。
 *
 * @author 季聿阶 j00559309
 * @since 2023-06-06
 */
public interface HelloService {
    /**
     * 测试 Genericable。
     *
     * @param name 表示测试参数的 {@link String}。
     * @return 表示测试返回值。
     */
    String hi(String name);
}
