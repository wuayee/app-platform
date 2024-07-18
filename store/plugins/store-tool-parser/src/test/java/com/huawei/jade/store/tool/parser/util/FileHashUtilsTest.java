/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.tool.parser.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.NoSuchFileException;
import java.security.NoSuchAlgorithmException;

/**
 * 表示 {@link FileHashUtils} 的单元测试。
 *
 * @author 杭潇 h00675922
 * @since 2024-07-16
 */
@DisplayName("测试 FileHashUtils 类")
class FileHashUtilsTest {
    @Test
    @DisplayName("给定错误路径的文件，抛出异常")
    void givenInvalidFilePathThenThrowException() {
        String sourceFolderPath = "src/test/resources/tool.json";
        NoSuchFileException noSuchFileException =
                catchThrowableOfType(() -> FileHashUtils.calculateFileHash(sourceFolderPath, "SHA-256"),
                        NoSuchFileException.class);
        assertThat(noSuchFileException.getMessage()).isNotNull();
    }

    @Test
    @DisplayName("给定正确路径的文件，不支持的算法，抛出异常")
    void givenValidFileAndInvalidAlgorithmThenThrowException() {
        String sourceFolderPath = "src/test/resources/tools.json";
        NoSuchAlgorithmException noSuchAlgorithmException =
                catchThrowableOfType(() -> FileHashUtils.calculateFileHash(sourceFolderPath, "sha-100"),
                        NoSuchAlgorithmException.class);
        assertThat(noSuchAlgorithmException.getMessage()).isEqualTo("sha-100 MessageDigest not available");
    }
}