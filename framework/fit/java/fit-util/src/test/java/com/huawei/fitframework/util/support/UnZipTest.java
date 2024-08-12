/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2022-2022. All rights reserved.
 */

package com.huawei.fitframework.util.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

import com.huawei.fitframework.merge.ConflictResolutionPolicy;
import com.huawei.fitframework.util.FileUtils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * {@link Unzip} 的单元测试。
 *
 * @author 季聿阶
 * @since 2022-02-02
 */
public class UnZipTest {
    private File zipFile;
    private File unzipToFile;

    @BeforeEach
    void setup() throws IOException {
        this.zipFile = new File("src/test/resources/unzip-tmp.zip");
        Zip zip = new Zip(this.zipFile, null).override(true).add(new File("src/test/resources/zip"));
        zip.start();

        this.unzipToFile = new File("src/test/resources/unzip-tmp");
    }

    @AfterEach
    void teardown() {
        FileUtils.delete(this.unzipToFile);
        FileUtils.delete(this.zipFile);
    }

    @Nested
    @DisplayName("Given expected scenario")
    class GivenExpectedScenario {
        @Test
        @DisplayName("Given zipped file then unzip correctly")
        void givenZippedFileThenUnzipCorrectly() {
            Unzip unzip = new Unzip(UnZipTest.this.zipFile, null).target(UnZipTest.this.unzipToFile)
                    .filter(entry -> !entry.getName().endsWith(".out"))
                    .secure(Unzip.Security.DEFAULT);
            assertThatNoException().isThrownBy(unzip::start);
            List<String> allSubFileNames = FileUtils.traverse(UnZipTest.this.unzipToFile)
                    .map(File::getName)
                    .collect(Collectors.toList());
            assertThat(allSubFileNames).contains("file1.txt", "file2.txt", "file3.txt", "file4.txt")
                    .doesNotContain("file5.out");
        }

        @Test
        @DisplayName("Given zipped file and redirector then unzip correctly")
        void givenZippedFileAndRedirectorThenUnzipCorrectly() {
            Unzip unzip = new Unzip(UnZipTest.this.zipFile, null).target(UnZipTest.this.unzipToFile).redirect(entry -> {
                if (entry.getName().endsWith(".txt")) {
                    return Unzip.Redirect.unredirected();
                } else if (entry.getName().endsWith(".out")) {
                    File absoluteFile = FileUtils.canonicalize(UnZipTest.this.unzipToFile);
                    return Unzip.Redirect.redirected(
                            new File(absoluteFile.getPath() + "/" + entry.getName() + ".redirect"));
                } else {
                    return Unzip.Redirect.unredirected();
                }
            });
            assertThatNoException().isThrownBy(unzip::start);
            List<String> allSubFileNames = FileUtils.traverse(UnZipTest.this.unzipToFile)
                    .map(File::getName)
                    .collect(Collectors.toList());
            assertThat(allSubFileNames).contains("file1.txt", "file2.txt", "file3.txt", "file4.txt",
                    "file5.out.redirect");
        }

        @Test
        @DisplayName("Given unzipped directory is exist file and allowed override then unzip correctly")
        void givenUnzippedDirectoryIsExistFileAndNotAllowedOverrideThenUnzipCorrectly() throws IOException {
            Unzip unzip = new Unzip(UnZipTest.this.zipFile, null).target(UnZipTest.this.unzipToFile)
                    .override(true)
                    .resolveConflict(ConflictResolutionPolicy.OVERRIDE);
            File existFile = new File(UnZipTest.this.unzipToFile, "zip");
            FileUtils.ensureDirectory(UnZipTest.this.unzipToFile);
            boolean created = existFile.createNewFile();
            assertThat(created).isTrue();
            assertThatNoException().isThrownBy(unzip::start);
            List<String> allSubFileNames = FileUtils.traverse(UnZipTest.this.unzipToFile)
                    .map(File::getName)
                    .collect(Collectors.toList());
            assertThat(allSubFileNames).contains("file1.txt", "file2.txt", "file3.txt", "file4.txt", "file5.out");
        }

        @Test
        @DisplayName("Given unzip twice and allowed override then unzip correctly")
        void givenUnzipTwiceAndAllowedOverrideThenUnzipCorrectly() throws IOException {
            Unzip unzip = new Unzip(UnZipTest.this.zipFile, null).target(UnZipTest.this.unzipToFile)
                    .override(true)
                    .resolveConflict(ConflictResolutionPolicy.OVERRIDE);
            unzip.start();
            assertThatNoException().isThrownBy(unzip::start);
            List<String> allSubFileNames = FileUtils.traverse(UnZipTest.this.unzipToFile)
                    .map(File::getName)
                    .collect(Collectors.toList());
            assertThat(allSubFileNames).contains("file1.txt", "file2.txt", "file3.txt", "file4.txt", "file5.out");
        }
    }

