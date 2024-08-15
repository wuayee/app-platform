/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.plugin.dynamic.mvn.support;

import static com.huawei.fitframework.inspection.Validation.isTrue;
import static com.huawei.fitframework.inspection.Validation.notBlank;
import static com.huawei.fitframework.inspection.Validation.notNull;

import com.huawei.fit.http.client.HttpClassicClientFactory;
import com.huawei.fit.http.client.HttpClassicClientRequest;
import com.huawei.fit.http.client.HttpClassicClientResponse;
import com.huawei.fit.http.protocol.HttpRequestMethod;
import com.huawei.fit.plugin.dynamic.mvn.MavenArtifactDownloader;
import com.huawei.fitframework.annotation.Component;
import com.huawei.fitframework.annotation.Value;
import com.huawei.fitframework.protocol.jar.Jar;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

/**
 * 表示 {@link MavenArtifactDownloader} 的默认实现。
 *
 * @author 季聿阶
 * @since 2023-09-17
 */
@Component
public class DefaultMavenArtifactDownloader implements MavenArtifactDownloader {
    private static final char SEPARATOR_URL = '/';

    private final HttpClassicClientFactory factory;
    private final String url;
    private final File root;

    public DefaultMavenArtifactDownloader(HttpClassicClientFactory factory, @Value("${repository-url}") String url,
            @Value("${directory}") String directory) {
        this.factory = notNull(factory, "The http classic client factory cannot be null.");
        notBlank(url, "The maven repository url cannot be blank. [config='plugin.fit.dynamic.plugin.repository-url']");
        if (url.endsWith(String.valueOf(SEPARATOR_URL))) {
            this.url = url;
        } else {
            this.url = url + SEPARATOR_URL;
        }
        this.root = new File(notBlank(directory,
                "The directory to monitor cannot be blank. [config='plugin.fit.dynamic.plugin.directory']"));
        isTrue(this.root.isDirectory(), "The directory to monitor must be a directory. [directory={0}]", directory);
    }

    @Override
    public File download(String groupId, String artifactId, String version) throws IOException {
        String actualUrl =
                this.url + groupId.replace('.', SEPARATOR_URL) + SEPARATOR_URL + artifactId + SEPARATOR_URL + version
                        + SEPARATOR_URL + artifactId + "-" + version + Jar.FILE_EXTENSION;
        HttpClassicClientRequest request = this.factory.create().createRequest(HttpRequestMethod.GET, actualUrl);
        HttpClassicClientResponse<Object> exchange = request.exchange();
        File downloaded = new File(this.root, artifactId + "-" + version);
        try (OutputStream out = Files.newOutputStream(downloaded.toPath())) {
            byte[] bytes = exchange.entityBytes();
            out.write(bytes);
            out.flush();
        }
        return downloaded;
    }
}


