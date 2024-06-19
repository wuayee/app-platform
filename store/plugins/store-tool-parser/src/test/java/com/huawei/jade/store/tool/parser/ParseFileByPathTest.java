/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.tool.parser;

import static com.huawei.jade.store.tool.parser.utils.ParseFileByPath.getRunnableInfo;
import static com.huawei.jade.store.tool.parser.utils.ParseFileByPath.getSchemaInfo;
import static com.huawei.jade.store.tool.parser.utils.ParseFileByPath.parseToolsJsonSchema;
import static org.assertj.core.api.Assertions.assertThat;

import com.huawei.fitframework.util.support.Zip;
import com.huawei.jade.store.tool.parser.entity.MethodEntity;
import com.huawei.jade.store.tool.parser.entity.ParameterEntity;
import com.huawei.jade.store.tool.parser.utils.ParseFileByPath;

import org.apache.maven.surefire.shared.compress.archivers.tar.TarArchiveEntry;
import org.apache.maven.surefire.shared.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.maven.surefire.shared.compress.utils.IOUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 表示 {@link ParseFileByPath} 的单元测试。
 *
 * @author 杭潇 h00675922
 * @since 2024-06-15
 */
@DisplayName("测试 ParseFileByPath 类")
public class ParseFileByPathTest {
    private final String sourceFolderPath = "src/test/resources/FIT-INF";

    @Nested
    @DisplayName("测试 zip 文件的解析")
    class TestZipFile {
        private final String targetZipFilePath = "src/test/resources/testFile.zip";

        @BeforeEach
        void setup() throws IOException {
            File sourceFolder = new File(sourceFolderPath);
            File zipFile = new File(this.targetZipFilePath);
            zipFile.deleteOnExit();
            Zip zip = new Zip(zipFile, StandardCharsets.UTF_8).override(true).add(sourceFolder);
            zip.start();
        }

        @Test
        @DisplayName("解析有效的 zip 文件中的 schema 数据成功")
        void givenValidZipFileThenParsedSchemaSuccessfully() throws IOException {
            List<MethodEntity> list = parseToolsJsonSchema(this.targetZipFilePath);
            assertThat(list.size()).isEqualTo(3);

            assertThat(list.get(0).getMethodName()).isEqualTo("链表加法");
            assertThat(list.get(1).getMethodName()).isEqualTo("自己相加");
            assertThat(list.get(2).getMethodName()).isEqualTo("财经问题结果生成");
            assertThat(list.get(0).getMethodDescription()).isEqualTo("This method adds two list");
            assertThat(list.get(1).getMethodDescription()).isEqualTo("This method adds two integers");
            assertThat(list.get(2).getMethodDescription()).isEqualTo("这是一个用于生成财经类问题结果的工具。");
            assertThat(list.get(0).getReturnDescription()).isEqualTo("The sum of two list");
            assertThat(list.get(1).getReturnDescription()).isEqualTo("The sum of two integers");
            assertThat(list.get(2).getReturnDescription()).isEqualTo("生成财经类问题结果");
            assertThat(list.get(0).getReturnType()).isEqualTo("int");
            assertThat(list.get(1).getReturnType()).isEqualTo("object[]");
            assertThat(list.get(2).getReturnType()).isEqualTo("string");
            assertThat(list.get(0).getParameterEntities().size()).isEqualTo(2);
            assertThat(list.get(1).getParameterEntities().size()).isEqualTo(1);
            assertThat(list.get(2).getParameterEntities().size()).isEqualTo(2);

            ParameterEntity parameterEntity = list.get(1).getParameterEntities().get(0);
            assertThat(parameterEntity.getDescription()).isEqualTo("First integer");
            assertThat(parameterEntity.getName()).isEqualTo("a");
            assertThat(parameterEntity.getType()).isEqualTo("int");
        }

        @Test
        @DisplayName("解析有效的 zip 文件中的 schema 数据成功")
        void givenValidZipFileThenParsedRunnableSuccessfully() throws IOException {
            List<Map<String, Object>> runnableInfo = getRunnableInfo(this.targetZipFilePath);
            assertThat(runnableInfo.size()).isEqualTo(3);

            Set<String> runnableKey = new HashSet<>();
            runnableKey.add("FIT");
            for (Map<String, Object> runnable : runnableInfo) {
                assertThat(runnable.keySet()).isEqualTo(runnableKey);
                assertThat(runnable.get("FIT").toString()).isEqualTo(
                        "{fitableId=default, genericableId=com.huawei.fit.jober.aipp.tool.create.app}");
            }
        }