    @Nested
    @DisplayName("Given exception scenario")
    class GivenExceptionScenario {
        @Test
        @DisplayName("Given unzip twice and default abort conflict policy then throw IOException")
        void givenUnzipTwiceAndDefaultAbortConflictPolicyThenThrowException() throws IOException {
            Unzip unzip = new Unzip(UnZipTest.this.zipFile, null).target(UnZipTest.this.unzipToFile)
                    .resolveConflict(ConflictResolutionPolicy.ABORT);
            unzip.start();
            IOException exception = catchThrowableOfType(unzip::start, IOException.class);
            assertThat(exception).isNotNull().hasMessage("File already exists. Cannot unzip entry. [entry=zip/]");
        }

        @Test
        @DisplayName("Given unzip twice and custom conflict policy then throw IOException")
        void givenUnzipTwiceAndCustomConflictPolicyThenThrowException() throws IOException {
            Unzip unzip = new Unzip(UnZipTest.this.zipFile, null).target(UnZipTest.this.unzipToFile)
                    .resolveConflict(ConflictResolutionPolicy.ABORT)
                    .resolveEntryConflict(conflict -> {
                        if (conflict.getEntry().getName().endsWith(".txt")) {
                            return ConflictResolutionPolicy.SKIP;
                        } else if (conflict.getTarget().getName().endsWith(".out")) {
                            return null;
                        } else {
                            return ConflictResolutionPolicy.OVERRIDE;
                        }
                    });
            unzip.start();
            IOException exception = catchThrowableOfType(unzip::start, IOException.class);
            assertThat(exception).isNotNull()
                    .hasMessage("File already exists. Cannot unzip entry. [entry=zip/folder1/file5.out]");
        }

        @Test
        @DisplayName("Given unzip twice and not allowed override then throw IOException")
        void givenUnzipTwiceAndNotAllowedOverrideThenThrowException() throws IOException {
            File singleZipFile = new File("src/test/resources/single-unzip-tmp.zip");
            Zip singZip = new Zip(singleZipFile, null).override(true).add(new File("src/test/resources/single-zip"));
            singZip.start();
            File singleUnzipToFile = new File("src/test/resources/single-unzip-tmp");
            Unzip unzip = new Unzip(singleZipFile, null).target(singleUnzipToFile)
                    .override(false)
                    .resolveConflict(ConflictResolutionPolicy.OVERRIDE);
            unzip.start();
            IOException exception = catchThrowableOfType(unzip::start, IOException.class);
            assertThat(exception).isNotNull().hasMessage("File already exists. Cannot unzip entry. [name=file1.txt]");
            FileUtils.delete(singleUnzipToFile);
            FileUtils.delete(singleZipFile);
        }

        @Test
        @DisplayName("Given max 1 entry security then throw SecurityException")
        void givenMax1EntrySecurityThenThrowException() {
            Unzip unzip = new Unzip(UnZipTest.this.zipFile, null).target(UnZipTest.this.unzipToFile)
                    .secure(new Unzip.Security(100, 1));
            SecurityException exception = catchThrowableOfType(unzip::start, SecurityException.class);
            assertThat(exception).isNotNull()
                    .hasMessage("The file to unzip contains too many entries. [file=unzip-tmp.zip, max=1]");
        }

        @Test
        @DisplayName("Given max 1 byte security then throw SecurityException")
        void givenMax1ByteSecurityThenThrowException() {
            Unzip unzip = new Unzip(UnZipTest.this.zipFile, null).target(UnZipTest.this.unzipToFile)
                    .secure(new Unzip.Security(1, 1024));
            SecurityException exception = catchThrowableOfType(unzip::start, SecurityException.class);
            assertThat(exception).isNotNull().hasMessage("The file to unzip is too large. [file=unzip-tmp.zip, max=1]");
        }

        @Test
        @DisplayName("Given unzipped directory is exist file and not allowed override then throw IOException")
        void givenUnzippedDirectoryIsExistFileAndNotAllowedOverrideThenThrowException() throws IOException {
            Unzip unzip = new Unzip(UnZipTest.this.zipFile, null).target(UnZipTest.this.unzipToFile)
                    .override(false)
                    .resolveConflict(ConflictResolutionPolicy.OVERRIDE);
            File existFile = new File(UnZipTest.this.unzipToFile, "zip");
            FileUtils.ensureDirectory(UnZipTest.this.unzipToFile);
            boolean created = existFile.createNewFile();
            assertThat(created).isTrue();
            IOException exception = catchThrowableOfType(unzip::start, IOException.class);
            assertThat(exception).isNotNull().hasMessage("File already exists. Cannot create directory. [name=zip]");
        }
    }
}
