/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2024. All rights reserved.
 */

package com.huawei.fitframework.io.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.UUID;

@DisplayName("测试 FileRandomAccessor 类")
class FileRandomAccessorTest {
    private static final String FILE_NAME_PREFIX = "RandomAccess-";
    private static final String FILE_NAME_SUFFIX = ".test";

    private static File randomAccessorFile;

    @BeforeAll
    static void setup() throws IOException {
        randomAccessorFile = Files.createTempFile(FILE_NAME_PREFIX, FILE_NAME_SUFFIX).toFile();
        try (OutputStream out = new FileOutputStream(randomAccessorFile)) {
            for (int i = 0; i < 128; i++) {
                out.write(i);
            }
        }
    }

    @AfterAll
    static void teardown() throws IOException {
        Files.delete(randomAccessorFile.toPath());
    }

    @Nested
    @DisplayName("构造实例")
    class Construct {
        @Test
        @DisplayName("当文件为 null 时抛出异常")
        void should_throw_when_file_is_null() {
            IllegalArgumentException exception =
                    assertThrows(IllegalArgumentException.class, () -> new FileRandomAccessor(null));
            assertEquals("The file to access cannot be null.", exception.getMessage());
        }

        @Test
        @DisplayName("当文件不能被标准化时抛出异常")
        void should_throw_when_file_is_not_canonical() {
            File file = new File("\u0000");
            IllegalArgumentException exception =
                    assertThrows(IllegalArgumentException.class, () -> new FileRandomAccessor(file));
            String expectedError = "The file to access is not canonical.";
            String actualError = exception.getMessage();
            assertTrue(actualError.length() > expectedError.length());
            assertEquals(expectedError, actualError.substring(0, expectedError.length()));
        }

        @Test
        @DisplayName("当文件不存在时抛出异常")
        void should_throw_when_file_not_exists() {
            File file = new File(UUID.randomUUID().toString());
            while (file.exists()) {
                file = new File(UUID.randomUUID().toString());
            }
            File nonExist = file;
            IllegalArgumentException exception =
                    assertThrows(IllegalArgumentException.class, () -> new FileRandomAccessor(nonExist));
            String expectedError = "The file to access does not exist.";
            String actualError = exception.getMessage();
            assertTrue(actualError.length() > expectedError.length());
            assertEquals(expectedError, actualError.substring(0, expectedError.length()));
        }
    }

    @Nested
    @DisplayName("读取文件内容")
    class Read {
        private FileRandomAccessor access;

        @BeforeEach
        void setup() {
            this.access = new FileRandomAccessor(randomAccessorFile);
        }

        @AfterEach
        void teardown() throws IOException {
            this.access.close();
        }

        @Test
        @DisplayName("应读取到文件中指定位置的数据")
        void should_return_data_at_expected_position() throws IOException {
            byte[] buffer = this.access.read(10, 2);
            assertEquals(2, buffer.length);
            assertEquals(10, Byte.toUnsignedInt(buffer[0]));
            assertEquals(11, Byte.toUnsignedInt(buffer[1]));
        }

        @Test
        @DisplayName("当 offset 为负数时抛出异常")
        void should_throw_when_offset_is_negative() {
            IllegalArgumentException exception =
                    assertThrows(IllegalArgumentException.class, () -> this.access.read(-1, 10));
            assertEquals("The offset of data to access is out of bound. [offset=-1, total=128]",
                    exception.getMessage());
        }

        @Test
        @DisplayName("当 offset 超出文件大小时抛出异常")
        void should_throw_when_offset_is_greater_than_size() {
            IllegalArgumentException exception =
                    assertThrows(IllegalArgumentException.class, () -> this.access.read(129, 0));
            assertEquals("The offset of data to access is out of bound. [offset=129, total=128]",
                    exception.getMessage());
        }

        @Test
        @DisplayName("当 length 为负数时抛出异常")
        void should_throw_when_length_is_negative() {
            IllegalArgumentException exception =
                    assertThrows(IllegalArgumentException.class, () -> this.access.read(0, -1));
            assertEquals("The length of data to access is out of bounds. [length=-1, offset=0, total=128]",
                    exception.getMessage());
        }

        @Test
        @DisplayName("当 offset 与 length 的和大于 size 时抛出异常")
        void should_throw_when_sum_of_offset_and_length_is_greater_than_size() {
            IllegalArgumentException exception =
                    assertThrows(IllegalArgumentException.class, () -> this.access.read(0, 129));
            assertEquals("The length of data to access is out of bounds. [length=129, offset=0, total=128]",
                    exception.getMessage());
        }
    }

    @Test
    @DisplayName("当文件相同时，哈希值也相同")
    void should_return_same_hash_code_when_contains_same_data() throws IOException {
        try (FileRandomAccessor access1 = new FileRandomAccessor(randomAccessorFile);
             FileRandomAccessor access2 = new FileRandomAccessor(randomAccessorFile)) {
            assertEquals(access1.hashCode(), access2.hashCode());
        }
    }

    @Test
    @DisplayName("当比较同一个访问程序时，返回 true")
    void should_return_true_when_equals_with_itself() throws IOException {
        try (FileRandomAccessor access = new FileRandomAccessor(randomAccessorFile)) {
            assertEquals(access, access);
        }
    }

    @Test
    @DisplayName("当文件相同时，哈希值也相同")
    void should_return_true_when_contains_same_data() throws IOException {
        try (FileRandomAccessor access1 = new FileRandomAccessor(randomAccessorFile);
             FileRandomAccessor access2 = new FileRandomAccessor(randomAccessorFile)) {
            assertEquals(access1, access2);
        }
    }

    @Test
    @DisplayName("当与 null 比较时，返回 false")
    void should_return_false_when_equals_with_null() throws IOException {
        try (FileRandomAccessor access = new FileRandomAccessor(randomAccessorFile)) {
            assertNotEquals(access, null);
        }
    }

    @Test
    @DisplayName("toString 方法应返回友好的信息")
    void should_return_friendly_message_from_to_string() throws IOException {
        try (FileRandomAccessor access = new FileRandomAccessor(randomAccessorFile)) {
            String expected = randomAccessorFile.getCanonicalPath();
            String actual = access.toString();
            assertEquals(expected, actual);
        }
    }
}
