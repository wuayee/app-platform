/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package com.huawei.jade.store.tool.parser;

import static com.huawei.jade.store.tool.parser.support.FileParser.parseToolSchema;
import static org.assertj.core.api.Assertions.assertThat;

import com.huawei.fitframework.util.FileUtils;
import com.huawei.fitframework.util.support.Zip;
import com.huawei.jade.store.tool.parser.entity.MethodEntity;
import com.huawei.jade.store.tool.parser.entity.ParameterEntity;
import com.huawei.jade.store.tool.parser.support.FileParser;

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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 表示 {@link FileParser} 的单元测试。
 *
 * @author 杭潇 h00675922
 * @since 2024-06-15
 */
@DisplayName("测试 ParseFileByPath 类")
public class FileParserTest {
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
            List<MethodEntity> list = parseToolSchema(this.targetZipFilePath);
            assertThat(list.size()).isEqualTo(3);

            assertThat(list.get(0).getMethodName()).isEqualTo("add list");
            assertThat(list.get(1).getMethodName()).isEqualTo("add itself");
            assertThat(list.get(2).getMethodName()).isEqualTo("name for tool");
            assertThat(list.get(0).getMethodDescription()).isEqualTo("This method adds two list");
            assertThat(list.get(1).getMethodDescription()).isEqualTo("This method adds two integers");
            assertThat(list.get(2).getMethodDescription()).isEqualTo("description for tool.");
            assertThat(list.get(0).getReturnDescription()).isEqualTo("The sum of two list");
            assertThat(list.get(1).getReturnDescription()).isEqualTo("The sum of two integers");
            assertThat(list.get(2).getReturnDescription()).isEqualTo("the ans for test demo.");
            assertThat(list.get(0).getReturnType()).isEqualTo("int");
            assertThat(list.get(1).getReturnType()).isEqualTo("object[]");
            assertThat(list.get(2).getReturnType()).isEqualTo("string");
            assertThat(list.get(0).getParameterEntities().size()).isEqualTo(2);
            assertThat(list.get(1).getParameterEntities().size()).isEqualTo(1);
            assertThat(list.get(2).getParameterEntities().size()).isEqualTo(2);

            Set<String> tags = new HashSet<>(Arrays.asList("FIT", "BUILTIN"));
            assertThat(list.get(0).getTags()).isEqualTo(tags);
            assertThat(list.get(1).getTags()).isEqualTo(tags);
            assertThat(list.get(2).getTags()).isEqualTo(tags);

            ParameterEntity parameterEntity = list.get(1).getParameterEntities().get(0);
            assertThat(parameterEntity.getDescription()).isEqualTo("First integer");
            assertThat(parameterEntity.getName()).isEqualTo("a");
            assertThat(parameterEntity.getType()).isEqualTo("int");

            assertSchema(list);
            assertRunnable(list);
        }

        private void assertRunnable(List<MethodEntity> methodEntities) {
            Set<String> runnableKey = new HashSet<>(Collections.singletonList("FIT"));
            for (MethodEntity methodEntity : methodEntities) {
                assertThat(methodEntity.getRunnablesInfo().keySet()).isEqualTo(runnableKey);
                assertThat(methodEntity.getRunnablesInfo().get("FIT").toString()).isEqualTo(
                        "{fitableId=default_fitable_id, genericableId=com.huawei.fit.test_demo}");
            }
        }

        private void assertSchema(List<MethodEntity> methodEntities) {
            Set<String> schemaKey = new HashSet<>();
            schemaKey.add("name");
            schemaKey.add("description");
            schemaKey.add("parameters");
            schemaKey.add("order");
            schemaKey.add("return");
            for (MethodEntity methodEntity : methodEntities) {
                assertThat(methodEntity.getSchemaInfo().keySet()).isEqualTo(schemaKey);
            }
            assertThat(methodEntities.get(0).getSchemaInfo().get("name").toString()).isEqualTo("add list");
            assertThat(methodEntities.get(1).getSchemaInfo().get("name").toString()).isEqualTo("add itself");
            assertThat(methodEntities.get(2).getSchemaInfo().get("name").toString()).isEqualTo("name for tool");
        }
    }

    @Nested
    @DisplayName("测试 jar 文件的解析")
    class TestJarFile {
        private final String targetJarFilePath = "src/test/resources/testFile.jar";

        @BeforeEach
        void setup() throws IOException {
            File sourceFolder = new File(sourceFolderPath);
            File targetJarFile = new File(targetJarFilePath);

            try (FileOutputStream fos = new FileOutputStream(targetJarFile);
                 ZipOutputStream zos = new ZipOutputStream(fos)) {
                zipFolder(sourceFolder, sourceFolder.getName(), zos);
            }
        }

        @Test
        @DisplayName("解析有效的 jar 文件中的 schema 数据成功")
        void givenValidJarFileThenParsedSchemaSuccessfully() throws IOException {
            List<MethodEntity> list = parseToolSchema(this.targetJarFilePath);
            assertThat(list.size()).isEqualTo(3);

            assertThat(list.get(0).getMethodName()).isEqualTo("add list");
            assertThat(list.get(1).getMethodName()).isEqualTo("add itself");
            assertThat(list.get(2).getMethodName()).isEqualTo("name for tool");
        }

        @AfterEach
        void teardown() {
            FileUtils.delete(this.targetJarFilePath);
        }
    }

    private static void zipFolder(File folder, String parentFolder, ZipOutputStream zos) throws IOException {
        for (File file : Objects.requireNonNull(folder.listFiles())) {
            if (file.isDirectory()) {
                zipFolder(file, parentFolder + "/" + file.getName(), zos);
                continue;
            }
            zos.putNextEntry(new ZipEntry(parentFolder + "/" + file.getName()));

            try (FileInputStream fis = new FileInputStream(file)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = fis.read(buffer)) >= 0) {
                    zos.write(buffer, 0, length);
                }
                zos.closeEntry();
            }
        }
    }
}