        @Test
        @DisplayName("解析有效的 zip 文件中的所有 schema json 数据成功")
        void givenValidZipFileThenParsedSchemaJsonSuccessfully() throws IOException {
            List<Map<String, Object>> schemaInfo = getSchemaInfo(this.targetZipFilePath);
            assertThat(schemaInfo.size()).isEqualTo(3);
            Set<String> schemaKey = new HashSet<>();
            schemaKey.add("name");
            schemaKey.add("description");
            schemaKey.add("parameters");
            schemaKey.add("order");
            schemaKey.add("return");
            for (Map<String, Object> schema : schemaInfo) {
                assertThat(schema.keySet()).isEqualTo(schemaKey);
            }
            assertThat(schemaInfo.get(0).get("name").toString()).isEqualTo("链表加法");
            assertThat(schemaInfo.get(1).get("name").toString()).isEqualTo("自己相加");
            assertThat(schemaInfo.get(2).get("name").toString()).isEqualTo("财经问题结果生成");
        }
    }

    @Nested
    @DisplayName("测试 tar 文件的解析")
    class TestTarFile {
        private final String targetTarFilePath = "src/test/resources/testFile.tar";

        @BeforeEach
        void setup() throws IOException {
            File sourceFolder = new File(sourceFolderPath);
            File targetTarFile = new File(this.targetTarFilePath);
            try (FileOutputStream fos = new FileOutputStream(targetTarFile);
                 TarArchiveOutputStream tarOut = new TarArchiveOutputStream(fos)) {
                addFilesToTar(sourceFolder, tarOut, "");
            }
        }

        @Test
        @DisplayName("解析有效的 tar 文件中的 schema 数据成功")
        void givenValidTarFileThenParsedSchemaSuccessfully() throws IOException {
            List<MethodEntity> list = parseToolsJsonSchema(this.targetTarFilePath);
            assertThat(list.size()).isEqualTo(3);

            assertThat(list.get(0).getMethodName()).isEqualTo("链表加法");
            assertThat(list.get(1).getMethodName()).isEqualTo("自己相加");
            assertThat(list.get(2).getMethodName()).isEqualTo("财经问题结果生成");
        }

        @AfterEach
        void teardown() {
            deleteFile(this.targetTarFilePath);
        }
    }

    @Nested
    @DisplayName("测试 jar 文件的解析")
    class TestJarFile {
        private final String targetJarFilePath = "src/test/resources/testFile.jar";

        @BeforeEach
        void setup() throws IOException {
            File sourceFolder = new File(sourceFolderPath);
            File targetTarFile = new File(this.targetJarFilePath);
            try (FileOutputStream fos = new FileOutputStream(targetTarFile);
                 ZipOutputStream jarOut = new ZipOutputStream(fos)) {
                addFilesToJar(sourceFolder, jarOut, "");
            }
        }

        @Test
        @DisplayName("解析有效的 jar 文件中的 schema 数据成功")
        void givenValidJarFileThenParsedSchemaSuccessfully() throws IOException {
            List<MethodEntity> list = parseToolsJsonSchema(this.targetJarFilePath);
            assertThat(list.size()).isEqualTo(3);

            assertThat(list.get(0).getMethodName()).isEqualTo("链表加法");
            assertThat(list.get(1).getMethodName()).isEqualTo("自己相加");
            assertThat(list.get(2).getMethodName()).isEqualTo("财经问题结果生成");
        }

        @AfterEach
        void teardown() {
            deleteFile(this.targetJarFilePath);
        }
    }

    private static void addFilesToTar(File file, TarArchiveOutputStream tarOut, String parent) throws IOException {
        String entryName = parent + file.getName();
        tarOut.putArchiveEntry(new TarArchiveEntry(file, entryName));

        if (file.isFile()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                IOUtils.copy(fis, tarOut);
            }
            tarOut.closeArchiveEntry();
        } else if (file.isDirectory()) {
            tarOut.closeArchiveEntry();
            for (File childFile : Objects.requireNonNull(file.listFiles())) {
                addFilesToTar(childFile, tarOut, entryName + "/");
            }
        }
    }

    private static void addFilesToJar(File file, ZipOutputStream jarOut, String parent) throws IOException {
        String entryName = parent + file.getName();
        jarOut.putNextEntry(new ZipEntry(entryName));

        if (file.isFile()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                IOUtils.copy(fis, jarOut);
            }
            jarOut.closeEntry();
        } else if (file.isDirectory()) {
            jarOut.closeEntry();
            for (File childFile : Objects.requireNonNull(file.listFiles())) {
                addFilesToJar(childFile, jarOut, entryName + "/");
            }
        }
    }

    private void deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.exists() && !file.delete()) {
            throw new IllegalStateException(String.format("Failed to delete file %s .", filePath));
        }
    }
}