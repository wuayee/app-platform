/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fitframework.resource.support;

import com.huawei.fitframework.io.InputStreamSupplier;
import com.huawei.fitframework.resource.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * 为 {@link Resource} 提供默认实现。
 *
 * @author 梁济时
 * @since 2023-01-12
 */
public final class DefaultResource implements Resource {
    private final String name;
    private final URL url;
    private final InputStreamSupplier inputStreamSupplier;

    public DefaultResource(String name, URL url, InputStreamSupplier inputStreamSupplier) {
        this.name = name;
        this.url = url;
        this.inputStreamSupplier = inputStreamSupplier;
    }

    @Override
    public String filename() {
        return this.name;
    }

    @Override
    public URL url() {
        return this.url;
    }

    @Override
    public InputStream read() throws IOException {
        return this.inputStreamSupplier.get();
    }

    @Override
    public String toString() {
        return this.url.toExternalForm();
    }
}
