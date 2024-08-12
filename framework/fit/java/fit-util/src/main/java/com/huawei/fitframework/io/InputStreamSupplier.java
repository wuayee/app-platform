/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * 为 {@link InputStream} 提供供应程序。
 *
 * @author 梁济时
 * @since 2023-01-12
 */
@FunctionalInterface
public interface InputStreamSupplier {
    /**
     * 获取输入流实例。
     *
     * @return 表示输入流实例的 {@link InputStream}。
     * @throws IOException 打开输入流过程发生输入输出异常。
     */
    InputStream get() throws IOException;
}
