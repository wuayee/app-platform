/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.fit.jober.aipp.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import com.huawei.fitframework.util.FileUtils;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.File;

/**
 * AippFileUtils测试类
 *
 * @since 2024-07-31
 */
public class AippFileUtilsTest {
    @Test
    void testDeleteFile() {
        File mockFile = mock(File.class);
        try (MockedStatic<FileUtils> mockedStatic = mockStatic(FileUtils.class)) {
            mockedStatic.when(() -> FileUtils.delete(mockFile)).thenThrow(new IllegalStateException("delete failed"));
            AippFileUtils.deleteFile(mockFile);
        }
    }

    @Test
    void testGetFilepath() {
        String endPoint = "http://localhost";
        String pathPrefix = "/test";
        String filePath = "/test";
        String fileName = "test";
        assertEquals("http://localhost/test/v1/api/31f20efc7e0848deab6a6bc10fc3021e/file?filePath=/test&fileName=test",
                AippFileUtils.getFileDownloadUrl(endPoint, pathPrefix, filePath, fileName));
        assertEquals("http://localhost/test/v1/api/31f20efc7e0848deab6a6bc10fc3021e/file?filePath=/test",
                AippFileUtils.getFileDownloadFilePath(endPoint, pathPrefix, filePath));
    }
}
