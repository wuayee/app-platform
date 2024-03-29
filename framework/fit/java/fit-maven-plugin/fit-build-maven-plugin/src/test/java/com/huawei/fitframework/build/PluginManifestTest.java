/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 */

package com.huawei.fitframework.build;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.huawei.fitframework.build.plugin.PluginManifest;
import com.huawei.fitframework.util.IoUtils;

import org.apache.maven.plugin.MojoExecutionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@DisplayName("测试 PluginMetadataFile 类")
class PluginManifestTest {
    @Test
    @DisplayName("将正确的元数据信息写入到输出流中")
    void shouldWritePluginMetadata() throws IOException, MojoExecutionException {
        byte[] buffer;
        PluginManifest file = PluginManifest.custom()
                .group("group")
                .name("part1-part2")
                .version("1.0.0")
                .category("user")
                .level("5")
                .build();
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            file.write(out);
            buffer = out.toByteArray();
        }
        String actual = new String(buffer, StandardCharsets.UTF_8);
        String expected = IoUtils.content(PluginManifestTest.class.getClassLoader(), "plugin.xml");
        assertEquals(expected, actual);
    }
}
