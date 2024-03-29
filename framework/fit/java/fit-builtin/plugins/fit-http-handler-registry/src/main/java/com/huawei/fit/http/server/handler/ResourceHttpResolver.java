/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.http.server.handler;

import com.huawei.fitframework.protocol.jar.Jar;
import com.huawei.fitframework.protocol.jar.JarEntryLocation;
import com.huawei.fitframework.protocol.jar.JarLocation;
import com.huawei.fitframework.resource.Resource;
import com.huawei.fitframework.resource.support.ClassLoaderResourceResolver;
import com.huawei.fitframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * 表示资源的 http 请求解析器。
 *
 * @author 邬涨财 w00575064
 * @since 2024-01-18
 */
public class ResourceHttpResolver extends AbstractFileHttpResolver<Resource> {
    private static final String TYPE = "resource";

    @Override
    protected Resource getFile(String actualPath, ClassLoader classLoader) {
        ClassLoaderResourceResolver resourceResolver = new ClassLoaderResourceResolver(classLoader);
        try {
            Resource[] resources = resourceResolver.resolve(actualPath);
            if (resources.length > 0) {
                return resources[0];
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to get resource.", e);
        }
        return null;
    }

    @Override
    protected boolean isFileValid(Resource resource) {
        return resource != null;
    }

    @Override
    protected InputStream getInputStream(Resource resource) throws IOException {
        return resource.read();
    }

    @Override
    protected long getLength(Resource resource, String actualPath, InputStream inputStream) throws IOException {
        URL url = resource.url();
        if (StringUtils.equalsIgnoreCase(url.getProtocol(), JarLocation.JAR_PROTOCOL)) {
            return Jar.from(JarEntryLocation.parse(url).jar()).entries().get(actualPath).sizeOfUncompressed();
        } else if (StringUtils.equalsIgnoreCase(url.getProtocol(), JarLocation.FILE_PROTOCOL)) {
            return inputStream.available();
        } else {
            throw new UnsupportedOperationException(StringUtils.format("Failed to get url length. [url={0}]",
                    url.toExternalForm()));
        }
    }

    @Override
    protected String getFileName(Resource resource) {
        return resource.filename();
    }

    @Override
    protected String getType() {
        return TYPE;
    }
}
