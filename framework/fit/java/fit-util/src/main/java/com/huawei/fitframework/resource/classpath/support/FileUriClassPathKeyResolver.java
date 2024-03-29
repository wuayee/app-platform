/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2020. All rights reserved.
 */

package com.huawei.fitframework.resource.classpath.support;

import com.huawei.fitframework.resource.classpath.ClassPathKey;
import com.huawei.fitframework.resource.classpath.UriClassPathKeyResolver;
import com.huawei.fitframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;

/**
 * 为 {@link UriClassPathKeyResolver} 提供基于文件的实现。
 *
 * @author 梁济时 l00815032
 * @since 2022-07-27
 */
public class FileUriClassPathKeyResolver implements UriClassPathKeyResolver {
    /**
     * 获取 {@link FileUriClassPathKeyResolver} 的唯一实例。
     */
    public static final FileUriClassPathKeyResolver INSTANCE = new FileUriClassPathKeyResolver();

    private FileUriClassPathKeyResolver() {}

    @Override
    public Optional<ClassPathKey> resolve(URI uri) throws IOException {
        if (StringUtils.equalsIgnoreCase(uri.getScheme(), "file")) {
            File file = new File(uri.getSchemeSpecificPart());
            return Optional.of(new FileClassPathKey(file));
        } else {
            return Optional.empty();
        }
    }
}
