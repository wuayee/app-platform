/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2023. All rights reserved.
 */

package com.huawei.fitframework.build;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.huawei.fitframework.build.service.ErrorManifest;
import com.huawei.fitframework.util.IoUtils;
import com.huawei.fitframework.util.MapBuilder;

import org.apache.maven.plugin.MojoExecutionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 表示 {@link ErrorManifest} 的单元测试。
 *
 * @author 季聿阶 j00559309
 * @since 2023-06-18
 */
@DisplayName("测试 ErrorMetadata 类")
class ErrorManifestTest {
    @Test
    @DisplayName("将正确的元数据信息写入到输出流中")
    void shouldWriteErrorMetadata() throws IOException, MojoExecutionException {
        byte[] buffer;
        ErrorManifest file =
                new ErrorManifest(MapBuilder.<String, Integer>get().put("com.huawei.SampleException", 1).build());
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            file.write(out);
            buffer = out.toByteArray();
        }
        String actual = new String(buffer, StandardCharsets.UTF_8);
        String expected = IoUtils.content(ErrorManifestTest.class.getClassLoader(), "errors.xml");
        assertEquals(expected, actual);
    }
}
